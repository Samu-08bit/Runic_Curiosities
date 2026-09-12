package com.runiccuriosities_pck;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class TranslucentArmorItem extends ArmorItem {

    // In 1.21.1 ArmorMaterial deve essere passato come Holder
    public TranslucentArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    // Questo è un trucco speciale fornito da NeoForge per intercettare il rendering
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                return new TranslucentArmorModel(armorSlot);
            }
        });
    }

    // Il nostro modello segreto che usa la trasparenza
    private static class TranslucentArmorModel extends HumanoidModel<LivingEntity> {
        private final EquipmentSlot slot;

        public TranslucentArmorModel(EquipmentSlot slot) {
            // Recupera la struttura standard del manichino di Minecraft
            super(Minecraft.getInstance().getEntityModels().bakeLayer(
                    slot == EquipmentSlot.LEGS ?
                            net.minecraft.client.model.geom.ModelLayers.PLAYER_INNER_ARMOR :
                            net.minecraft.client.model.geom.ModelLayers.PLAYER_OUTER_ARMOR
            ));
            this.slot = slot;
        }

        // In 1.21.1 il metodo ha perso i parametri red, green, blue a favore di un unico colore "color" argb.
        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
            // Capisce se deve caricare i pantaloni o il resto
            String textureName = (this.slot == EquipmentSlot.LEGS) ? "saviritium_layer_2.png" : "saviritium_layer_1.png";

            // 1.21.1 ResourceLocation
            ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "textures/models/armor/" + textureName);

            // IL TRUCCO MAGICO: Ignoriamo il "buffer" base (opaco) e chiediamo al gioco un buffer "Translucent" (Vetro)
            MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            VertexConsumer translucentBuffer = bufferSource.getBuffer(RenderType.entityTranslucent(textureLocation));

            // Disegniamo l'armatura con la vera trasparenza
            super.renderToBuffer(poseStack, translucentBuffer, packedLight, packedOverlay, color);
        }
    }
}