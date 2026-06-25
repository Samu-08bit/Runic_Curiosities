package com.runiccuriosities_pck;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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

    // Questo è il metodo che il tuo ModEvents.java sta cercando di chiamare
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Registriamo la struttura esatta: /runic_curiosities time_hourglass timestop
        dispatcher.register(Commands.literal("runic_curiosities")
                .then(Commands.literal("time_hourglass")
                        .then(Commands.literal("timestop")
                                // Richiede i permessi da OP (livello 2) per essere eseguito
                                .requires(source -> source.hasPermission(2))
                                .executes(ModCommands::executeTimeStop)
                        )
                )
        );
    }

    private static int executeTimeStop(CommandContext<CommandSourceStack> context) {
        try {
            // Otteniamo il player che esegue il comando
            ServerPlayer player = context.getSource().getPlayerOrException();
            ServerLevel level = player.serverLevel();
            Vec3 pos = player.position();

            // === 1. EFFETTI SONORI (Identici al Talismano) ===
            level.playSound(null, pos.x, pos.y, pos.z, net.minecraft.sounds.SoundEvents.END_PORTAL_SPAWN, net.minecraft.sounds.SoundSource.PLAYERS, 1.5F, 0.5F);
            level.playSound(null, pos.x, pos.y, pos.z, net.minecraft.sounds.SoundEvents.BELL_RESONATE, net.minecraft.sounds.SoundSource.PLAYERS, 2.0F, 0.1F);

            // === 2. RAGGIO DI AZIONE (Identico al Talismano) ===
            double radius = 12.0;
            AABB freezeArea = player.getBoundingBox().inflate(radius);

            // === 3. SELEZIONE BERSAGLI (Incluso chi fa il comando) ===
            // A differenza del talismano, NON filtriamo "entity != player". Prendiamo tutti.
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, freezeArea);

            for (LivingEntity entity : targets) {
                // Puliamo i tag di sicurezza come nel talismano
                if (entity.getPersistentData().contains("QueuedToDie")) {
                    entity.getPersistentData().remove("QueuedToDie");
                }
                if (entity.getPersistentData().contains("StoredLethalDamage")) {
                    entity.getPersistentData().remove("StoredLethalDamage");
                }

                // === 4. APPLICAZIONE EFFETTI (Identici al Talismano) ===
                // Slowness 255: blocca completamente i movimenti
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 255, false, false));

                // Blindness 5 (Amplifier 4)
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 4, false, false));

                // Glowing
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 0, false, false));
            }

            // === 5. SINCRONIZZAZIONE CLIENT ===
            // Mandiamo il pacchetto per renderizzare il cubo arcobaleno e avviare il timer a schermo per tutti
            ModMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketSyncTimeFreeze(pos.x, pos.y, pos.z));

            return 1; // Comando eseguito con successo
        } catch (Exception e) {
            // Se il comando viene eseguito dalla console server (che non ha posizione) fallisce senza crashare
            return 0;
        }
    }
}