package com.runiccuriosities_pck.client.renderer;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

import com.runiccuriosities_pck.GolemLaserEntity;
import com.runiccuriosities_pck.client.model.Modelgolem_laser;

public class GolemLaserRenderer extends EntityRenderer<GolemLaserEntity> {

	private static final ResourceLocation texture = new ResourceLocation("runic_curiosities:textures/entity/golem_laser.png");
	private final Modelgolem_laser model;

	public GolemLaserRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new Modelgolem_laser(context.bakeLayer(Modelgolem_laser.LAYER_LOCATION));
	}

	@Override
	public void render(GolemLaserEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		VertexConsumer vb = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
		poseStack.pushPose();

		// 1. Allinea il laser alla direzione di volo
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));

		// 2. Correzione altezza: se nello screenshot è volato troppo in alto, questo lo abbassa dentro l'hitbox!
		// (Se vedi che è andato TROPPO giù, cambia -1.5F in -1.0F o -0.5F)
		poseStack.translate(0.0F, -1.5F, 0.0F);

		// 3. Effetto Luminoso Fullbright (il laser non ha ombre)
		model.renderToBuffer(poseStack, vb, 15728880, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

		poseStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(GolemLaserEntity entity) {
		return texture;
	}
}