package com.runiccuriosities_pck;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public class PacketTimeFreeze implements CustomPacketPayload {

    // 1. Definisci il TYPE univoco
    public static final Type<PacketTimeFreeze> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "time_freeze"));

    // 2. Lo StreamCodec (vuoto perché non inviamo dati in questo pacchetto, è solo un segnale)
    public static final StreamCodec<FriendlyByteBuf, PacketTimeFreeze> STREAM_CODEC = StreamCodec.ofMember(
            PacketTimeFreeze::write,
            PacketTimeFreeze::new
    );

    public PacketTimeFreeze() {}
    public PacketTimeFreeze(FriendlyByteBuf buf) {}
    public void write(FriendlyByteBuf buf) {}

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {

                if (player.getCooldowns().isOnCooldown(ModItems.TIME_HOURGLASS.get())) {
                    return;
                }
                player.getCooldowns().addCooldown(ModItems.TIME_HOURGLASS.get(), 3600);

                Vec3 pos = player.position();
                ServerLevel level = player.serverLevel();

                // Recuperiamo il suono custom
                net.minecraft.sounds.SoundEvent chosenSound = ModCommands.getHourglassSound(player, net.minecraft.sounds.SoundEvents.END_PORTAL_SPAWN);
                level.playSound(null, pos.x, pos.y, pos.z, chosenSound, net.minecraft.sounds.SoundSource.PLAYERS, 1.5F, 0.5F);
                level.playSound(null, pos.x, pos.y, pos.z, net.minecraft.sounds.SoundEvents.BELL_RESONATE, net.minecraft.sounds.SoundSource.PLAYERS, 2.0F, 0.1F);

                double radius = 12.0;
                AABB freezeArea = player.getBoundingBox().inflate(radius);

                // Il talismano colpisce tutti TRANNE il player che lo lancia
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, freezeArea, entity -> entity != player);

                for (LivingEntity entity : targets) {
                    // 1.21.1: Usiamo i nuovi Attachments definiti in ModCommands al posto dei vecchi PersistentData!
                    entity.setData(ModCommands.QUEUED_TO_DIE, false);
                    entity.setData(ModCommands.STORED_LETHAL_DAMAGE, 0.0f);

                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 127, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 300, 200, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 4, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 0, false, false));

                    entity.setData(ModCommands.RUNIC_TIME_FREEZE, 300);
                    entity.setData(ModCommands.HAD_GRAVITY_DISABLED, true);
                    entity.setNoGravity(true);
                }

                // In 1.21.1 il PacketDistributor si usa così (molto più facile!)
                PacketDistributor.sendToAllPlayers(new PacketSyncTimeFreeze(pos.x, pos.y, pos.z));
            }
        });
    }
}