package com.runiccuriosities_pck;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = RunicCuriosities.MODID, bus = EventBusSubscriber.Bus.GAME)
public class TimeFreezeEvents {

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        LivingEntity entity = event.getEntity();

        // Identifica il Time Stop accettando qualsiasi amplificatore uguale o maggiore di 127
        if (entity != null && entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
            if (entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() >= 127) {
                event.setCanceled(true);
            }
        }
    }

    // In 1.21.1 si usa LivingIncomingDamageEvent invece di LivingHurtEvent
    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide()) return;

        // Verifica se l'entità è attualmente bloccata nel tempo (Slowness >= 127)
        if (entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) &&
                entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() >= 127) {

            float incomingDamage = event.getAmount();
            float currentHealth = entity.getHealth();

            // 1.21.1: Lettura dei dati tramite Attachments
            if (entity.getData(ModCommands.QUEUED_TO_DIE)) {
                event.setCanceled(true);
                return;
            }

            // Se il colpo è fatale (danno maggiore o uguale alla salute attuale)
            if (incomingDamage >= currentHealth) {
                // Annulliamo l'evento fatale in modo che Minecraft non attivi la morte reale
                event.setCanceled(true);

                // Memorizziamo i dettagli del danno letale tramite Attachments
                entity.setData(ModCommands.QUEUED_TO_DIE, true);
                entity.setData(ModCommands.STORED_LETHAL_DAMAGE, incomingDamage);

                // Manteniamo l'entità tecnicamente in vita a mezzo cuore (1.0f)
                entity.setHealth(1.0f);
            }
        }
    }

    // In 1.21.1 si usa EntityTickEvent.Post e controlliamo se è LivingEntity
    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        // Usa >= 127 così siamo blindati su qualsiasi valore alto
        boolean isTimeFrozen = entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) &&
                entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() >= 127;

        if (isTimeFrozen) {
            // == MID-AIR FREEZE LOGIC ==

            // Azzera la velocità per bloccare istantaneamente in volo le entità
            entity.setDeltaMovement(Vec3.ZERO);

            // Ripristina la posizione passata per evitare lo "stuttering" visivo nel client
            entity.xo = entity.getX();
            entity.yo = entity.getY();
            entity.zo = entity.getZ();

            // Sospendi la gravità per non farli cadere
            entity.setNoGravity(true);
            entity.setData(ModCommands.HAD_GRAVITY_DISABLED, true);

            // SPEGNE IL "CERVELLO" (IA) DEI MOSTRI
            if (entity instanceof Mob mob) {
                if (!entity.getData(ModCommands.HAD_AI_DISABLED)) {
                    mob.setNoAi(true);
                    entity.setData(ModCommands.HAD_AI_DISABLED, true);
                }
            }
        } else {
            // == UNFREEZE LOGIC ==

            if (entity.getData(ModCommands.HAD_GRAVITY_DISABLED)) {
                entity.setNoGravity(false);
                entity.setData(ModCommands.HAD_GRAVITY_DISABLED, false); // Resetta a false invece di remove
            }

            // Riaccendi il cervello dell'IA quando l'effetto svanisce
            if (entity.getData(ModCommands.HAD_AI_DISABLED)) {
                if (entity instanceof Mob mob) {
                    mob.setNoAi(false);
                }
                entity.setData(ModCommands.HAD_AI_DISABLED, false); // Resetta
            }
        }

        // Gestione dei cadaveri in piedi ritardati durante il timestop
        if (entity.getData(ModCommands.QUEUED_TO_DIE)) {

            // Ripesca lo stato del time stop
            if (isTimeFrozen) {
                // MENTRE IL TEMPO È BLOCCATO:
                // Forza l'animazione di "ferita" (colore rosso) bloccata nel tempo
                entity.hurtTime = 10;
                entity.setHealth(1.0f);

                // Blocca le rotazioni indipendenti del corpo e della testa
                entity.yBodyRot = entity.yBodyRotO;
                entity.yHeadRot = entity.yHeadRotO;
            } else {
                // QUANDO IL TEMPO RIPARTE:
                if (!entity.level().isClientSide()) {
                    float lethalDamage = entity.getData(ModCommands.STORED_LETHAL_DAMAGE);

                    // Pulisci i tag per evitare loop infiniti di danni
                    entity.setData(ModCommands.QUEUED_TO_DIE, false);
                    entity.setData(ModCommands.STORED_LETHAL_DAMAGE, 0.0f);

                    // Riapplica la mazzata di grazia
                    entity.hurt(entity.damageSources().generic(), lethalDamage + 10.0f);
                }
            }
        }
    }
}