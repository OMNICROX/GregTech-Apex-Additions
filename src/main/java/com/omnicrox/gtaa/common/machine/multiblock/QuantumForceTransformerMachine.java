package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

/**
 * Quantum Force Transformer. Port of GT++ MTEQuantumForceTransformer (GT5-Unofficial, LGPL-3.0):
 * <ul>
 * <li>Manipulator casings set the crafting tier, Shielding cores set the focus tier</li>
 * <li>the recipe's catalyst must sit in an input bus; it isn't used up and its stack count is the parallel cap</li>
 * <li>every output has an equal share of the total chance; Neptunium Plasma + a programmed circuit focuses
 * one output (circuit N = output N, counting from 0), Fermium Plasma raises every output</li>
 * <li>plasma use: focus tier * 4 * sqrt(parallels) L per second</li>
 * </ul>
 */
public class QuantumForceTransformerMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            QuantumForceTransformerMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CTX_CRAFT = "QFTCraftTier", CTX_FOCUS = "QFTFocusTier";
    public static final String TIER = "qft_tier", FOCUS = "qft_focus", CATALYST = "qft_catalyst";
    private static final int PLASMA_PER_FOCUS_TIER = 4;

    @DescSynced private int craftingTier, focusingTier;
    @Persisted @DescSynced private boolean doNeptunium, doFermium;
    @Persisted private int runParallels;

    public QuantumForceTransformerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public static TraceabilityPredicate tiered(String ctx, Supplier<Block[]> blocks, String what) {
        return new TraceabilityPredicate(s -> {
            Block[] arr = blocks.get();
            int tier = 0;
            for (int i = 0; i < arr.length; i++) if (arr[i] == s.getBlockState().getBlock()) tier = i + 1;
            if (tier == 0) return false;
            Object current = s.getMatchContext().getOrPut(ctx, tier);
            if (!current.equals(tier)) {
                s.setError(new PatternStringError(what + " must all be the same tier"));
                return false;
            }
            return true;
        }, () -> Arrays.stream(blocks.get()).map(b -> BlockInfo.fromBlockState(b.defaultBlockState())).toArray(BlockInfo[]::new));
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Object c = getMultiblockState().getMatchContext().get(CTX_CRAFT);
        Object f = getMultiblockState().getMatchContext().get(CTX_FOCUS);
        craftingTier = c instanceof Integer i ? i : 0;
        focusingTier = f instanceof Integer i ? i : 0;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        craftingTier = focusingTier = 0;
    }

    private List<ItemStack> inputItems() {
        List<ItemStack> out = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != IO.IN) continue;
                for (var h : handler.getCapability(ItemRecipeCapability.CAP)) {
                    if (h instanceof NotifiableItemStackHandler inv) {
                        for (int i = 0; i < inv.getSlots(); i++) {
                            ItemStack s = inv.getStackInSlot(i);
                            if (!s.isEmpty()) out.add(s);
                        }
                    }
                }
            }
        }
        return out;
    }

    private List<IFluidHandler> inputTanks() {
        List<IFluidHandler> out = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != IO.IN) continue;
                for (var h : handler.getCapability(FluidRecipeCapability.CAP)) {
                    if (h instanceof NotifiableFluidTank t) out.add(t);
                }
            }
        }
        return out;
    }

    private static FluidStack plasma(com.gregtechceu.gtceu.api.data.chemical.material.Material m, int amount) {
        try {
            FluidStack s = m.getFluid(FluidStorageKeys.PLASMA, amount);
            return s == null ? FluidStack.EMPTY : s;
        } catch (Throwable t) {
            return FluidStack.EMPTY;
        }
    }

    private boolean hasFluid(FluidStack stack) {
        if (stack.isEmpty()) return false;
        for (IFluidHandler t : inputTanks())
            if (((com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank) t).drainInternal(stack, IFluidHandler.FluidAction.SIMULATE).getAmount() > 0) return true;
        return false;
    }

    private boolean drain(FluidStack stack) {
        if (stack.isEmpty()) return false;
        int left = stack.getAmount();
        for (IFluidHandler t : inputTanks()) {
            left -= ((com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank) t).drainInternal(new FluidStack(stack, left), IFluidHandler.FluidAction.SIMULATE).getAmount();
            if (left <= 0) break;
        }
        if (left > 0) return false;
        left = stack.getAmount();
        for (IFluidHandler t : inputTanks()) {
            left -= ((com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank) t).drainInternal(new FluidStack(stack, left), IFluidHandler.FluidAction.EXECUTE).getAmount();
            if (left <= 0) break;
        }
        return true;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof QuantumForceTransformerMachine qft)) return ModifierFunction.NULL;
        int tier = Math.max(1, recipe.data.getInt(TIER));
        if (tier > qft.craftingTier) {
            return ModifierFunction.cancel(Component.literal("Needs Manipulator tier " + tier));
        }
        Item catalyst = ForgeRegistries.ITEMS.getValue(new ResourceLocation(recipe.data.getString(CATALYST)));
        int catalysts = 0;
        int circuit = -1;
        for (ItemStack s : qft.inputItems()) {
            if (catalyst != null && s.is(catalyst)) catalysts += s.getCount();
            if (s.is(GTItems.PROGRAMMED_CIRCUIT.get())) circuit = IntCircuitBehaviour.getCircuitConfiguration(s);
        }
        if (catalysts <= 0) {
            return ModifierFunction.cancel(Component.literal("Missing catalyst: " +
                    (catalyst == null ? "?" : catalyst.getDescription().getString())));
        }
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, catalysts);
        if (parallels <= 0) return ModifierFunction.NULL;

        int outputCount = 0;
        for (var e : recipe.outputs.entrySet()) outputCount += e.getValue().size();
        if (outputCount == 0) return ModifierFunction.NULL;

        boolean nep = false, fer = false;
        if (recipe.data.getInt(FOCUS) <= qft.focusingTier) {
            nep = circuit >= 0 && circuit < outputCount && qft.hasFluid(plasma(GTMaterials.Neptunium, 1));
            fer = qft.hasFluid(plasma(GTMaterials.Fermium, 1));
        }
        int[] chances = chances(outputCount, qft.focusingTier - tier, nep ? circuit : -1, nep, fer);
        qft.doNeptunium = nep;
        qft.doFermium = fer;
        qft.runParallels = parallels;

        final int p = parallels;
        return r -> {
            GTRecipe copy = r.copy(ContentModifier.multiplier(p), false);
            Map<RecipeCapability<?>, List<Content>> outs = new LinkedHashMap<>();
            int idx = 0;
            for (var e : copy.outputs.entrySet()) {
                List<Content> list = new ArrayList<>();
                for (Content c : e.getValue()) {
                    double f = chances[idx++] / 10000.0;
                    if (f <= 0) continue;
                    list.add(c.copy(e.getKey(), ContentModifier.multiplier(f)));
                }
                outs.put(e.getKey(), list);
            }
            copy.outputs.clear();
            copy.outputs.putAll(outs);
            copy.parallels = p;
            return copy;
        };
    }

    /** GT++ getOutputChances */
    static int[] chances(int n, int difference, int focused, boolean nep, boolean fer) {
        int per = 10000 / n;
        int[] c = new int[n];
        Arrays.fill(c, per);
        for (int i = 0; i < n; i++) {
            switch (Math.max(0, Math.min(3, difference))) {
                case 0 -> {
                    if (nep) c[i] = i == focused ? c[i] + per / 2 * (n - 1) : c[i] / 2;
                    if (fer) c[i] += (10000 - c[i]) / 4;
                }
                case 1 -> {
                    if (nep) c[i] = i == focused ? c[i] + per * 3 / 4 * (n - 1) : c[i] / 4;
                    if (fer) c[i] += (10000 - c[i]) / 3;
                }
                default -> {
                    if (nep) c[i] = i == focused ? 10000 : 0;
                    if (fer) c[i] += (10000 - c[i]) / 2;
                }
            }
        }
        return c;
    }

    @Override
    public boolean onWorking() {
        boolean ok = super.onWorking();
        if ((doNeptunium || doFermium) && getOffsetTimer() % 20 == 0) {
            int amount = (int) (Math.max(1, focusingTier) * PLASMA_PER_FOCUS_TIER * Math.sqrt(Math.max(1, runParallels)));
            boolean failed = false;
            if (doNeptunium && !drain(plasma(GTMaterials.Neptunium, amount))) failed = true;
            if (doFermium && !drain(plasma(GTMaterials.Fermium, amount))) failed = true;
            if (failed) {
                // GT++: running out of plasma voids the run
                doNeptunium = doFermium = false;
                getRecipeLogic().interruptRecipe();
                return false;
            }
        }
        return ok;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Manipulator tier: " + craftingTier + "  |  Focus tier: " + focusingTier)
                .withStyle(ChatFormatting.AQUA));
        if (doNeptunium || doFermium) textList.add(Component.literal((doNeptunium ? "Neptunium focus " : "") +
                (doFermium ? "Fermium boost" : "")).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
