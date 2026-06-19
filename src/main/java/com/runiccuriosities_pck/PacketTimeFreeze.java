package com.runiccuriosities_pck;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import java.util.List;
import java.util.function.Supplier;

public class PacketTimeFreeze {

    // Standard empty constructor
    public PacketTimeFreeze() {}

    // Constructor reading from buffer, required by ModMessages (PacketTimeFreeze::new)
    public PacketTimeFreeze(FriendlyByteBuf buf) {}

    // Encoder method expected by ModMessages
    public void toBytes(FriendlyByteBuf buf) {}

    public static void handle(PacketTimeFreeze msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {

                // Check if the Time Hourglass item is currently on cooldown for the player
                if (player.getCooldowns().isOnCooldown(ModItems.TIME_HOURGLASS.get())) {
                    // Abort execution if the 3-minute cooldown hasn't expired yet
                    return;
                }

                // Apply the 3-minute cooldown (3 minutes * 60 seconds * 20 ticks = 3600 ticks)
                player.getCooldowns().addCooldown(ModItems.TIME_HOURGLASS.get(), 3600);

                // Get activation details and the server world/dimension where the player is
                Vec3 pos = player.position();
                ServerLevel level = player.serverLevel();

                // === TIME STOP SOUND EFFECTS ===
                // 1. Deep bass shockwave (End Portal Spawn pitched down)
                level.playSound(null, pos.x, pos.y, pos.z, net.minecraft.sounds.SoundEvents.END_PORTAL_SPAWN, net.minecraft.sounds.SoundSource.PLAYERS, 1.5F, 0.5F);
                // 2. Distorted clock chime (Bell Resonate pitched way down)
                level.playSound(null, pos.x, pos.y, pos.z, net.minecraft.sounds.SoundEvents.BELL_RESONATE, net.minecraft.sounds.SoundSource.PLAYERS, 2.0F, 0.1F);
                // ===============================

                // Modified freeze action radius around the player: set to 12 blocks
                double radius = 12.0;
                AABB freezeArea = player.getBoundingBox().inflate(radius);

                // Fetch all living entities inside the bounding box, excluding the executing player
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, freezeArea,
                        entity -> entity != player);

                for (LivingEntity entity : targets) {
                    // Reset custom NBT freeze tags to ensure a clean state for this new time stop session
                    if (entity.getPersistentData().contains("QueuedToDie")) {
                        entity.getPersistentData().remove("QueuedToDie");
                    }
                    if (entity.getPersistentData().contains("StoredLethalDamage")) {
                        entity.getPersistentData().remove("StoredLethalDamage");
                    }

                    // 1. Total Movement Freeze: Slowness 255 completely zeroes out movement speed and breaks AI pathfinding
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 255, false, false));

                    // 2. Blindness 5: (Amplifier 4 = Effect Level 5)
                    entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 4, false, false));

                    // 3. Glowing Effect: Highlights the frozen mobs in the dark rainbow area
                    entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 0, false, false));
                }

                // Sync the visual effects and timer execution to all connected clients in the dimension
                ModMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketSyncTimeFreeze(pos.x, pos.y, pos.z));
            }
        });
        context.setPacketHandled(true);
    }
}