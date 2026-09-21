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

        if (entity != null && entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
            if (entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() >= 127) {
                event.setCanceled(true);
            }
        }
    }


    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide()) return;

        if (entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) &&
                entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() >= 127) {

            float incomingDamage = event.getAmount();
            float currentHealth = entity.getHealth();

            if (entity.getData(ModCommands.QUEUED_TO_DIE)) {
                event.setCanceled(true);
                return;
            }


            if (incomingDamage >= currentHealth) {

                event.setCanceled(true);


                entity.setData(ModCommands.QUEUED_TO_DIE, true);
                entity.setData(ModCommands.STORED_LETHAL_DAMAGE, incomingDamage);


                entity.setHealth(1.0f);
            }
        }
    }


    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        boolean isTimeFrozen = entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) &&
                entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() >= 127;

        if (isTimeFrozen) {
            // == MID-AIR FREEZE LOGIC ==

            entity.setDeltaMovement(Vec3.ZERO);

            entity.xo = entity.getX();
            entity.yo = entity.getY();
            entity.zo = entity.getZ();

            entity.setNoGravity(true);
            entity.setData(ModCommands.HAD_GRAVITY_DISABLED, true);

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
                entity.setData(ModCommands.HAD_GRAVITY_DISABLED, false);
            }

            if (entity.getData(ModCommands.HAD_AI_DISABLED)) {
                if (entity instanceof Mob mob) {
                    mob.setNoAi(false);
                }
                entity.setData(ModCommands.HAD_AI_DISABLED, false);
            }
        }

        if (entity.getData(ModCommands.QUEUED_TO_DIE)) {


            if (isTimeFrozen) {

                entity.hurtTime = 10;
                entity.setHealth(1.0f);

                entity.yBodyRot = entity.yBodyRotO;
                entity.yHeadRot = entity.yHeadRotO;
            } else {

                if (!entity.level().isClientSide()) {
                    float lethalDamage = entity.getData(ModCommands.STORED_LETHAL_DAMAGE);


                    entity.setData(ModCommands.QUEUED_TO_DIE, false);
                    entity.setData(ModCommands.STORED_LETHAL_DAMAGE, 0.0f);

                    entity.hurt(entity.damageSources().generic(), lethalDamage + 10.0f);
                }
            }
        }
    }
}