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
                            .sized(1.0f, 1.0f) // Qui imposti la larghezza e l'altezza dell'hitbox
                            .build("saviritium_golem"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}