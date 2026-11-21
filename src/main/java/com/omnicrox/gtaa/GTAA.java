package com.omnicrox.gtaa;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import com.mojang.logging.LogUtils;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.data.GTAACreativeTab;
import com.omnicrox.gtaa.common.data.GTAAMachines;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * GregTech Apex Additions - GTNH-style late-game multiblocks for GregTech CEu Modern.
 * Several machines are ports of GT5-Unofficial / GT New Horizons (LGPL-3.0); see CREDITS.md.
 */
@Mod(GTAA.MOD_ID)
public class GTAA {

    public static final String MOD_ID = "gtapexadditions";
    public static final String NAME = "GregTech Apex Additions";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final GTRegistrate REGISTRATE = GTRegistrate.create(MOD_ID);

    public GTAA() {
        GTAACreativeTab.init();
        GTAABlocks.init();
        com.omnicrox.gtaa.common.data.GTAAItems.init();
        REGISTRATE.registerRegistrate();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addGenericListener(com.gregtechceu.gtceu.api.recipe.GTRecipeType.class, this::registerRecipeTypes);
        bus.addGenericListener(MachineDefinition.class, this::registerMachines);
        bus.addListener(this::commonSetup);
        com.omnicrox.gtaa.common.item.AngelRingItem.registerEvents();
        com.omnicrox.gtaa.common.item.ApexSuitItem.registerEvents();
        com.omnicrox.gtaa.common.infernal.InfernalMobs.register();
        com.omnicrox.gtaa.common.entity.GTAAEntities.register(bus);
        com.omnicrox.gtaa.common.stargate.ApexStargate.registerEvents();
        com.omnicrox.gtaa.common.network.GTAANetwork.register();
        com.omnicrox.gtaa.common.item.StructureUpgrader.registerEvents();
        com.omnicrox.gtaa.common.item.MaxwellItem.registerEvents();
        if (net.minecraftforge.fml.ModList.get().isLoaded("botania")) com.omnicrox.gtaa.common.compat.BotaniaCompat.init();
        if (net.minecraftforge.fml.ModList.get().isLoaded("botanicadds")) com.omnicrox.gtaa.common.compat.BotanicAdditionsCompat.init(bus);
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON,
                com.omnicrox.gtaa.common.infernal.InfernalConfig.SPEC, "gtapexadditions-infernal.toml");
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient()) {
            com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager.register(id("antimatter_core"),
                    com.omnicrox.gtaa.client.AntimatterCoreRender.TYPE);
            com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager.register(id("suit_hologram"),
                    com.omnicrox.gtaa.client.SuitHologramRender.TYPE);
        }
        bus.addListener(this::registerMaterialRegistry);
        bus.addListener(this::registerMaterials);
        bus.addListener(this::postMaterials);
    }

    private void registerMaterialRegistry(MaterialRegistryEvent event) {
        GTCEuAPI.materialManager.createRegistry(MOD_ID);
    }

    private void registerMaterials(MaterialEvent event) {
        GTAAMaterials.init();
    }

    private void postMaterials(com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent event) {
        GTAAMaterials.addFusionFluids();
        GTAAMaterials.resolveExternal();
    }

    private void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, com.gregtechceu.gtceu.api.recipe.GTRecipeType> event) {
        com.omnicrox.gtaa.common.data.GTAARecipeTypes.init();
    }

    private void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        GTAAMachines.init();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FusionReactorMachine.registerFusionTier(GTValues.UHV, " (MKIV)");
            FusionReactorMachine.registerFusionTier(GTValues.UEV, " (MKV)");
            com.omnicrox.gtaa.common.data.GTAARecipeTypes.DRACONIC_FUSION.linkProxy();
            if (!net.minecraftforge.fml.loading.FMLEnvironment.production) {
                // dev self-check: build the big JSON patterns once so mistakes show up at startup
                GTAAMachines.ANTIMATTER_FORGE.getPatternFactory().get();
                GTAAMachines.ANTIMATTER_GENERATOR.getPatternFactory().get();
                GTAAMachines.SPACE_ELEVATOR.getPatternFactory().get();
                GTAAMachines.SPACE_ELEVATOR_EXTENDED_PATTERN.get();
                GTAAMachines.SPACE_ASSEMBLER_MK1.getPatternFactory().get();
                for (var cf : GTAAMachines.COMPACT_FUSION) cf.getPatternFactory().get();
                GTAAMachines.LARGE_NAQUADAH_REACTOR.getPatternFactory().get();
                GTAAMachines.NAQUADAH_FUEL_REFINERY.getPatternFactory().get();
                GTAAMachines.COMPONENT_ASSEMBLY_LINE.getPatternFactory().get();
                GTAAMachines.QUANTUM_FORCE_TRANSFORMER.getPatternFactory().get();
                for (var m : new com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition[] { GTAAMachines.PURIFICATION_PLANT,
                        GTAAMachines.PURIFICATION_CLARIFIER, GTAAMachines.PURIFICATION_OZONATION, GTAAMachines.PURIFICATION_FLOCCULATION,
                        GTAAMachines.PURIFICATION_PH, GTAAMachines.PURIFICATION_PLASMA, GTAAMachines.PURIFICATION_UV,
                        GTAAMachines.PURIFICATION_DEGASSER, GTAAMachines.PURIFICATION_BARYONIC, GTAAMachines.MEGA_DISTILLATION_TOWER,
                        GTAAMachines.EXTREME_ENTITY_CRUSHER, GTAAMachines.DRACONIC_FUSION_CRAFTER,
                        GTAAMachines.MYSTICAL_CULTIVATION_ARRAY, GTAAMachines.SUIT_FABRICATION_STATION }) m.getPatternFactory().get();
                LOGGER.info("[GTAA] antimatter patterns built OK");
            }
        });
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
