package com.runiccuriosities_pck;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    // In NeoForge 1.21.1 si usa Registries.ENTITY_TYPE
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, RunicCuriosities.MODID);

    // I RegistryObject diventano Supplier o DeferredHolder. Supplier è il modo più pulito!
    public static final Supplier<EntityType<SaviritiumGolemEntity>> SAVIRITIUM_GOLEM =
            ENTITY_TYPES.register("saviritium_golem",
                    () -> EntityType.Builder.of(SaviritiumGolemEntity::new, MobCategory.CREATURE)
                            .sized(1.0f, 1.0f) // Larghezza e altezza dell'hitbox del golem
                            .build("saviritium_golem"));

    // REGISTRAZIONE DEL LASER (GolemLaserEntity)
    public static final Supplier<EntityType<GolemLaserEntity>> GOLEM_LASER =
            ENTITY_TYPES.register("golem_laser",
                    () -> EntityType.Builder.<GolemLaserEntity>of(GolemLaserEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f) // Hitbox piccolina per il proiettile
                            .clientTrackingRange(4)
                            .updateInterval(1)
                            // .setShouldReceiveVelocityUpdates(true) rimosso perché ora è automatico!
                            .build("golem_laser"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}