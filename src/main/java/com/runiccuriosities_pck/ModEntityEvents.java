package com.runiccuriosities_pck;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ModEntityEvents {

    @Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {
        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.SAVIRITIUM_GOLEM.get(), SaviritiumGolemEntity.createAttributes().build());
        }
    }

    @Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModClientEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.SAVIRITIUM_GOLEM.get(), SaviritiumGolemRenderer::new);
        }
    }
}