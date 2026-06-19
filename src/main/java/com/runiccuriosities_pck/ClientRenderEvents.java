package com.runiccuriosities_pck;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientRenderEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientTimeFreezeManager.tick();
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id()) && !ClientTimeFreezeManager.activeStops.isEmpty()) {

            // Find the maximum remaining time among all active Time Stops
            int maxTicks = 0;
            for (ClientTimeFreezeManager.TimeStopInstance instance : ClientTimeFreezeManager.activeStops) {
                if (instance.ticksRemaining > maxTicks) {
                    maxTicks = instance.ticksRemaining;
                }
            }

            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;

            int seconds = (maxTicks + 19) / 20;
            String text = "TIME STOP: " + seconds + "s";

            int xPos = (event.getWindow().getGuiScaledWidth() - font.width(text)) / 2;
            int yPos = event.getWindow().getGuiScaledHeight() - 52;

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

            // Loop through every active Time Stop and render its cube
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

                // OUTSIDE FACES
                vertex(matrix, builder, min, min, max, r, g, b, alpha);
                vertex(matrix, builder, max, min, max, r, g, b, alpha);
                vertex(matrix, builder, max, max, max, r, g, b, alpha);
                vertex(matrix, builder, min, max, max, r, g, b, alpha);
                vertex(matrix, builder, min, max, min, r, g, b, alpha);
                vertex(matrix, builder, max, max, min, r, g, b, alpha);
                vertex(matrix, builder, max, min, min, r, g, b, alpha);
                vertex(matrix, builder, min, min, min, r, g, b, alpha);
                vertex(matrix, builder, max, min, min, r, g, b, alpha);
                vertex(matrix, builder, max, max, min, r, g, b, alpha);
                vertex(matrix, builder, max, max, max, r, g, b, alpha);
                vertex(matrix, builder, max, min, max, r, g, b, alpha);
                vertex(matrix, builder, min, min, min, r, g, b, alpha);
                vertex(matrix, builder, min, min, max, r, g, b, alpha);
                vertex(matrix, builder, min, max, max, r, g, b, alpha);
                vertex(matrix, builder, min, max, min, r, g, b, alpha);
                vertex(matrix, builder, min, max, max, r, g, b, alpha);
                vertex(matrix, builder, max, max, max, r, g, b, alpha);
                vertex(matrix, builder, max, max, min, r, g, b, alpha);
                vertex(matrix, builder, min, max, min, r, g, b, alpha);
                vertex(matrix, builder, min, min, min, r, g, b, alpha);
                vertex(matrix, builder, max, min, min, r, g, b, alpha);
                vertex(matrix, builder, max, min, max, r, g, b, alpha);
                vertex(matrix, builder, min, min, max, r, g, b, alpha);

                // INSIDE FACES
                vertex(matrix, builder, min, max, max, r, g, b, alpha);
                vertex(matrix, builder, max, max, max, r, g, b, alpha);
                vertex(matrix, builder, max, min, max, r, g, b, alpha);
                vertex(matrix, builder, min, min, max, r, g, b, alpha);
                vertex(matrix, builder, min, min, min, r, g, b, alpha);
                vertex(matrix, builder, max, min, min, r, g, b, alpha);
                vertex(matrix, builder, max, max, min, r, g, b, alpha);
                vertex(matrix, builder, min, max, min, r, g, b, alpha);
                vertex(matrix, builder, max, min, max, r, g, b, alpha);
                vertex(matrix, builder, max, max, max, r, g, b, alpha);
                vertex(matrix, builder, max, max, min, r, g, b, alpha);
                vertex(matrix, builder, max, min, min, r, g, b, alpha);
                vertex(matrix, builder, min, max, min, r, g, b, alpha);
                vertex(matrix, builder, min, max, max, r, g, b, alpha);
                vertex(matrix, builder, min, min, max, r, g, b, alpha);
                vertex(matrix, builder, min, min, min, r, g, b, alpha);
                vertex(matrix, builder, min, max, min, r, g, b, alpha);
                vertex(matrix, builder, max, max, min, r, g, b, alpha);
                vertex(matrix, builder, max, max, max, r, g, b, alpha);
                vertex(matrix, builder, min, max, max, r, g, b, alpha);
                vertex(matrix, builder, min, min, max, r, g, b, alpha);
                vertex(matrix, builder, max, min, max, r, g, b, alpha);
                vertex(matrix, builder, max, min, min, r, g, b, alpha);
                vertex(matrix, builder, min, min, min, r, g, b, alpha);

                poseStack.popPose();
            }

            bufferSource.endBatch(RenderType.lightning());
        }
    }

    private static void vertex(Matrix4f matrix, VertexConsumer builder, float x, float y, float z, int r, int g, int b, int a) {
        builder.vertex(matrix, x, y, z).color(r, g, b, a).endVertex();
    }
}