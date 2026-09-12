package com.runiccuriosities_pck;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SaviritiumGolemRenderer extends GeoEntityRenderer<SaviritiumGolemEntity> {
    public SaviritiumGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SaviritiumGolemModel());
        this.shadowRadius = 0.5f; // Grandezza dell'ombra sotto l'entità
    }
}