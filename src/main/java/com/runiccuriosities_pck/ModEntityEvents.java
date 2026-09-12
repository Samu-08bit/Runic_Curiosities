package com.runiccuriosities_pck;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

public class ModEntityEvents {

    @EventBusSubscriber(modid = RunicCuriosities.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {
        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.SAVIRITIUM_GOLEM.get(), SaviritiumGolemEntity.createAttributes().build());
        }
    }

    @EventBusSubscriber(modid = RunicCuriosities.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModClientEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.SAVIRITIUM_GOLEM.get(), SaviritiumGolemRenderer::new);
        }
    }
}