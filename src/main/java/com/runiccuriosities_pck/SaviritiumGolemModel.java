package com.runiccuriosities_pck;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SaviritiumGolemModel extends GeoModel<SaviritiumGolemEntity> {

    @Override
    public ResourceLocation getModelResource(SaviritiumGolemEntity object) {
        // Assicurati che il nome del file .geo.json esportato da blockbench sia esattamente questo (o modificalo qui)
        return new ResourceLocation(RunicCuriosities.MODID, "geo/saviritium_golem.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SaviritiumGolemEntity object) {
        // Assicurati che il nome della texture esportata da blockbench sia esattamente questo
        return new ResourceLocation(RunicCuriosities.MODID, "textures/entity/saviritium_golem.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SaviritiumGolemEntity animatable) {
        // Assicurati che il nome del file delle animazioni esportato da blockbench sia esattamente questo
        return new ResourceLocation(RunicCuriosities.MODID, "animations/saviritium_golem.animation.json");
    }
}