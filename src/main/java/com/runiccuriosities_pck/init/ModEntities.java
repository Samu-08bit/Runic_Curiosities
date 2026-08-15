package com.runiccuriosities_pck;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RunicCuriosities.MODID);

    public static final RegistryObject<EntityType<SaviritiumGolemEntity>> SAVIRITIUM_GOLEM =
            ENTITY_TYPES.register("saviritium_golem",
                    () -> EntityType.Builder.of(SaviritiumGolemEntity::new, MobCategory.CREATURE)
                            .sized(1.0f, 1.0f) // Larghezza e altezza dell'hitbox del golem
                            .build("saviritium_golem"));

    // REGISTRAZIONE DEL LASER (GolemLaserEntity)
    public static final RegistryObject<EntityType<GolemLaserEntity>> GOLEM_LASER =
            ENTITY_TYPES.register("golem_laser",
                    () -> EntityType.Builder.<GolemLaserEntity>of(GolemLaserEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f) // Hitbox piccolina per il proiettile
                            .clientTrackingRange(4)
                            .updateInterval(1)
                            .setShouldReceiveVelocityUpdates(true)
                            .build("golem_laser"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}