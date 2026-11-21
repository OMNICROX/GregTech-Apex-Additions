package com.omnicrox.gtaa.common.entity;

import com.omnicrox.gtaa.GTAA;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class GTAAEntities {

    private GTAAEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GTAA.MOD_ID);

    public static final RegistryObject<EntityType<AscendingMaxwellEntity>> ASCENDING_MAXWELL = ENTITIES.register("ascending_maxwell",
            () -> EntityType.Builder.<AscendingMaxwellEntity>of(AscendingMaxwellEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.6f).clientTrackingRange(10).updateInterval(1).fireImmune()
                    .build(GTAA.id("ascending_maxwell").toString()));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
