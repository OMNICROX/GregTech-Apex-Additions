package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.MarkerMaterials;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

/**
 * Items that other mods register but never give a recipe (found with /gtapexadditions audit):
 * - GTCA's wafer and optical line (europium / optical boules, NPIC, PPIC, pico wafers, optical SMDs, optical RAM,
 *   optically perfect CPU, optical circuit board): our Cosmic and Transcendent circuits need them. Modeled on GT New
 *   Horizons' optical line, which GTCA copies; GTCA's Optical Processor / Assembly / Supercomputer / Mainframe become
 *   UV / UHV / UEV / UIV circuits as in GTNH.
 * - GTMThings' UHV-OpV wireless energy covers (its UHV+ wireless hatches need them, but it only makes LV-UV covers).
 */
public final class GTAAMissingRecipes {

    private GTAAMissingRecipes() {}

    private static final Item[] S = { Items.BARRIER, Items.STRUCTURE_VOID, Items.LIGHT, Items.JIGSAW, Items.COMMAND_BLOCK,
            Items.CHAIN_COMMAND_BLOCK, Items.REPEATING_COMMAND_BLOCK, Items.DEBUG_STICK, Items.KNOWLEDGE_BOOK,
            Items.COMMAND_BLOCK_MINECART };

    /** a provider that swaps S[0..] for these ids and only loads with the given mod */
    private static Consumer<FinishedRecipe> with(Consumer<FinishedRecipe> provider, String mod, String... ids) {
        Map<Item, String> m = new HashMap<>();
        for (int i = 0; i < ids.length; i++) m.put(S[i], ids[i]);
        return GTAACompatRecipes.needing(provider, mod, m);
    }

    private static boolean has(TagPrefix p, Material m) {
        return m != null && !ChemicalHelper.get(p, m).isEmpty();
    }

    private static Material pick(TagPrefix p, Material... options) {
        for (Material m : options) if (has(p, m)) return m;
        return options[options.length - 1];
    }

    public static void init(Consumer<FinishedRecipe> provider) {
        gtcaWafers(provider);
        gtcaOptical(provider);
        opticalCircuits(provider);
        wirelessCovers(provider);
    }

    // ---------------- GTCA wafers: europium boule -> NPIC -> PPIC -> pico wafer ----------------
    private static void gtcaWafers(Consumer<FinishedRecipe> p) {
        BLAST_RECIPES.recipeBuilder("gtapexadditions:gtca/europium_boule")
                .inputItems(dust, GTMaterials.Silicon, 64).inputItems(dust, GTMaterials.Europium, 8)
                .inputItems(dust, GTMaterials.GalliumArsenide, 4).inputFluids(GTMaterials.Argon.getFluid(8000))
                .outputItems(S[0]).blastFurnaceTemp(7200).duration(9000).EUt(VA[UV])
                .save(with(p, "gtca", "gtca:europium_doped_monocrystalyne_silicon_boule"));
        CUTTER_RECIPES.recipeBuilder("gtapexadditions:gtca/europium_waffle")
                .inputItems(S[0]).inputFluids(GTMaterials.Lubricant.getFluid(500)).outputItems(S[1], 64)
                .cleanroom(CleanroomType.CLEANROOM).duration(1600).EUt(VA[UV])
                .save(with(p, "gtca", "gtca:europium_doped_monocrystalyne_silicon_boule", "gtca:europium_waffle"));
        LASER_ENGRAVER_RECIPES.recipeBuilder("gtapexadditions:gtca/npic_wafer")
                .inputItems(S[0]).notConsumable(lens, MarkerMaterials.Color.Red).outputItems(S[1])
                .cleanroom(CleanroomType.CLEANROOM).duration(600).EUt(VA[UHV])
                .save(with(p, "gtca", "gtca:europium_waffle", "gtca:npic_wafer"));
        CUTTER_RECIPES.recipeBuilder("gtapexadditions:gtca/nano_power_ic")
                .inputItems(S[0]).inputFluids(GTMaterials.Lubricant.getFluid(100)).outputItems(S[1], 2)
                .cleanroom(CleanroomType.CLEANROOM).duration(900).EUt(VA[UHV])
                .save(with(p, "gtca", "gtca:npic_wafer", "gtca:nano_power_ic"));
        LASER_ENGRAVER_RECIPES.recipeBuilder("gtapexadditions:gtca/ppic_wafer")
                .inputItems(S[0]).notConsumable(lens, MarkerMaterials.Color.Purple).outputItems(S[1])
                .cleanroom(CleanroomType.CLEANROOM).duration(600).EUt(VA[UEV])
                .save(with(p, "gtca", "gtca:npic_wafer", "gtca:ppic_wafer"));
        CUTTER_RECIPES.recipeBuilder("gtapexadditions:gtca/piko_wafer_ic")
                .inputItems(S[0]).inputFluids(GTMaterials.Lubricant.getFluid(100)).outputItems(S[1], 2)
                .cleanroom(CleanroomType.CLEANROOM).duration(900).EUt(VA[UEV])
                .save(with(p, "gtca", "gtca:ppic_wafer", "gtca:piko_wafer_ic"));
    }

