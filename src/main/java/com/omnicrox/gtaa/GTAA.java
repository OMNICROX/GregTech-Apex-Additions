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
        bus.addListener(this::registerMaterialRegistry);
        bus.addListener(this::registerMaterials);
    }

    private void registerMaterialRegistry(MaterialRegistryEvent event) {
        GTCEuAPI.materialManager.createRegistry(MOD_ID);
    }

    private void registerMaterials(MaterialEvent event) {
        GTAAMaterials.init();
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
            if (!net.minecraftforge.fml.loading.FMLEnvironment.production) {
                // dev self-check: build the big JSON patterns once so mistakes show up at startup
                GTAAMachines.ANTIMATTER_FORGE.getPatternFactory().get();
                GTAAMachines.ANTIMATTER_GENERATOR.getPatternFactory().get();
                GTAAMachines.SPACE_ELEVATOR.getPatternFactory().get();
                GTAAMachines.SPACE_ELEVATOR_EXTENDED_PATTERN.get();
                GTAAMachines.SPACE_ASSEMBLER_MK1.getPatternFactory().get();
                LOGGER.info("[GTAA] antimatter patterns built OK");
            }
        });
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
