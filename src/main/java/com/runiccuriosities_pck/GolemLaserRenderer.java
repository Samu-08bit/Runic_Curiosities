package com.runiccuriosities_pck;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GolemLaserRenderer extends ArrowRenderer<GolemLaserEntity> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "textures/entity/golem_laser.png");

    public GolemLaserRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(GolemLaserEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, 15728880);
    }

    @Override
    public ResourceLocation getTextureLocation(GolemLaserEntity entity) {
        return TEXTURE;
    }
}