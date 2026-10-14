package com.runiccuriosities_pck.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.runiccuriosities_pck.GolemLaserEntity;
import com.runiccuriosities_pck.RunicCuriosities;
import com.runiccuriosities_pck.client.model.Modelgolem_laser;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GolemLaserRenderer extends EntityRenderer<GolemLaserEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(RunicCuriosities.MODID, "textures/entity/golem_laser.png");
	private final Modelgolem_laser<GolemLaserEntity> model;

	public GolemLaserRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new Modelgolem_laser<>(context.bakeLayer(Modelgolem_laser.LAYER_LOCATION));
	}

	@Override
	protected int getBlockLightLevel(GolemLaserEntity entity, BlockPos pos) {
		return 15;
	}

	@Override
	public void render(GolemLaserEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();

		float yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
		float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

		poseStack.mulPose(Axis.YP.rotationDegrees(yRot - 90.0F));
		poseStack.mulPose(Axis.ZP.rotationDegrees(xRot));

		VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.eyes(TEXTURE));
		this.model.renderToBuffer(poseStack, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(GolemLaserEntity entity) {
		return TEXTURE;
	}
}