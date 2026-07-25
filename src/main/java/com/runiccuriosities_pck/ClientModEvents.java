package com.runiccuriosities_pck;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class ClientModEvents {

    // 1. Bus FORGE: Per gli eventi "in-game" (es. overlay rosso sullo schermo)
    @Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
            if (event.getOverlay().id().equals(VanillaGuiOverlay.VIGNETTE.id())) {
                Minecraft mc = Minecraft.getInstance();
                LocalPlayer player = mc.player;

                if (player != null && mc.level != null) {
                    if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                        boolean hasEyes = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.SCARLET_EYES.get()).isPresent();
                        if (hasEyes) {
                            int width = event.getWindow().getGuiScaledWidth();
                            int height = event.getWindow().getGuiScaledHeight();
                            event.getGuiGraphics().fill(RenderType.guiOverlay(), 0, 0, width, height, 0x66FF0000);
                        }
                    }
                }
            }
        }
    }

    // 2. Bus MOD: Per le registrazioni di setup lato client (es. Animazione elmetto)
    @Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientSetupEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                // Registriamo la proprietà "anim_state" per il Neptune's Helmet
                ItemProperties.register(ModItems.NEPTUNES_HELMET.get(), new ResourceLocation(RunicCuriosities.MODID, "anim_state"),
                        (itemStack, clientLevel, livingEntity, seed) -> {
                            if (itemStack.hasTag() && itemStack.getTag().contains("AnimState")) {
                                // Ritorna 0.0, 0.5 o 1.0 a seconda dello stato per cambiare texture
                                return itemStack.getTag().getInt("AnimState") / 2.0F;
                            }
                            return 0.0F;
                        });
            });
        }
    }
}