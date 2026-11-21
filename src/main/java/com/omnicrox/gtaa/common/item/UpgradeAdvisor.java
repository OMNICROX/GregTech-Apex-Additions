package com.omnicrox.gtaa.common.item;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.omnicrox.gtaa.api.DefcRecipeType;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.machine.multiblock.ComponentAssemblyLineMachine;
import com.omnicrox.gtaa.common.machine.multiblock.MysticalCultivationArrayMachine;
import com.omnicrox.gtaa.common.machine.multiblock.QuantumForceTransformerMachine;
import com.omnicrox.gtaa.common.machine.multiblock.SuitFabricationStationMachine;
import com.omnicrox.gtaa.common.machine.multiblock.naquadah.NaquadahFuelRefineryMachine;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Explains Structure Upgrader choices for one machine: what a block family does in it, which of the machine's
 * recipes each tier unlocks (read from the same recipe data the machine checks), and whether an upgrade is
 * needed or only makes it faster.
 */
final class UpgradeAdvisor {

    static final int MAXED = 0, OPTIONAL = 1, NEEDED = 2;

    record FamilyAdvice(List<String> info, int verdict, String verdictText, List<String> tags, List<String> details,
                        int recommended) {}

    record PartAdvice(List<String> info, int verdict, String verdictText, String tag, String detail) {}

    private final IMultiController controller;
    private final List<GTRecipe> recipes = new ArrayList<>();
    private final String machinePath;
    private final long maxVoltage;
    private final int[] coilTemps;

    UpgradeAdvisor(IMultiController controller) {
        this.controller = controller;
        Level level = controller.self().getLevel();
        machinePath = controller.self().getDefinition().getId().getPath();
        if (controller instanceof IRecipeLogicMachine rlm && level != null) {
            for (GTRecipeType type : rlm.getRecipeTypes()) {
                if (type == null) continue;
                try {
                    recipes.addAll(level.getRecipeManager().getAllRecipesFor(type));
                } catch (Exception ignored) {}
            }
        }
        // the Cultivation Array's recipes are only a JEI guide; it farms through its own loop
        if (controller instanceof MysticalCultivationArrayMachine) recipes.clear();
        maxVoltage = controller instanceof WorkableElectricMultiblockMachine w ? w.getMaxVoltage() : 0;
        List<ICoilType> coils = new ArrayList<>(GTCEuAPI.HEATING_COILS.keySet());
        coils.sort(Comparator.comparingInt(ICoilType::getTier));
        coilTemps = coils.stream().mapToInt(ICoilType::getCoilTemperature).toArray();
    }

