package com.runiccuriosities_pck;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
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

        // Cancel knockback if the entity has Slowness 255 (active time freeze)
        if (entity != null && entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
            if (entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() == 255) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide()) return;

        // Check if the entity is currently frozen in time
        if (entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) &&
                entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() == 255) {

            float incomingDamage = event.getAmount();
            float currentHealth = entity.getHealth();

            // If the entity is already flagged as "queued to die", prevent any further damage accumulation
            if (entity.getPersistentData().getBoolean("QueuedToDie")) {
                event.setCanceled(true);
                return;
            }

            // If the hit is fatal (damage is equal or greater than remaining health)
            if (incomingDamage >= currentHealth) {
                // Cancel the fatal damage event so Minecraft doesn't trigger clearEffects() or the standard death cycle
                event.setCanceled(true);

                // Store the lethal damage details inside NBT to apply them when the time stop expires
                entity.getPersistentData().putBoolean("QueuedToDie", true);
                entity.getPersistentData().putFloat("StoredLethalDamage", incomingDamage);

                // Set health to minimum survival value (0.5 hearts / 1.0f) to guarantee it stays alive but near-death
                entity.setHealth(1.0f);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;

        // Verifica se l'entità è sotto l'effetto del Time Stop (Slowness a livello 255)
        boolean isTimeFrozen = entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) &&
                entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() == 255;

        if (isTimeFrozen) {
            // 1. Azzera la gravità e i movimenti vettoriali generali
            entity.setDeltaMovement(Vec3.ZERO);
            entity.setNoGravity(true);
            entity.getPersistentData().putBoolean("HadGravityDisabled", true);

            // 2. BLOCCO IA PER I MOB: spegne letteralmente i "pensieri" della strega/zombie/ecc.
            if (entity instanceof Mob mob) {
                mob.setNoAi(true);
            }

            // 3. BLOCCO PER I GIOCATORI: Ti inchioda alle tue coordinate precedenti.
            // Dato che slowness impedisce di camminare ma non di saltare/scivolare,
            // forziamo la posizione a quella del tick appena passato, rendendo impossibile spostarsi.
            if (entity instanceof Player player) {
                player.setPos(player.xo, player.yo, player.zo);
            }

        } else if (entity.getPersistentData().getBoolean("HadGravityDisabled")) {
            // === SBLOCCO DEL TEMPO (Alla scadenza dei 15 secondi) ===

            // Ripristina la gravità per farli cadere a terra
            entity.setNoGravity(false);
            entity.getPersistentData().remove("HadGravityDisabled");

            // RIPRISTINA L'IA DEI MOB: FIX BUG (Senza questo, i mob rimangono paralizzati a vita!)
            if (entity instanceof Mob mob) {
                mob.setNoAi(false);
            }
        }

        // Gestione dei danni letali inflitti ai corpi durante il freeze (effetti visivi)
        if (entity.getPersistentData().getBoolean("QueuedToDie")) {
            if (isTimeFrozen) {
                entity.hurtTime = 10;
                entity.setHealth(1.0f);
                entity.yBodyRot = entity.yBodyRotO;
                entity.yHeadRot = entity.yHeadRotO;
            } else {
                if (!entity.level().isClientSide()) {
                    float lethalDamage = entity.getPersistentData().getFloat("StoredLethalDamage");
                    entity.getPersistentData().remove("QueuedToDie");
                    entity.getPersistentData().remove("StoredLethalDamage");
                    entity.hurt(entity.damageSources().generic(), lethalDamage + 10.0f);
                }
            }
        }
    }
}