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

    public PacketTimeFreeze() {}
    public PacketTimeFreeze(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public static void handle(PacketTimeFreeze msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {

                if (player.getCooldowns().isOnCooldown(ModItems.TIME_HOURGLASS.get())) {
                    return;
                }
                player.getCooldowns().addCooldown(ModItems.TIME_HOURGLASS.get(), 3600);

                Vec3 pos = player.position();
                ServerLevel level = player.serverLevel();

                level.playSound(null, pos.x, pos.y, pos.z, net.minecraft.sounds.SoundEvents.END_PORTAL_SPAWN, net.minecraft.sounds.SoundSource.PLAYERS, 1.5F, 0.5F);
                level.playSound(null, pos.x, pos.y, pos.z, net.minecraft.sounds.SoundEvents.BELL_RESONATE, net.minecraft.sounds.SoundSource.PLAYERS, 2.0F, 0.1F);

                double radius = 12.0;
                AABB freezeArea = player.getBoundingBox().inflate(radius);

                // Il talismano colpisce tutti TRANNE il player che lo lancia
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, freezeArea, entity -> entity != player);

                for (LivingEntity entity : targets) {
                    entity.getPersistentData().remove("QueuedToDie");
                    entity.getPersistentData().remove("StoredLethalDamage");

                    // Nuovi effetti ottimizzati per bloccare salto e camminata
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 127, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 300, 200, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 4, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 0, false, false));

                    entity.getPersistentData().putInt("RunicTimeFreeze", 300);
                    entity.getPersistentData().putBoolean("HadGravityDisabled", true);
                    entity.setNoGravity(true);
                }

                ModMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketSyncTimeFreeze(pos.x, pos.y, pos.z));
            }
        });
        context.setPacketHandled(true);
    }
}