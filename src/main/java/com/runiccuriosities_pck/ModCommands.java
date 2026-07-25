package com.runiccuriosities_pck;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Crea il comando /runic_curiosities time_hourglass timestop
        dispatcher.register(Commands.literal("runic_curiosities")
                .requires(source -> source.hasPermission(2)) // Solo per gli OP
                .then(Commands.literal("time_hourglass")
                        .then(Commands.literal("timestop")
                                .executes(context -> executeTimeStop(context.getSource()))
                        )
                )
        );
    }

    private static int executeTimeStop(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            Vec3 pos = player.position();
            ServerLevel level = player.serverLevel();

            // Suoni ambientali spaziali
            level.playSound(null, pos.x, pos.y, pos.z, net.minecraft.sounds.SoundEvents.END_PORTAL_SPAWN, net.minecraft.sounds.SoundSource.PLAYERS, 1.5F, 0.5F);
            level.playSound(null, pos.x, pos.y, pos.z, net.minecraft.sounds.SoundEvents.BELL_RESONATE, net.minecraft.sounds.SoundSource.PLAYERS, 2.0F, 0.1F);

            // Raggio di 12 blocchi come il talismano
            double radius = 12.0;
            AABB freezeArea = player.getBoundingBox().inflate(radius);

            // Recupera TUTTI, INCLUSO IL PLAYER CHE FA IL COMANDO (non c'è il filtro)
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, freezeArea);

            for (LivingEntity entity : targets) {
                // Pulisci tag per sicurezza
                entity.getPersistentData().remove("QueuedToDie");
                entity.getPersistentData().remove("StoredLethalDamage");

                // Slowness 127 annulla la velocità di movimento a piedi
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 127, false, false));
                // Jump Boost 200 annulla totalmente la capacità di saltare nel motore di Minecraft
                entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 300, 200, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 4, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 0, false, false));

                // Congela mezz'aria ed elimina la gravità
                entity.getPersistentData().putInt("RunicTimeFreeze", 300);
                entity.getPersistentData().putBoolean("HadGravityDisabled", true);
                entity.setNoGravity(true);
            }

            // Manda l'animazione del cubo arcobaleno ai client
            ModMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketSyncTimeFreeze(pos.x, pos.y, pos.z));

            source.sendSuccess(() -> Component.literal("Time stop activated! Time has stopped."), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("Only one player can use this command!"));
            return 0;
        }
    }
}