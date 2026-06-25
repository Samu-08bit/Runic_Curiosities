package com.runiccuriosities_pck;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide()) return;

        // Verifica se l'entità è attualmente bloccata nel tempo (Slowness >= 127)
        if (entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) &&
                entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() >= 127) {

            float incomingDamage = event.getAmount();
            float currentHealth = entity.getHealth();

            // Se l'entità è già stata "segnata per morire", blocchiamo ulteriori danni in modo
            // che tu possa continuare a colpirla senza che muoia o subisca veri danni aggiuntivi.
            if (entity.getPersistentData().getBoolean("QueuedToDie")) {
                event.setCanceled(true);
                return;
            }

            // Se il colpo è fatale (danno maggiore o uguale alla salute attuale)
            if (incomingDamage >= currentHealth) {
                // Annulliamo l'evento fatale in modo che Minecraft non attivi la morte reale
                event.setCanceled(true);

                // Memorizziamo i dettagli del danno letale per applicarli quando scade il tempo
                entity.getPersistentData().putBoolean("QueuedToDie", true);
                entity.getPersistentData().putFloat("StoredLethalDamage", incomingDamage);

                // Manteniamo l'entità tecnicamente in vita a mezzo cuore (1.0f)
                entity.setHealth(1.0f);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;

        // Usa >= 127 così siamo blindati su qualsiasi valore alto che hai usato su ModCommands/Packet
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
            entity.getPersistentData().putBoolean("HadGravityDisabled", true);

            // SPEGNE IL "CERVELLO" (IA) DEI MOSTRI: Niente attacchi, rotazioni, ne lancio pozioni (Streghe)
            if (entity instanceof Mob mob) {
                if (!entity.getPersistentData().getBoolean("HadAiDisabled")) {
                    mob.setNoAi(true);
                    entity.getPersistentData().putBoolean("HadAiDisabled", true);
                }
            }
        } else {
            // == UNFREEZE LOGIC ==

            if (entity.getPersistentData().getBoolean("HadGravityDisabled")) {
                entity.setNoGravity(false);
                entity.getPersistentData().remove("HadGravityDisabled");
            }

            // Riaccendi il cervello dell'IA quando l'effetto svanisce
            if (entity.getPersistentData().getBoolean("HadAiDisabled")) {
                if (entity instanceof Mob mob) {
                    mob.setNoAi(false);
                }
                entity.getPersistentData().remove("HadAiDisabled");
            }
        }

        // Gestione dei cadaveri in piedi ritardati durante il timestop
        if (entity.getPersistentData().getBoolean("QueuedToDie")) {

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
                    float lethalDamage = entity.getPersistentData().getFloat("StoredLethalDamage");

                    // Pulisci i tag per evitare loop infiniti di danni
                    entity.getPersistentData().remove("QueuedToDie");
                    entity.getPersistentData().remove("StoredLethalDamage");

                    // Riapplica la mazzata di grazia usando danno generico per triggerare il drop vero
                    entity.hurt(entity.damageSources().generic(), lethalDamage + 10.0f);
                }
            }
        }
    }
}