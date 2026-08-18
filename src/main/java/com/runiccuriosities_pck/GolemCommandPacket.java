package com.runiccuriosities_pck;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GolemCommandPacket {
    private final int entityId;
    private final int commandId; // 0 = Follow, 1 = Stay, 2 = Toggle Sit

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

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

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
        });
        ctx.get().setPacketHandled(true);
    }
}