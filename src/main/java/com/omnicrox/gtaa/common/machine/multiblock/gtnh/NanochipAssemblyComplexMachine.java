package com.omnicrox.gtaa.common.machine.multiblock.gtnh;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.apexlib.multiblock.ApexModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Nanochip Assembly Complex (GT5-Unofficial 2.9), condensed: instead of separate module multis joined by vacuum
 * conveyors, the 9 modules are blocks in the 3x3 module bay on the roof. The Assembly Matrix is required; the
 * others unlock circuit families and each installed module adds 32 parallels. Runs every circuit recipe from the
 * Circuit Assembler and the Assembly Line (circuits only) with perfect overclocks.
 */
public class NanochipAssemblyComplexMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            NanochipAssemblyComplexMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CTX = "NACModules";

    public enum Module {
        ASSEMBLY_MATRIX("Assembly Matrix", "required; basic, electronic and integrated circuits"),
        PART_PROCESSOR("Part Processor", "+100% speed"),
        BOARD_PROCESSOR("Board Processor", "microprocessor / processor circuits"),
        ETCHING_ARRAY("Etching Array", "crystal circuits"),
        CUTTING_CHAMBER("Cutting Chamber", "nano circuits"),
        WIRE_TRACER("Wire Tracer", "quantum circuits"),
        OPTICAL_ORGANIZER("Optical Organizer", "optical, cosmic and exotic circuits"),
        BIOLOGICAL_COORDINATION("Biological Coordination", "wetware and bio circuits"),
        ENCASEMENT_WRAPPER("Encasement Wrapper", "mainframes and supercomputers");

        public final String title, unlocks;

        Module(String title, String unlocks) {
            this.title = title;
            this.unlocks = unlocks;
        }
    }

    @DescSynced private int moduleMask;

    public NanochipAssemblyComplexMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    /** module bay slot: any module, or a Firewall Projection Casing for an empty slot */
    public static TraceabilityPredicate moduleSlot() {
        return new TraceabilityPredicate(s -> {
            Block b = s.getBlockState().getBlock();
            if (b == GTAABlocks.NANOCHIP_FIREWALL_PROJECTION_CASING.get()) return true;
            Block[] mods = GTAABlocks.nacModules();
            for (int i = 0; i < mods.length; i++) {
                if (mods[i] == b) {
                    Set<Integer> set = s.getMatchContext().getOrPut(CTX, new java.util.HashSet<Integer>());
                    set.add(i);
                    return true;
                }
            }
            return false;
        }, () -> {
            List<BlockInfo> l = new ArrayList<>();
            for (Block b : GTAABlocks.nacModules()) l.add(BlockInfo.fromBlockState(b.defaultBlockState()));
            l.add(BlockInfo.fromBlockState(GTAABlocks.NANOCHIP_FIREWALL_PROJECTION_CASING.get().defaultBlockState()));
            return l.toArray(BlockInfo[]::new);
        });
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Object o = getMultiblockState().getMatchContext().get(CTX);
        moduleMask = 0;
        if (o instanceof Set<?> set) for (Object i : set) if (i instanceof Integer n) moduleMask |= 1 << n;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        moduleMask = 0;
    }

    public boolean has(Module m) {
        return (moduleMask & (1 << m.ordinal())) != 0;
    }

    public EnumSet<Module> modules() {
        EnumSet<Module> s = EnumSet.noneOf(Module.class);
        for (Module m : Module.values()) if (has(m)) s.add(m);
        return s;
    }

    private static boolean isCircuit(ItemStack s) {
        return s.getTags().anyMatch(t -> t.location().getPath().startsWith("circuits"));
    }

    /** the module a circuit needs, from its id */
    static Module required(String id) {
        String p = id.toLowerCase(Locale.ROOT);
        if (p.contains("mainframe") || p.contains("supercomputer") || p.contains("computer")) return Module.ENCASEMENT_WRAPPER;
        if (p.contains("wetware") || p.contains("bio")) return Module.BIOLOGICAL_COORDINATION;
        if (p.contains("optical") || p.contains("cosmic") || p.contains("exotic") || p.contains("photonic")) return Module.OPTICAL_ORGANIZER;
        if (p.contains("crystal")) return Module.ETCHING_ARRAY;
        if (p.contains("quantum")) return Module.WIRE_TRACER;
        if (p.contains("nano")) return Module.CUTTING_CHAMBER;
        if (p.contains("processor") || p.contains("micro")) return Module.BOARD_PROCESSOR;
        return Module.ASSEMBLY_MATRIX;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof NanochipAssemblyComplexMachine nac)) return ModifierFunction.NULL;
        if (!nac.has(Module.ASSEMBLY_MATRIX)) return ModifierFunction.cancel(Component.literal("Needs an Assembly Matrix module"));
        List<ItemStack> outs = RecipeHelper.getOutputItems(recipe);
        if (outs.isEmpty() || !isCircuit(outs.get(0))) return ModifierFunction.cancel(Component.literal("Only assembles circuits"));
        Module need = required(BuiltInRegistries.ITEM.getKey(outs.get(0).getItem()).getPath());
        if (!nac.has(need)) return ModifierFunction.cancel(Component.literal("Needs the " + need.title + " module"));
        int max = 32 * Integer.bitCount(nac.moduleMask);
        double speed = nac.has(Module.PART_PROCESSOR) ? 0.5 : 1.0;
        ModifierFunction par = ApexModifiers.parallel(m -> max, m -> speed, 1.0).getModifier(machine, recipe);
        if (par == ModifierFunction.NULL) return par;
        return par.andThen(GTRecipeModifiers.OC_PERFECT.getModifier(machine, par.apply(recipe)));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Modules: " + Integer.bitCount(moduleMask) + "/9 (" + 32 * Integer.bitCount(moduleMask) +
                " parallels)").withStyle(ChatFormatting.LIGHT_PURPLE));
        for (Module m : Module.values())
            textList.add(Component.literal((has(m) ? " ✔ " : " ✘ ") + m.title + " - " + m.unlocks)
                    .withStyle(has(m) ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
    }
}
