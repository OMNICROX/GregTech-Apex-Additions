package com.omnicrox.gtaa.common.machine.multiblock.tst;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.apexlib.multiblock.ApexModifiers;
import com.omnicrox.gtaa.common.machine.multiblock.QuantumForceTransformerMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Crystalline Infinitier (Twist Space Technology "GTCM_CrystallineInfinitier"): autoclave and chemical bath.
 * Field generator tier F (Containment Field Generator = 1, Stabilisation Field Generators T0-T8 = 3-11, as in TST,
 * which had an Ultimate Containment Field Generator at 2): parallels = F x (voltage tier)^2 (voltage stands in for
 * TST's glass tier), EU / F, perfect overclocks from F 3. Autoclave runs 4x faster, chemical bath 16x.
 * Switch modes in the controller GUI.
 */
public class CrystallineInfinitierMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            CrystallineInfinitierMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CTX = "CIFieldGenerator";

    @DescSynced private int fieldTier;

    public CrystallineInfinitierMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private static Block[] fieldBlocks() {
        List<Block> out = new ArrayList<>();
        add(out, "gtna:containment_field_generator");
        for (int i = 0; i <= 8; i++) add(out, "gtna:stabilisation_field_generator_tier_" + i);
        return out.toArray(Block[]::new);
    }

    private static void add(List<Block> out, String id) {
        Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));
        out.add(b == null ? Blocks.BEDROCK : b);
    }

    public static TraceabilityPredicate fieldGenerators() {
        return QuantumForceTransformerMachine.tiered(CTX, CrystallineInfinitierMachine::fieldBlocks, "Field generators");
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Object c = getMultiblockState().getMatchContext().get(CTX);
        int idx = c instanceof Integer i ? i : 0;
        fieldTier = idx <= 0 ? 0 : idx == 1 ? 1 : idx + 1;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        fieldTier = 0;
    }

    public int maxParallel() {
        int v = Math.max(1, ApexModifiers.tier(this));
        return (int) Math.min(Integer.MAX_VALUE, (long) Math.max(1, fieldTier) * v * v);
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof CrystallineInfinitierMachine m)) return ModifierFunction.NULL;
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, m.maxParallel());
        if (parallels <= 0) return ModifierFunction.NULL;
        double speed = m.getRecipeType() == GTRecipeTypes.CHEMICAL_BATH_RECIPES ? 1.0 / 16 : 1.0 / 4;
        ModifierFunction base = ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels)).parallels(parallels)
                .durationMultiplier(speed).eutMultiplier(1.0 / Math.max(1, m.fieldTier)).build();
        GTRecipe scaled = base.apply(recipe);
        if (scaled == null) return ModifierFunction.NULL;
        var oc = m.fieldTier >= 3 ? GTRecipeModifiers.OC_PERFECT_SUBTICK : GTRecipeModifiers.OC_NON_PERFECT_SUBTICK;
        return base.andThen(oc.getModifier(machine, scaled));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Field generator tier " + fieldTier + "  |  " + maxParallel() + " parallels  |  EU / " +
                Math.max(1, fieldTier)).withStyle(ChatFormatting.AQUA));
        if (fieldTier >= 3) textList.add(Component.literal("Perfect overclocks").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
