package com.runiccuriosities_pck;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, RunicCuriosities.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<SaviritiumGolemEntity>> SAVIRITIUM_GOLEM =
            ENTITY_TYPES.register("saviritium_golem",
                    () -> EntityType.Builder.of(SaviritiumGolemEntity::new, MobCategory.CREATURE)
                            .sized(1.0f, 1.0f)
                            .build("saviritium_golem"));

    public static final DeferredHolder<EntityType<?>, EntityType<GolemLaserEntity>> GOLEM_LASER =
            ENTITY_TYPES.register("golem_laser",
                    () -> EntityType.Builder.<GolemLaserEntity>of(GolemLaserEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(4)
                            .updateInterval(1)
                            .build("golem_laser"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}