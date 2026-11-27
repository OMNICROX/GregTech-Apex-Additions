package com.omnicrox.gtaa.common.recipe;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IParallelHatch;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.item.IGTTool;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.omnicrox.apexlib.multiblock.ApexHatches;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Matter Fabricator (GT-Not-Leisure "MatterFabricator"): any ingot or gem (a storage block counts as 9) becomes
 * 640 AE2 matter balls with circuit 1, or 2000 mB of UU Amplifier with circuit 2. Up to 8 units a run (a Parallel Hatch
 * raises that), 4 EU/t per unit, 6.4 s at 167% speed.
 *
 * GTNL's code gives 100,000 mB of UU Amplifier per ingot while its recipe page shows 2000 mB; this port uses the page.
 * This file is subject to the additional terms in LICENSE-ADDITIONAL-TERMS.md (GT-Not-Leisure).
 */
public enum MatterFabricationLogic implements GTRecipeType.ICustomRecipeLogic {

    INSTANCE;

    public static final int BALLS = 640, AMPLIFIER_MB = 2000, BASE_PARALLEL = 8, DURATION = 77;
    private static final TagKey<Item> INGOTS = ItemTags.create(new ResourceLocation("forge", "ingots"));
    private static final TagKey<Item> GEMS = ItemTags.create(new ResourceLocation("forge", "gems"));
    private static final TagKey<Item> BLOCKS = ItemTags.create(new ResourceLocation("forge", "storage_blocks"));

    private static Item matterBall() {
        Item i = ForgeRegistries.ITEMS.getValue(new ResourceLocation("ae2", "matter_ball"));
        return i == null || i == Items.AIR ? Items.SNOWBALL : i;
    }

    /** GTCA's UU Matter Amplifier (what this pack's UU Matter Fabricator eats), else GTCEu's UU Matter */
    private static net.minecraftforge.fluids.FluidStack amplifier(long mb) {
        var f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("gtca", "uu_matter_amplifier"));
        if (f == null || f == net.minecraft.world.level.material.Fluids.EMPTY) return GTMaterials.UUMatter.getFluid((int) Math.min(Integer.MAX_VALUE, mb));
        return new net.minecraftforge.fluids.FluidStack(f, (int) Math.min(Integer.MAX_VALUE, mb));
    }

    private static int units(ItemStack s) {
        if (s.is(INGOTS) || s.is(GEMS)) return 1;
        if (s.is(BLOCKS)) return 9;
        return 0;
    }

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        if (!(holder instanceof IMultiController ctrl)) return null;
        int circuit = 0;
        int max = BASE_PARALLEL;
        for (IMultiPart part : ctrl.getParts())
            if (part instanceof IParallelHatch hatch) max = Math.max(max, hatch.getCurrentParallel());
        ItemStack found = ItemStack.EMPTY;
        for (NotifiableItemStackHandler bus : ApexHatches.itemHandlers(ctrl, IO.IN))
            for (int i = 0; i < bus.getSlots(); i++) {
                ItemStack s = bus.getStackInSlot(i);
                if (s.isEmpty()) continue;
                if (IntCircuitBehaviour.isIntegratedCircuit(s)) {
                    circuit = IntCircuitBehaviour.getCircuitConfiguration(s);
                    continue;
                }
                if (found.isEmpty() && units(s) > 0 && !(s.getItem() instanceof IGTTool)) found = s;
            }
        if (found.isEmpty() || (circuit != 1 && circuit != 2)) return null;
        int per = units(found);
        int count = Math.max(1, Math.min(found.getCount(), Math.max(1, max / per)));
        int made = count * per;
        var b = GTAARecipeTypes.MATTER_FABRICATOR.recipeBuilder("matter_fabrication")
                .inputItems(found.copyWithCount(count)).circuitMeta(circuit)
                .duration(DURATION).EUt(Math.max(GTValues.VA[GTValues.MV], 4L * made));
        if (circuit == 1) b.outputItems(new ItemStack(matterBall(), Math.min(Integer.MAX_VALUE, BALLS * made)));
        else b.outputFluids(amplifier((long) AMPLIFIER_MB * made));
        return b.buildRawRecipe();
    }

    @Override
    public void buildRepresentativeRecipes() {
        GTAARecipeTypes.MATTER_FABRICATOR.addToMainCategory(GTAARecipeTypes.MATTER_FABRICATOR
                .recipeBuilder("matter_fabricator_balls").inputItems(new ItemStack(Items.IRON_INGOT)).circuitMeta(1)
                .outputItems(new ItemStack(matterBall(), BALLS)).duration(DURATION).EUt(GTValues.VA[GTValues.MV])
                .buildRawRecipe());
        GTAARecipeTypes.MATTER_FABRICATOR.addToMainCategory(GTAARecipeTypes.MATTER_FABRICATOR
                .recipeBuilder("matter_fabricator_amplifier").inputItems(new ItemStack(Items.IRON_INGOT)).circuitMeta(2)
                .outputFluids(amplifier(AMPLIFIER_MB)).duration(DURATION)
                .EUt(GTValues.VA[GTValues.MV]).buildRawRecipe());
    }
}