    // ---------------- GTCA optical line ----------------
    private static void gtcaOptical(Consumer<FinishedRecipe> p) {
        var sterile = CleanroomType.STERILE_CLEANROOM;
        BLAST_RECIPES.recipeBuilder("gtapexadditions:gtca/optical_boule")
                .inputItems(dust, GTMaterials.Silicon, 64).inputItems(dust, GTMaterials.Europium, 16)
                .inputItems(dust, GTMaterials.Rubidium, 8).inputFluids(GTMaterials.Radon.getFluid(8000))
                .outputItems(S[0]).blastFurnaceTemp(9900).duration(12000).EUt(VA[UHV])
                .save(with(p, "gtca", "gtca:opticaly_enriched_monocrystalyne_silicon_boule"));
        CUTTER_RECIPES.recipeBuilder("gtapexadditions:gtca/photonically_prepared_wafer")
                .inputItems(S[0]).inputFluids(GTMaterials.Lubricant.getFluid(500)).outputItems(S[1], 32)
                .cleanroom(sterile).duration(1600).EUt(VA[UHV])
                .save(with(p, "gtca", "gtca:opticaly_enriched_monocrystalyne_silicon_boule", "gtca:photonically_prepared_wafer"));
        LASER_ENGRAVER_RECIPES.recipeBuilder("gtapexadditions:gtca/raw_exposed_optical_chip")
                .inputItems(S[0]).notConsumable(lens, MarkerMaterials.Color.White).outputItems(S[1])
                .cleanroom(sterile).duration(900).EUt(VA[UEV])
                .save(with(p, "gtca", "gtca:photonically_prepared_wafer", "gtca:raw_exposed_optical_chip"));
        LASER_ENGRAVER_RECIPES.recipeBuilder("gtapexadditions:gtca/accurate_optical_ram")
                .inputItems(S[0]).notConsumable(lens, MarkerMaterials.Color.Cyan).outputItems(S[1], 8)
                .cleanroom(sterile).duration(900).EUt(VA[UEV])
                .save(with(p, "gtca", "gtca:photonically_prepared_wafer", "gtca:accurate_optical_ram"));
        Material housingFoil = pick(foil, GTMaterials.Tritanium, GTMaterials.Neutronium);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:gtca/optical_cpu_containment_housing")
                .inputItems(plate, GTMaterials.Neutronium, 2).inputItems(foil, housingFoil, 8)
                .inputFluids(GTMaterials.Polybenzimidazole.getFluid(L * 2)).outputItems(S[0])
                .cleanroom(sterile).duration(400).EUt(VA[UHV])
                .save(with(p, "gtca", "gtca:optical_cpu_containment_housing"));
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:gtca/optically_perfect_cpu")
                .inputItems(S[0]).inputItems(S[1]).inputFluids(GTMaterials.Polybenzimidazole.getFluid(L))
                .outputItems(S[2]).cleanroom(sterile).duration(400).EUt(VA[UEV])
                .save(with(p, "gtca", "gtca:raw_exposed_optical_chip", "gtca:optical_cpu_containment_housing", "gtca:optically_perfect_cpu"));
        CHEMICAL_RECIPES.recipeBuilder("gtapexadditions:gtca/optical_circuit_board")
                .inputItems(GTItems.WETWARE_CIRCUIT_BOARD.asStack()).inputItems(foil, GTMaterials.Neutronium, 4)
                .inputFluids(GTMaterials.Radon.getFluid(1000)).outputItems(S[0])
                .cleanroom(sterile).duration(600).EUt(VA[UEV])
                .save(with(p, "gtca", "gtca:optical_circuit_board"));
        // optical SMDs: GTCEu's advanced SMD recipes one step up (16 per craft)
        Material wire = pick(wireFine, GTMaterials.Trinium, GTMaterials.NaquadahAlloy, GTMaterials.Europium);
        Material nFoil = pick(foil, GTMaterials.Neutronium, GTMaterials.Tritanium);
        Material nRing = pick(ring, GTMaterials.Neutronium, GTMaterials.Tritanium, GTMaterials.NaquadahAlloy);
        smd(p, "optical_smd_transistor", b -> b.inputItems(foil, nFoil, 1).inputItems(wireFine, wire, 8));
        smd(p, "optical_smd_resistor", b -> b.inputItems(dust, GTMaterials.Naquadria, 1).inputItems(wireFine, wire, 4));
        smd(p, "optical_smd_capacitor", b -> b.inputItems(foil, GTMaterials.Polybenzimidazole, 2).inputItems(foil, nFoil, 1));
        smd(p, "optical_smd_diode", b -> b.inputItems(dust, GTMaterials.IndiumGalliumPhosphide, 2).inputItems(wireFine, wire, 16));
        smd(p, "optical_smd_inductor", b -> b.inputItems(ring, nRing, 1).inputItems(wireFine, wire, 4));
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:gtca/nanotube_spool")
                .inputItems(foil, GTMaterials.Graphene, 16).inputItems(rod, GTMaterials.Neutronium, 2)
                .inputFluids(GTMaterials.Polybenzimidazole.getFluid(L)).outputItems(S[0])
                .cleanroom(CleanroomType.CLEANROOM).duration(800).EUt(VA[UHV])
                .save(with(p, "gtca", "gtca:nanotube_spool"));
    }

