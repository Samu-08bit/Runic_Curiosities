package com.runiccuriosities_pck;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class GolemCommandPacket implements CustomPacketPayload {

    // 1. Definisci un TYPE univoco per questo pacchetto in 1.21.1
    public static final Type<GolemCommandPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "golem_command"));

    // 2. Lo StreamCodec serve al gioco per capire come trasformare la classe in byte e viceversa
    public static final StreamCodec<FriendlyByteBuf, GolemCommandPacket> STREAM_CODEC = StreamCodec.ofMember(
            GolemCommandPacket::write, // Come scrivere (encoding)
            GolemCommandPacket::new // Come leggere (decoding)
    );

    private final int entityId;
    private final int commandId; // 0 = Follow, 1 = Stay, 2 = Toggle Sit

    public GolemCommandPacket(int entityId, int commandId) {
        this.entityId = entityId;
        this.commandId = commandId;
    }

    // Costruttore per la LETTURA
    public GolemCommandPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.commandId = buf.readInt();
    }

    // Metodo per la SCRITTURA
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.commandId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // Il metodo HANDLE aggiornato a IPayloadContext per la 1.21.1
    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            // Il player in 1.21.1 si prende direttamente dal context (se è server side)
            if (ctx.player() instanceof ServerPlayer player) {
                Entity entity = player.level().getEntity(this.entityId);

                if (entity instanceof SaviritiumGolemEntity golem) {
                    if (golem.isOwnedBy(player)) {
                        if (this.commandId == 0) { // Follow Me
                            golem.setOrderedToSit(false);
                            golem.setStaying(false); // <--- Sync update
                            if (golem.isInSittingPose()) {
                                golem.setInSittingPose(false);
                                golem.setStandingUp(true);
                                golem.setStandUpTick(45);
                            }
                        } else if (this.commandId == 1) { // Stay
                            golem.setOrderedToSit(true);
                            golem.setStaying(true); // <--- Sync update
                            if (golem.isInSittingPose()) {
                                golem.setInSittingPose(false);
                                golem.setStandingUp(true);
                                golem.setStandUpTick(45);
                            }
                            golem.getNavigation().stop();
                        } else if (this.commandId == 2) { // Toggle Sit
                            boolean wasSitting = golem.isInSittingPose();
                            if (wasSitting) {
                                // Si alza e rimane in Stay
                                golem.setInSittingPose(false);
                                golem.setOrderedToSit(true);
                                golem.setStaying(true); // <--- Sync update
                                golem.setStandingUp(true);
                                golem.setStandUpTick(45);
                            } else {
                                // Si siede
                                golem.setInSittingPose(true);
                                golem.setOrderedToSit(true);
                                golem.setStaying(false); // <--- Sync update
                            }
                            golem.getNavigation().stop();
                        }
                    }
                }
            }
        });
    }
}