    private static long eut(GTRecipe r) {
        try {
            var io = RecipeHelper.getRealEUtWithIO(r);
            return io.isInput() ? io.stack().getTotalEU() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private static String vn(int tier) {
        return GTValues.VN[Math.max(0, Math.min(GTValues.VN.length - 1, tier))];
    }

    private static int voltageTier(long v) {
        for (int t = 0; t < GTValues.V.length; t++) if (GTValues.V[t] >= v) return t;
        return GTValues.MAX;
    }

    /** Recipes this machine can't start right now because its energy hatches are too weak. */
    int powerLocked() {
        if (maxVoltage <= 0) return 0;
        int n = 0;
        for (GTRecipe r : recipes) if (eut(r) > maxVoltage) n++;
        return n;
    }

    int recipeCount() {
        return recipes.size();
    }

    long maxVoltage() {
        return maxVoltage;
    }

    /** The family tier a recipe needs, or 0 when this family doesn't gate it. */
    private int need(StructureUpgrader.Family fam, GTRecipe r) {
        switch (fam.id()) {
            case "coils" -> {
                int temp = r.data.getInt("ebf_temp");
                if (temp <= 0) return 0;
                for (int i = 0; i < coilTemps.length; i++) if (coilTemps[i] >= temp) return i + 1;
                return coilTemps.length + 1;
            }
            case "qft_manipulators" -> {
                return Math.max(1, r.data.getInt(QuantumForceTransformerMachine.TIER));
            }
            case "field_restriction" -> {
                return Math.max(1, r.data.getInt(NaquadahFuelRefineryMachine.COIL_TIER));
            }
            case "defc_cores" -> {
                return Math.max(1, r.data.getInt(DefcRecipeType.TIER_KEY));
            }
            case "suit_casings" -> {
                return Math.max(1, r.data.getInt("station_tier"));
            }
            case "mana_cores" -> {
                return Math.max(1, r.data.getInt(com.omnicrox.gtaa.common.machine.multiblock.ManaNexusMachine.TIER));
            }
            case "coal_casings" -> {
                int v = r.data.getInt(ComponentAssemblyLineMachine.CASING_TIER);
                if (v <= 0) return 0;
                for (int t = 1; t <= fam.tiers().size(); t++)
                    if (GTAABlocks.coalCasingTier(fam.block(t)) >= v) return t;
                return fam.tiers().size() + 1;
            }
            default -> {
                return 0;
            }
        }
    }

    private boolean gates(StructureUpgrader.Family fam) {
        for (GTRecipe r : recipes) if (need(fam, r) > 0) return true;
        return false;
    }

    private int locked(StructureUpgrader.Family fam, int tier) {
        int n = 0;
        for (GTRecipe r : recipes) if (need(fam, r) > tier) n++;
        return n;
    }

    private int between(StructureUpgrader.Family fam, int lo, int hi) {
        int n = 0;
        for (GTRecipe r : recipes) {
            int need = need(fam, r);
            if (need > lo && need <= hi) n++;
        }
        return n;
    }

    private int highestNeed(StructureUpgrader.Family fam) {
        int max = 0;
        for (GTRecipe r : recipes) max = Math.max(max, need(fam, r));
        return Math.min(max, fam.tiers().size());
    }

    private String unit(StructureUpgrader.Family fam) {
        return fam.id().equals("suit_casings") ? "suits" : "recipes";
    }

    private List<String> familyInfo(StructureUpgrader.Family fam, int cur) {
        List<String> info = new ArrayList<>();
        switch (fam.id()) {
            case "coils" -> {
                if (gates(fam)) {
                    info.add("Sets the max heat. A recipe only runs if its heat (shown in JEI) is at or below the coils'.");
                    info.add("Spare heat makes recipes cheaper: 5% less power per 900 K spare, and a perfect overclock per 1800 K.");
                } else if (machinePath.contains("pyrolyse")) {
                    info.add("Speed: every coil tier makes the oven faster. No recipe needs better coils.");
                } else if (machinePath.contains("cracker")) {
                    info.add("Efficiency: every coil tier cuts power use by 10%. No recipe needs better coils.");
                } else if (machinePath.contains("smelter")) {
                    info.add("More parallel smelts and less power per coil tier. No recipe needs better coils.");
                } else {
                    info.add("Better coils make this machine faster or cheaper. No recipe here needs a higher coil.");
                }
            }
            case "qft_manipulators" -> info.add("Crafting tier: the QFT only runs recipes up to this tier.");
            case "qft_shielding" -> {
                info.add("Focus tier: lets Neptunium / Fermium plasma boost outputs, and gives later outputs a bigger share.");
                info.add("Only matters if you feed it plasma. Not needed to run any recipe.");
            }
            case "field_restriction" -> info.add("Decides which fuels it can make, 4 parallels per tier, and one perfect overclock per tier above a recipe.");
            case "defc_cores" -> {
                info.add("Sets the highest fusion-crafting tier. Every tier above a recipe halves its time.");
                info.add("Chaotic cores also need Fusion Machine Casing MK II in the structure.");
            }
            case "coal_casings" -> info.add("Sets the highest component tier it can make. Every tier above a recipe halves its time.");
            case "mana_cores" -> {
                info.add("Mana storage, crafts at once, and which Botania crafts the Nexus can do.");
                info.add("Every core must be the same kind.");
            }
            case "suit_casings" -> info.add("Decides which suits the station can make (Steel Booth, Showcase Pod, Holo Chamber, Ascension Forge).");
            case "mca_soil" -> info.add("Yield: how much essence each crop gives per harvest. Higher soil = more output, nothing is locked.");
            case "mca_glass" -> info.add("Caps the voltage the array runs at. Higher voltage = faster harvests (up to LuV) and more crops.");
            default -> info.add("Tiered blocks of this structure.");
        }
        return info;
    }

    FamilyAdvice family(StructureUpgrader.Family fam, int cur) {
        int size = fam.tiers().size();
        List<String> info = familyInfo(fam, cur);
        List<String> tags = new ArrayList<>(), details = new ArrayList<>();
        int verdict;
        String verdictText;
        int recommended = 0;
        boolean gated = gates(fam);
        String unit = unit(fam);

        if (gated) {
            int lockedNow = locked(fam, cur);
            int top = highestNeed(fam);
            if (lockedNow > 0) {
                verdict = NEEDED;
                recommended = Math.max(cur + 1, top);
                int next = cur + 1;
                while (next < recommended && between(fam, cur, next) == 0) next++;
                String all = fam.block(Math.min(size, recommended)).getName().getString();
                verdictText = lockedNow + " " + unit + " locked. " + (next < recommended
                        ? "Next step: " + fam.block(next).getName().getString() + " unlocks " + between(fam, cur, next) + "; " + all + " unlocks all."
                        : all + " unlocks all of them.");
            } else {
                verdict = cur >= size ? MAXED : OPTIONAL;
                verdictText = cur >= size ? "Maxed." : "Not needed: all " + unit + " already run. Higher only makes it " +
                        (fam.id().equals("suit_casings") || fam.id().equals("qft_manipulators") ? "nothing new." : "faster / cheaper.");
            }
        } else if (fam.id().equals("mca_glass")) {
            int runTier = Math.min(voltageTier(maxVoltage), GTValues.LuV);
            int cap = MysticalCultivationArrayMachine.GLASS_CAP[Math.max(0, Math.min(3, cur - 1))];
            if (maxVoltage > 0 && cap < runTier) {
                verdict = NEEDED;
                for (int t = cur + 1; t <= size; t++)
                    if (MysticalCultivationArrayMachine.GLASS_CAP[Math.min(3, t - 1)] >= runTier) {
                        recommended = t;
                        break;
                    }
                verdictText = "Your energy hatches give " + vn(voltageTier(maxVoltage)) + " but the glass holds it at " + vn(cap) + ".";
            } else {
                verdict = cur >= size ? MAXED : OPTIONAL;
                verdictText = "Not needed with your current energy hatches.";
            }
        } else {
            verdict = cur >= size ? MAXED : OPTIONAL;
            verdictText = cur >= size ? "Maxed." : "Optional.";
        }

        for (int t = 1; t <= size; t++) {
            String tag = "", detail;
            if (t < cur) {
                int lose = gated ? between(fam, t, cur) : 0;
                tag = "↓";
                detail = "Downgrade" + (lose > 0 ? ": loses " + lose + " " + unit : "") + ".";
            } else if (t == cur) {
                detail = "Current tier (makes every block in the structure match).";
            } else {
                int gain = gated ? between(fam, cur, t) : 0;
                detail = switch (fam.id()) {
                    case "coils" -> "Max heat " + coilTemps[Math.min(coilTemps.length, t) - 1] + " K";
                    case "qft_manipulators" -> "Crafting tier " + t;
                    case "qft_shielding" -> "Focus tier " + t + " (plasma boosts up to tier " + t + " recipes)";
                    case "field_restriction" -> "T" + t + ": " + (4 * t) + " parallels";
                    case "defc_cores" -> "Core tier " + t + ": " + (1 << (t - cur)) + "× faster on what you run now";
                    case "coal_casings" -> "Makes up to " + vn(GTAABlocks.coalCasingTier(fam.block(t))) + " components, " +
                            (1 << (t - cur)) + "× faster on what you run now";
                    case "suit_casings" -> "Tier " + t + " (" + SuitFabricationStationMachine.TIER_NAMES[Math.min(4, t)] + ")";
                    case "mana_cores" -> com.omnicrox.gtaa.common.machine.multiblock.ManaNexusMachine.coreInfo(t).get(0);
                    case "mca_soil" -> "Yield " + MysticalCultivationArrayMachine.SOIL_YIELD[Math.min(6, t - 1)] + " per crop (now " +
                            MysticalCultivationArrayMachine.SOIL_YIELD[Math.min(6, Math.max(0, cur - 1))] + ")";
                    case "mca_glass" -> "Runs at up to " + vn(MysticalCultivationArrayMachine.GLASS_CAP[Math.min(3, t - 1)]);
                    default -> "Tier " + t;
                };
                if (gain > 0) {
                    tag = "+" + gain;
                    detail += "; unlocks " + gain + " " + unit;
                } else if (gated) {
                    detail += "; unlocks nothing new";
                }
                if (t == recommended) {
                    tag = "★ " + tag;
                    detail += ". Recommended: lowest tier that runs everything.";
                }
            }
            tags.add(tag.trim());
            details.add(detail);
        }
        return new FamilyAdvice(info, verdict, verdictText, tags, details, recommended);
    }

    PartAdvice part(IMultiPart part, int nextTier) {
        String path = part.self().getDefinition().getId().getPath().toLowerCase(Locale.ROOT);
        int tier = part instanceof ITieredMachine t ? t.getTier() : 0;
        List<String> info = new ArrayList<>();
        int verdict = OPTIONAL;
        String verdictText = "Optional.";
        String tag = vn(tier) + "→" + vn(nextTier);
        String detail;
        if (path.contains("energy") || path.contains("laser_target") || path.contains("substation_input")) {
            int locked = powerLocked();
            long newBudget = maxVoltage <= 0 ? GTValues.V[nextTier] : maxVoltage * (GTValues.V[nextTier] / Math.max(1, GTValues.V[tier]));
            int gain = 0;
            for (GTRecipe r : recipes) {
                long e = eut(r);
                if (e > maxVoltage && e <= newBudget) gain++;
            }
            if (gain > 0) tag += "  +" + gain;
            info.add("Energy input: sets the voltage the machine runs at. Recipes above it can't start; recipes below it overclock (2× speed for 4× power per tier).");
            detail = "Up to " + GTValues.V[nextTier] + " EU/t per amp (" + vn(nextTier) + ")" +
                    (gain > 0 ? "; with all your energy hatches at this tier, " + gain + " more recipes can run" : "");
            if (controller instanceof MysticalCultivationArrayMachine) {
                long v = 0;
                if (controller.self() instanceof WorkableElectricMultiblockMachine w && w.getEnergyContainer() != null)
                    v = w.getEnergyContainer().getInputVoltage();
                info.set(0, "Energy input: the array runs at this voltage (capped by its glass). Each tier harvests twice as fast, up to LuV, and doubles the crops it can grow.");
                if (v < GTValues.V[GTValues.LV]) {
                    verdict = NEEDED;
                    verdictText = "The array needs at least an LV energy hatch to run.";
                } else {
                    verdictText = "Optional: faster harvests and room for more crops.";
                }
            } else if (locked > 0) {
                verdict = NEEDED;
                verdictText = locked + " recipes need more power than your hatches give" +
                        (gain > 0 ? "; this tier unlocks " + gain + " of them (upgrade every energy hatch)." : ".");
            } else {
                verdictText = "Not needed: every recipe already has enough power. Higher = faster overclocks.";
            }
        } else if (path.contains("dynamo")) {
            info.add("Energy output: how much EU/t this generator can push out.");
            detail = "Up to " + GTValues.V[nextTier] + " EU/t per amp";
            verdictText = "Only needed if the generator makes more than the hatch can carry.";
        } else if (path.contains("seed_hatch")) {
            int now = com.omnicrox.gtaa.common.machine.part.SeedHatchPartMachine.slots(tier);
            int next = com.omnicrox.gtaa.common.machine.part.SeedHatchPartMachine.slots(nextTier);
            boolean full = part instanceof com.omnicrox.gtaa.common.machine.part.SeedHatchPartMachine h && h.census()[1] >= now;
            info.add("Seed Hatch: how many different crops you can plant (256 seeds per type).");
            detail = now + " \u2192 " + next + " seed types";
            tag += "  +" + (next - now);
            if (full) {
                verdict = NEEDED;
                verdictText = "This hatch is full: every slot has a crop in it.";
            } else {
                verdictText = "Not needed: this hatch still has free slots.";
            }
        } else if (path.contains("input_bus") || path.contains("output_bus")) {
            int now = (1 + Math.min(9, tier)) * (1 + Math.min(9, tier));
            int next = (1 + Math.min(9, nextTier)) * (1 + Math.min(9, nextTier));
            info.add("Item bus: more slots. Only matters if inputs or outputs don't fit.");
            detail = now + " → " + next + " slots";
        } else if (path.contains("input_hatch") || path.contains("output_hatch")) {
            long now = 8L << Math.min(9, tier), next = 8L << Math.min(9, nextTier);
            info.add("Fluid hatch: bigger tank. Only matters if a recipe needs more fluid than fits.");
            detail = now + " → " + next + " buckets";
        } else if (path.contains("muffler")) {
            info.add("Muffler: higher tiers recover more ash / by-products.");
            detail = "Better by-product recovery";
        } else if (path.contains("parallel")) {
            info.add("Parallel hatch: runs more copies of a recipe at once.");
            detail = "More parallels";
        } else {
            info.add("Higher-tier version of this part.");
            detail = vn(nextTier) + " version";
        }
        detail += ". The old hatch and its contents drop on the ground.";
        return new PartAdvice(info, verdict, verdictText, tag, detail);
    }
}
