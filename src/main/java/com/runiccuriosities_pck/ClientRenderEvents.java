package com.runiccuriosities_pck;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = RunicCuriosities.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientRenderEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ClientTimeFreezeManager.tick();
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiLayerEvent.Post event) {
        if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR) && !ClientTimeFreezeManager.activeStops.isEmpty()) {
            int maxTicks = 0;
            for (ClientTimeFreezeManager.TimeStopInstance instance : ClientTimeFreezeManager.activeStops) {
                if (instance.ticksRemaining > maxTicks) { maxTicks = instance.ticksRemaining; }
            }
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;
            int seconds = (maxTicks + 19) / 20;
            String text = "TIME STOP: " + seconds + "s";
            int xPos = (event.getGuiGraphics().guiWidth() - font.width(text)) / 2;
            int yPos = event.getGuiGraphics().guiHeight() - 52;
            long sysTime = System.currentTimeMillis();

            for (int i = 0; i < text.length(); i++) {
                String ch = String.valueOf(text.charAt(i));
                float hue = ((sysTime + (i * 120)) % 2000) / 2000f;
                int color = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
                event.getGuiGraphics().drawString(font, ch, xPos, yPos, color, true);
                xPos += font.width(ch);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES && !ClientTimeFreezeManager.activeStops.isEmpty()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            PoseStack poseStack = event.getPoseStack();
            Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();
            var bufferSource = mc.renderBuffers().bufferSource();
            VertexConsumer builder = bufferSource.getBuffer(RenderType.lightning());

            for (ClientTimeFreezeManager.TimeStopInstance instance : ClientTimeFreezeManager.activeStops) {
                poseStack.pushPose();
                poseStack.translate(instance.x - camera.x, instance.y - camera.y, instance.z - camera.z);

                int elapsed = 300 - instance.ticksRemaining;
                double maxRadius = 12.0D;
                double radius = elapsed < 60 ? (elapsed / 60.0D) * maxRadius : maxRadius;
                long time = System.currentTimeMillis();
                float hue = (time % 4000) / 4000f;
                int rgb = java.awt.Color.HSBtoRGB(hue, 0.85f, 0.45f);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int alpha = 120;

                Matrix4f matrix = poseStack.last().pose();
                float min = (float) -radius;
                float max = (float) radius;

                float[][] vertices = {
                        {min, min, max}, {max, min, max}, {max, max, max}, {min, max, max},
                        {min, max, min}, {max, max, min}, {max, min, min}, {min, min, min},
                        {max, min, min}, {max, max, min}, {max, max, max}, {max, min, max},
                        {min, min, min}, {min, min, max}, {min, max, max}, {min, max, min},
                        {min, max, max}, {max, max, max}, {max, max, min}, {min, max, min},
                        {min, min, min}, {max, min, min}, {max, min, max}, {min, min, max},
                        {min, max, max}, {max, max, max}, {max, min, max}, {min, min, max},
                        {min, min, min}, {max, min, min}, {max, max, min}, {min, max, min},
                        {max, min, max}, {max, max, max}, {max, max, min}, {max, min, min},
                        {min, max, min}, {min, max, max}, {min, min, max}, {min, min, min},
                        {min, max, min}, {max, max, min}, {max, max, max}, {min, max, max},
                        {min, min, max}, {max, min, max}, {max, min, min}, {min, min, min}
                };

                for (float[] v : vertices) {
                    builder.addVertex(matrix, v[0], v[1], v[2]).setColor(r, g, b, alpha);
                }
                poseStack.popPose();
            }
            bufferSource.endBatch(RenderType.lightning());
        }
    }

    // DISATTIVATO COMPLETAMENTE PER COMPILARE
    @EventBusSubscriber(modid = RunicCuriosities.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientSetupEvents {
        @SubscribeEvent
        public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        }
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        }
    }
}