    private static void smd(Consumer<FinishedRecipe> p, String id,
                            java.util.function.UnaryOperator<com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder> parts) {
        parts.apply(ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:gtca/" + id))
                .inputFluids(GTMaterials.Polybenzimidazole.getFluid(L * 2)).outputItems(S[0], 16)
                .cleanroom(CleanroomType.STERILE_CLEANROOM).duration(160).EUt(VA[UEV])
                .save(with(p, "gtca", "gtca:" + id));
    }

    // ---------------- GTNH optical circuits (UV / UHV / UEV / UIV), tagged in data/gtceu/tags/items/circuits ----------------
    private static void opticalCircuits(Consumer<FinishedRecipe> p) {
        var sterile = CleanroomType.STERILE_CLEANROOM;
        Material wire = pick(wireFine, GTMaterials.Trinium, GTMaterials.NaquadahAlloy, GTMaterials.Europium);
        String[] ids = { "gtca:optical_circuit_board", "gtca:optically_perfect_cpu", "gtca:optical_smd_transistor",
                "gtca:optical_smd_resistor", "gtca:optical_smd_capacitor", "gtca:optical_smd_inductor",
                "gtca:accurate_optical_ram", "gtca:optical_smd_diode", "gtca:optical_processor", "gtca:optical_assembly" };
        var q = with(p, "gtca", ids);
        CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:gtca/optical_processor")
                .inputItems(S[0]).inputItems(S[1]).inputItems(S[2], 4).inputItems(S[3], 4).inputItems(S[4], 4)
                .inputItems(wireFine, wire, 8).inputFluids(GTMaterials.SolderingAlloy.getFluid(L / 2))
                .outputItems(S[8], 2).cleanroom(sterile).duration(200).EUt(VA[UHV]).save(q);
        CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:gtca/optical_assembly")
                .inputItems(S[0]).inputItems(S[8], 2).inputItems(S[5], 6).inputItems(S[4], 8).inputItems(S[6], 16)
                .inputItems(wireFine, wire, 16).inputFluids(GTMaterials.SolderingAlloy.getFluid(L))
                .outputItems(S[9]).cleanroom(sterile).duration(400).EUt(VA[UHV]).save(q);
        var r = with(p, "gtca", "gtca:optical_circuit_board", "gtca:optical_assembly", "gtca:optical_smd_diode",
                "gtca:accurate_optical_ram", "gtca:optical_supercomputer", "gtca:optical_smd_inductor",
                "gtca:optical_smd_capacitor", "gtca:optical_smd_transistor", "gtca:optical_smd_resistor", "gtca:optical_mainframe");
        CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:gtca/optical_supercomputer")
                .inputItems(S[0], 2).inputItems(S[1], 2).inputItems(S[2], 8).inputItems(S[3], 16)
                .inputItems(wireFine, wire, 24).inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 2))
                .outputItems(S[4]).cleanroom(sterile).duration(600).EUt(VA[UEV]).save(r);
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:gtca/optical_mainframe")
                .inputItems(frameGt, GTMaterials.Neutronium, 2).inputItems(S[4], 2).inputItems(S[5], 32).inputItems(S[6], 32)
                .inputItems(S[7], 32).inputItems(S[8], 32).inputItems(S[2], 32).inputItems(S[3], 32)
                .inputItems(wireGtSingle, GTMaterials.Europium, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 20)).inputFluids(GTMaterials.Polybenzimidazole.getFluid(L * 8))
                .outputItems(S[9]).duration(2400).EUt(VA[UEV]).save(r);
    }

    // ---------------- GTMThings wireless covers, UHV-OpV ----------------
    private static void wirelessCovers(Consumer<FinishedRecipe> p) {
        var mm = com.gregtechceu.gtceu.api.GTCEuAPI.materialManager;
        String[] names = { "uhv", "uev", "uiv", "uxv", "opv" };
        int[] tiers = { UHV, UEV, UIV, UXV, OpV };
        Material[] plates = { GTMaterials.Neutronium, mm.getMaterial("gtna:infinity"), mm.getMaterial("gtna:transcendent_metal"),
                mm.getMaterial("gtna:space_time"), mm.getMaterial("gtna:universium") };
        var sensors = new com.tterrag.registrate.util.entry.ItemEntry[] { GTItems.SENSOR_UHV, GTItems.SENSOR_UEV, GTItems.SENSOR_UIV,
                GTItems.SENSOR_UXV, GTItems.SENSOR_OpV };
        var emitters = new com.tterrag.registrate.util.entry.ItemEntry[] { GTItems.EMITTER_UHV, GTItems.EMITTER_UEV, GTItems.EMITTER_UIV,
                GTItems.EMITTER_UXV, GTItems.EMITTER_OpV };
        var circuits = new net.minecraft.tags.TagKey[] { CustomTags.UHV_CIRCUITS, CustomTags.UEV_CIRCUITS, CustomTags.UIV_CIRCUITS,
                CustomTags.UXV_CIRCUITS, CustomTags.OpV_CIRCUITS };
        for (int i = 0; i < names.length; i++) {
            String n = names[i];
            Material plateMat = plates[i] != null && has(plate, plates[i]) ? plates[i] : GTMaterials.Neutronium;
            String cover = "gtmthings:" + n + "_wireless_energy_receive_cover";
            String cover4 = "gtmthings:" + n + "_4a_wireless_energy_receive_cover";
            ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:gtmthings/wireless_energy_receive_cover_" + n)
                    .inputItems(sensors[i].asStack()).inputItems(emitters[i].asStack()).inputItems(plate, GTMaterials.EnderPearl, 4)
                    .inputItems((net.minecraft.tags.TagKey<Item>) circuits[i], 2).inputItems(GTItems.VOLTAGE_COIL_UV.asStack(2))
                    .inputItems(GTItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT.asStack(4))
                    .inputItems(cableGtSingle, GTMaterials.Europium, 2).inputItems(plate, plateMat, 4)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 2)).outputItems(S[0])
                    .duration(200).EUt(VA[tiers[i]]).save(with(p, "gtmthings", cover));
            ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:gtmthings/wireless_energy_receive_cover_" + n + "_4a")
                    .inputItems(S[0], 2).inputItems(GTItems.ADVANCED_SMD_INDUCTOR.asStack(64))
                    .inputItems(cableGtQuadruple, GTMaterials.Europium, 4).inputItems(GTItems.VOLTAGE_COIL_UV.asStack(4))
                    .inputItems(plateDouble, plateMat, 2).inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 2))
                    .outputItems(S[1]).duration(200).EUt(VA[tiers[i]]).save(with(p, "gtmthings", cover, cover4));
        }
    }
}
