package com.runiccuriosities_pck;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SaviritiumGolemModel extends GeoModel<SaviritiumGolemEntity> {

    @Override
    public ResourceLocation getModelResource(SaviritiumGolemEntity object) {
        // 1.21.1: fromNamespaceAndPath
        return ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "geo/saviritium_golem.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SaviritiumGolemEntity object) {
        return ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "textures/entity/saviritium_golem.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SaviritiumGolemEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "animations/saviritium_golem.animation.json");
    }
}