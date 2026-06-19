package com.runiccuriosities_pck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;

@Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
            Player player = event.player;

            // Enforce uniqueness for all custom registration items to prevent duplication exploits
            enforceUniqueCurio(player, ModItems.EXAMPLE_ITEM.get());
            enforceUniqueCurio(player, ModItems.GOLDEN_EMERALD.get());
            enforceUniqueCurio(player, ModItems.EGG_OF_GLUTTONY.get());
            enforceUniqueCurio(player, ModItems.SCARLET_EYES.get());
            enforceUniqueCurio(player, ModItems.IGNITOR_SHIELD.get());
            enforceUniqueCurio(player, ModItems.RECHARGING_BREAD.get());
            enforceUniqueCurio(player, ModItems.GLASS_CLOTH.get());
            enforceUniqueCurio(player, ModItems.GUARDIAN_GOLEM.get());
            enforceUniqueCurio(player, ModItems.CAR_BOMB.get());
            enforceUniqueCurio(player, ModItems.ENERGY_DRINK.get());
            enforceUniqueCurio(player, ModItems.TIME_HOURGLASS.get());

            // 1. Talisman of Intuition (Head Slot)
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.EXAMPLE_ITEM.get()).isPresent()) {
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, true, false, true));
            }

            // 2. Golden Emerald (Charm Slot)
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.GOLDEN_EMERALD.get()).isPresent()) {
                player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 40, 0, true, false, true));
            }

            // 3. Egg of Gluttony (Charm Slot)
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.EGG_OF_GLUTTONY.get()).isPresent()) {
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 40, 0, true, false, true));
            }

            // 4. Scarlet Eyes (Head Slot)
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.SCARLET_EYES.get()).isPresent() && player.level().isNight()) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 210, 0, true, false, false));
            }

            // 5. Ignitor Shield (Back Slot)
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.IGNITOR_SHIELD.get()).isPresent()) {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, true, false, true));
            }

            // 6. Recharging Bread (Charm Slot)
            var breadOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.RECHARGING_BREAD.get());
            if (breadOpt.isPresent()) {
                ItemStack breadStack = breadOpt.get().stack();
                CompoundTag nbt = breadStack.getOrCreateTag();

                if (!nbt.contains("Charge")) {
                    nbt.putInt("Charge", 1200);
                }

                int charge = nbt.getInt("Charge");

                if (charge > 0) {
                    charge--;
                    player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 39, 0, true, false, false));
                } else {
                    int wheatSlot = -1;
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack invStack = player.getInventory().getItem(i);
                        if (invStack.is(Items.WHEAT)) {
                            wheatSlot = i;
                            break;
                        }
                    }

                    if (wheatSlot != -1) {
                        player.getInventory().removeItem(wheatSlot, 1);
                        charge = 1200;
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.2F);
                    }
                }
                nbt.putInt("Charge", charge);
            }

            // 7. Glass Cloth (Body Slot)
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.GLASS_CLOTH.get()).isPresent()) {
                boolean isInLiquid = player.isInWater() || player.isInLava() || player.isInFluidType();

                if (isInLiquid) {
                    if (player.hasEffect(MobEffects.INVISIBILITY)) {
                        player.removeEffect(MobEffects.INVISIBILITY);
                    }
                } else {
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 39, 0, true, false, true));
                }
            }

            // 8. Guardian Golem (Necklace Slot)
            var golemOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.GUARDIAN_GOLEM.get());
            if (golemOpt.isPresent()) {
                ItemStack golemStack = golemOpt.get().stack();
                CompoundTag nbt = golemStack.getOrCreateTag();

                if (!nbt.contains("Uses")) {
                    nbt.putInt("Uses", 2);
                }

                if (nbt.contains("GolemTimer")) {
                    int timer = nbt.getInt("GolemTimer");
                    if (timer > 0) {
                        timer--;
                        nbt.putInt("GolemTimer", timer);

                        if (nbt.contains("ActiveGolemUUID")) {
                            try {
                                java.util.UUID uuid = nbt.getUUID("ActiveGolemUUID");
                                if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                                    net.minecraft.world.entity.Entity entity = serverLevel.getEntity(uuid);

                                    if (entity instanceof IronGolem guardian) {
                                        if (guardian.getTarget() == null) {
                                            double distance = guardian.distanceTo(player);
                                            if (distance > 16.0D) {
                                                guardian.teleportTo(player.getX(), player.getY(), player.getZ());
                                            } else if (distance > 4.0D) {
                                                guardian.getNavigation().moveTo(player, 1.25D);
                                            }
                                        }
                                    }

                                    if (timer == 0) {
                                        if (entity != null) {
                                            entity.discard();
                                        }
                                        nbt.remove("ActiveGolemUUID");
                                    }
                                }
                            } catch (Exception e) {
                                // Safely catch tracking errors
                            }
                        }
                    }
                }

                if (nbt.getInt("Uses") == 0) {
                    int ironSlot = -1;
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack invStack = player.getInventory().getItem(i);
                        if (invStack.is(Items.IRON_INGOT)) {
                            ironSlot = i;
                            break;
                        }
                    }

                    if (ironSlot != -1) {
                        player.getInventory().removeItem(ironSlot, 1);
                        nbt.putInt("Uses", 2);
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }

            // 9. Car Bomb (Belt Slot)
            var bombOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.CAR_BOMB.get());
            if (bombOpt.isPresent()) {
                ItemStack bombStack = bombOpt.get().stack();
                CompoundTag nbt = bombStack.getOrCreateTag();

                if (!nbt.contains("Uses")) {
                    nbt.putInt("Uses", 3);
                }

                if (nbt.getInt("Uses") == 0) {
                    int tntSlot = -1;
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack invStack = player.getInventory().getItem(i);
                        if (invStack.is(Items.TNT)) {
                            tntSlot = i;
                            break;
                        }
                    }

                    if (tntSlot != -1) {
                        player.getInventory().removeItem(tntSlot, 1);
                        nbt.putInt("Uses", 3);
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 1.0F, 1.2F);
                    }
                }
            }

            // 10. Energy Drink (Hands Slot - Continuous Jump Boost, Speed, and Haste)
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.ENERGY_DRINK.get()).isPresent()) {
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 39, 0, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 39, 0, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 39, 0, true, false, true));
            }

            // 11. Time Hourglass (Hands Slot - Soul Sand Auto-Recharge Logic)
            var hourglassOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.TIME_HOURGLASS.get());
            if (hourglassOpt.isPresent()) {
                ItemStack stack = hourglassOpt.get().stack();
                CompoundTag nbt = stack.getOrCreateTag();

                if (!nbt.contains("Uses")) {
                    nbt.putInt("Uses", 1);
                }

                if (nbt.getInt("Uses") == 0) {
                    int sandSlot = -1;
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack invStack = player.getInventory().getItem(i);
                        if (invStack.is(Items.SOUL_SAND)) {
                            sandSlot = i;
                            break;
                        }
                    }

                    if (sandSlot != -1) {
                        player.getInventory().removeItem(sandSlot, 1);
                        nbt.putInt("Uses", 1);
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        // Time freeze velocity tracking & AI isolation containment loops for mobs
        if (!entity.level().isClientSide && entity.getPersistentData().contains("RunicTimeFreeze")) {
            int ticks = entity.getPersistentData().getInt("RunicTimeFreeze");
            if (ticks > 0) {
                ticks--;
                entity.getPersistentData().putInt("RunicTimeFreeze", ticks);

                // Forcibly kill any accumulated velocity vectors to freeze them perfectly in mid-air
                entity.setDeltaMovement(0, entity.getDeltaMovement().y < 0 ? 0 : entity.getDeltaMovement().y, 0);

                if (ticks == 0) {
                    if (entity instanceof Mob mob) {
                        mob.setNoAi(false); // Restores active decision-making loops when time resumes
                    }
                    entity.getPersistentData().remove("RunicTimeFreeze");
                }
            }
        }
    }

    private static void enforceUniqueCurio(Player player, Item item) {
        List<SlotResult> equippedInstances = CuriosApi.getCuriosHelper().findCurios(player, item);
        if (equippedInstances.size() > 1) {
            for (int i = 1; i < equippedInstances.size(); i++) {
                SlotResult duplicateResult = equippedInstances.get(i);
                ItemStack duplicateStack = duplicateResult.stack().copy();
                String slotIdentifier = duplicateResult.slotContext().identifier();
                int slotIndex = duplicateResult.slotContext().index();

                CuriosApi.getCuriosHelper().getCuriosHandler(player).ifPresent(handler -> {
                    var stacksHandler = handler.getCurios().get(slotIdentifier);
                    if (stacksHandler != null) {
                        stacksHandler.getStacks().setStackInSlot(slotIndex, ItemStack.EMPTY);
                    }
                });
                if (!player.getInventory().add(duplicateStack)) {
                    player.drop(duplicateStack, false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.IGNITOR_SHIELD.get()).isPresent()) {
                event.getEntity().setSecondsOnFire(5);
            }

            var breadOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.RECHARGING_BREAD.get());
            if (breadOpt.isPresent()) {
                ItemStack breadStack = breadOpt.get().stack();
                int charge = breadStack.getOrCreateTag().getInt("Charge");
                if (charge > 0) {
                    LivingEntity target = event.getEntity();
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(target.level());
                    if (lightning != null) {
                        lightning.setPos(target.getX(), target.getY(), target.getZ());
                        target.level().addFreshEntity(lightning);
                    }
                }
            }

            boolean isRanged = event.getSource().getDirectEntity() instanceof Projectile;
            if (isRanged) {
                var bombOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.CAR_BOMB.get());
                if (bombOpt.isPresent()) {
                    ItemStack bombStack = bombOpt.get().stack();
                    CompoundTag nbt = bombStack.getOrCreateTag();
                    int uses = nbt.contains("Uses") ? nbt.getInt("Uses") : 3;

                    if (uses > 0) {
                        LivingEntity target = event.getEntity();
                        MinecartTNT tntMinecart = EntityType.TNT_MINECART.create(target.level());

                        if (tntMinecart != null) {
                            tntMinecart.setPos(target.getX(), target.getY(), target.getZ());
                            tntMinecart.primeFuse();

                            target.level().addFreshEntity(tntMinecart);
                            nbt.putInt("Uses", uses - 1);

                            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.METAL_PLACE, SoundSource.PLAYERS, 1.0F, 0.8F);
                        }
                    }
                }
            }
        }

        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {

                // Prevent spawning if the attacker is the player themselves OR if the damage is an explosion
                if (attacker != player && !event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
                    var golemOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.GUARDIAN_GOLEM.get());

                    if (golemOpt.isPresent()) {
                        ItemStack golemStack = golemOpt.get().stack();
                        CompoundTag nbt = golemStack.getOrCreateTag();

                        int uses = nbt.contains("Uses") ? nbt.getInt("Uses") : 2;
                        int timer = nbt.contains("GolemTimer") ? nbt.getInt("GolemTimer") : 0;

                        if (uses > 0 && timer == 0) {
                            IronGolem guardian = EntityType.IRON_GOLEM.create(player.level());
                            if (guardian != null) {
                                guardian.setPos(player.getX(), player.getY(), player.getZ());
                                guardian.setCustomName(Component.literal("Guardian"));
                                guardian.setPlayerCreated(true);
                                guardian.setTarget(attacker);

                                player.level().addFreshEntity(guardian);

                                nbt.putUUID("ActiveGolemUUID", guardian.getUUID());
                                nbt.putInt("GolemTimer", 1200);
                                nbt.putInt("Uses", uses - 1);

                                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                        SoundEvents.IRON_GOLEM_DEATH, SoundSource.PLAYERS, 1.0F, 1.6F);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.IGNITOR_SHIELD.get()).isPresent()) {
                if (event.getSource().getDirectEntity() instanceof Projectile projectile) {
                    event.setCanceled(true);
                    projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.2D));
                    projectile.setYRot(projectile.getYRot() + 180F);
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPiglinTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Piglin && event.getNewTarget() instanceof Player player) {
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.GOLDEN_EMERALD.get()).isPresent()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (event.getItem().isEdible()) {
                if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.EGG_OF_GLUTTONY.get()).isPresent()) {
                    int nextAmplifier = 0;
                    MobEffectInstance activeStrength = player.getEffect(MobEffects.DAMAGE_BOOST);
                    if (activeStrength != null) {
                        nextAmplifier = activeStrength.getAmplifier() + 1;
                        if (nextAmplifier > 4) {
                            nextAmplifier = 4;
                        }
                    }
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 6000, nextAmplifier, true, false, true));
                }
            }
        }
    }
}