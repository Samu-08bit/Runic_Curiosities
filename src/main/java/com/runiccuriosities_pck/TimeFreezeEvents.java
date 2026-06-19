package com.runiccuriosities_pck;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob; // Aggiunto per poter spegnere la IA
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent; // Aggiunto per PvP/attacchi
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

    // NUOVO: Blocca in maniera assoluta qualsiasi attacco corpo a corpo o a distanza generato da un'entità congelata
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            // Se l'aggressore è sotto l'effetto del Time Stop, annulla il danno
            if (attacker.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) &&
                    attacker.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() == 255) {
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

        // Check if the entity is frozen in time via the standard Slowness 255 effect
        boolean isTimeFrozen = entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) &&
                entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() == 255;

        if (isTimeFrozen) {
            // MID-AIR FREEZE LOGIC:
            // Zero out any motion delta to freeze flying/jumping entities instantly in place
            entity.setDeltaMovement(Vec3.ZERO);

            // Restores position history to prevent clientside interpolation stuttering while frozen
            entity.xo = entity.getX();
            entity.yo = entity.getY();
            entity.zo = entity.getZ();

            // Suppress standard gravity mechanics so they don't fall down
            entity.setNoGravity(true);

            // NUOVO: Disabilita completamente l'IA. Questo impedisce a streghe di curarsi/lanciare pozioni,
            // agli scheletri di sparare, ai creeper di esplodere, ecc.
            if (!entity.level().isClientSide() && entity instanceof Mob mob) {
                mob.setNoAi(true);
            }

            // Re-enable gravity flag status mapping to ensure it gets cleared later if needed
            entity.getPersistentData().putBoolean("HadGravityDisabled", true);
        } else if (entity.getPersistentData().getBoolean("HadGravityDisabled")) {
            // UNFREEZE LOGIC:
            // Restore normal gravity mechanics once the time freeze effect finishes
            entity.setNoGravity(false);

            // NUOVO: Restituisci l'IA al mob terminato l'effetto del time stop
            if (!entity.level().isClientSide() && entity instanceof Mob mob) {
                mob.setNoAi(false);
            }

            entity.getPersistentData().remove("HadGravityDisabled");
        }

        // Handle the persistent lethal damage logic for dead corpses remaining during time stop
        if (entity.getPersistentData().getBoolean("QueuedToDie")) {

            // Re-verify if time stop is still active
            if (isTimeFrozen) {
                // WHILE TIME IS FROZEN:
                // Force the hurt time and death-like animation state on both logical sides
                entity.hurtTime = 10;

                // Keep health locked at 1.0f so standard ticking environments don't accidentally kill it prematurely
                entity.setHealth(1.0f);

                // Prevent any AI or independent rotation movements during this frozen state
                entity.yBodyRot = entity.yBodyRotO;
                entity.yHeadRot = entity.yHeadRotO;
            } else {
                // WHEN TIME STOP EXPIRES:
                if (!entity.level().isClientSide()) {
                    float lethalDamage = entity.getPersistentData().getFloat("StoredLethalDamage");

                    // Clean up custom NBT tags to avoid ticking loops
                    entity.getPersistentData().remove("QueuedToDie");
                    entity.getPersistentData().remove("StoredLethalDamage");

                    // Re-apply the lethal damage using a generic source to safely trigger the real death sequence
                    entity.hurt(entity.damageSources().generic(), lethalDamage + 10.0f);
                }
            }
        }
    }
}