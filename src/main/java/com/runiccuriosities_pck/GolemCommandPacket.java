package com.runiccuriosities_pck;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public class GolemCommandPacket implements CustomPacketPayload {

    public static final Type<GolemCommandPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "golem_command"));

    public static final StreamCodec<FriendlyByteBuf, GolemCommandPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            GolemCommandPacket::getEntityId,
            ByteBufCodecs.VAR_INT,
            GolemCommandPacket::getCommandId,
            GolemCommandPacket::new
    );

    private final int entityId;
    private final int commandId;

    public GolemCommandPacket(int entityId, int commandId) {
        this.entityId = entityId;
        this.commandId = commandId;
    }

    public GolemCommandPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.commandId = buf.readInt();
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.commandId);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getCommandId() {
        return this.commandId;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext ctx) {
        handle(this, ctx);
    }


    public static void handle(final GolemCommandPacket packet, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                Entity entity = player.level().getEntity(packet.getEntityId());

                if (entity instanceof SaviritiumGolemEntity golem) {
                    if (golem.isOwnedBy(player)) {

                        if (packet.getCommandId() == 0) { // Follow
                            golem.setOrderedToSit(false);
                            golem.setStaying(false);

                            if (golem.isInSittingPose()) {
                                golem.setInSittingPose(false);
                                golem.setStandingUp(true);
                                golem.setStandUpTick(45);
                            }
                        } else if (packet.getCommandId() == 1) { // Stay
                            golem.setOrderedToSit(true);
                            golem.setStaying(true);

                            if (golem.isInSittingPose()) {
                                golem.setInSittingPose(false);
                                golem.setStandingUp(true);
                                golem.setStandUpTick(45);
                            }
                            golem.getNavigation().stop();
                        } else if (packet.getCommandId() == 2) { // Sit
                            boolean wasSitting = golem.isInSittingPose();

                            if (wasSitting) {
                                golem.setInSittingPose(false);
                                golem.setOrderedToSit(true);
                                golem.setStandingUp(true);
                                golem.setStandUpTick(45);
                            } else {
                                golem.setInSittingPose(true);
                                golem.setOrderedToSit(true);
                            }
                            golem.getNavigation().stop();
                        }
                    }
                }
            }
        });
    }
}