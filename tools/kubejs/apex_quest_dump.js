// priority: -1000
// Apex Ascension: one-off dump for building the quest book, read from the live recipe manager after every script
// has run. Writes kubejs/apex_quest_recipes.json and kubejs/apex_quest_items.json. Reads only; changes nothing.

ServerEvents.loaded(event => {
  const ItemCap = Java.loadClass('com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability').CAP
  const FluidCap = Java.loadClass('com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability').CAP
  const RecipeHelper = Java.loadClass('com.gregtechceu.gtceu.api.recipe.RecipeHelper')
  const GTRecipe = Java.loadClass('com.gregtechceu.gtceu.api.recipe.GTRecipe')
  const FR = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
  let server = event.server, access = server.registryAccess()
  let idsOf = ing => {
    let out = []
    try { ing.getItemIds().forEach(x => { if (out.length < 40) out.push(String(x)) }) } catch (e) {}
    return out
  }
  let fluidsOf = ing => {
    let out = []
    try {
      let st = ing.getStacks()
      for (let k = 0; k < st.length && k < 10; k++) out.push(String(FR.FLUIDS.getKey(st[k].getFluid())))
    } catch (e) {}
    return out
  }
  let recipes = [], failed = 0, n = 0
  server.getRecipeManager().getRecipes().forEach(r => {
    n++
    try {
      let rec = { id: String(r.getId()), t: String(r.getType()), i: [], o: [], fi: [], fo: [], tier: 0, d: 0 }
      if (r instanceof GTRecipe) {
        let ins = r.inputs.get(ItemCap), outs = r.outputs.get(ItemCap)
        if (ins) ins.forEach(c => { let a = idsOf(c.content); if (a.length) rec.i.push(a) })
        if (outs) outs.forEach(c => idsOf(c.content).slice(0, 1).forEach(x => rec.o.push(x)))
        let fins = r.inputs.get(FluidCap), fouts = r.outputs.get(FluidCap)
        if (fins) fins.forEach(c => fluidsOf(c.content).slice(0, 1).forEach(x => rec.fi.push(x)))
        if (fouts) fouts.forEach(c => fluidsOf(c.content).slice(0, 1).forEach(x => rec.fo.push(x)))
        try { rec.t = String(r.recipeType.registryName) } catch (e) {}
        try { rec.tier = Number(RecipeHelper.getRecipeEUtTier(r)) } catch (e) {}
        rec.d = r.duration
      } else {
        try {
          let res = r.getResultItem(access)
          if (res && !res.isEmpty()) rec.o.push(String(FR.ITEMS.getKey(res.getItem())))
        } catch (e) {}
        try { r.getIngredients().forEach(ing => { let a = idsOf(ing); if (a.length) rec.i.push(a) }) } catch (e) {}
      }
      if (rec.o.length || rec.fo.length) recipes.push(rec)
    } catch (e) { failed++ }
  })
  JsonIO.write('kubejs/apex_quest_recipes.json', { recipes: recipes })
  let items = []
  Ingredient.all.getItemIds().forEach(id => {
    let name = String(id)
    try { name = String(Item.of(id).getHoverName().getString()) } catch (e) {}
    items.push([String(id), name])
  })
  JsonIO.write('kubejs/apex_quest_items.json', { items: items })
  console.info(`[APEX-QUESTDUMP] ${n} recipes in the manager, wrote ${recipes.length}, ${failed} failed, ${items.length} items`)
})
