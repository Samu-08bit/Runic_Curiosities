package com.runiccuriosities_pck;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.item.ItemEntity;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;

@EventBusSubscriber(modid = RunicCuriosities.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        // 16. Spider Boots - (Client & Server)
        if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.SPIDER_BOOTS.get()).isPresent()) {
            if (player.horizontalCollision) {
                player.setDeltaMovement(player.getDeltaMovement().x, 0.2D, player.getDeltaMovement().z);
                player.fallDistance = 0.0F;
            }
        }

        // Server-side only for talisman logic
        if (!player.level().isClientSide) {
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
            enforceUniqueCurio(player, ModItems.SPONGE_RING.get());
            enforceUniqueCurio(player, ModItems.VIPERS_EMBRACE.get());
            enforceUniqueCurio(player, ModItems.HEART_OF_RESOLUTION.get());
            enforceUniqueCurio(player, ModItems.WARDEN_ANTENNAS.get());
            enforceUniqueCurio(player, ModItems.SPIDER_BOOTS.get());
            enforceUniqueCurio(player, ModItems.FAIRY_WINGS.get());
            enforceUniqueCurio(player, ModItems.NEPTUNES_HELMET.get());
            enforceUniqueCurio(player, ModItems.RANDOM_CAULDRON.get());
            enforceUniqueCurio(player, ModItems.WARDEN_BEAM.get());

            // 1. Talisman of Intuition
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.EXAMPLE_ITEM.get()).isPresent()) {
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, true, false, true));
            }

            // 2. Golden Emerald (Hero of the Village + Tolerated by the Piglin)
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.GOLDEN_EMERALD.get()).isPresent()) {
                player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 40, 0, true, false, true));
                player.addEffect(new MobEffectInstance(ModEffects.TOLERATED_BY_PIGLINS, 40, 0, true, false, true));

                // Calms nearby non-aggroed Piglins and clears hostile memory modules
                AABB area = player.getBoundingBox().inflate(24.0D);
                List<Piglin> piglins = player.level().getEntitiesOfClass(Piglin.class, area);
                for (Piglin piglin : piglins) {
                    if (piglin.getLastHurtByMob() != player) {
                        if (piglin.getTarget() == player) {
                            piglin.setTarget(null);
                        }
                        if (piglin.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) &&
                                piglin.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null) == player) {
                            piglin.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                        }
                        if (piglin.getBrain().hasMemoryValue(MemoryModuleType.ANGRY_AT) &&
                                piglin.getBrain().getMemory(MemoryModuleType.ANGRY_AT).orElse(null) == player.getUUID()) {
                            piglin.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
                        }
                        if (piglin.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD) &&
                                piglin.getBrain().getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD).orElse(null) == player) {
                            piglin.getBrain().eraseMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
                        }
                    }
                }
            }

            // 3. Egg of Gluttony
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.EGG_OF_GLUTTONY.get()).isPresent()) {
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 40, 0, true, false, true));
            }

            // 4. Scarlet Eyes
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.SCARLET_EYES.get()).isPresent() && player.level().isNight()) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 210, 0, true, false, false));
            }

            // 5. Ignitor Shield
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.IGNITOR_SHIELD.get()).isPresent()) {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, true, false, true));
            }

            // 6. Recharging Bread
            var breadOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.RECHARGING_BREAD.get());
            if (breadOpt.isPresent()) {
                ItemStack breadStack = breadOpt.get().stack();
                CustomData data = breadStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                CompoundTag nbt = data.copyTag();

                int charge = nbt.contains("Charge") ? nbt.getInt("Charge") : 1200;

                if (charge > 0) {
                    charge--;
                    player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 39, 0, true, false, false));
                } else {
                    int wheatSlot = -1;
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        if (player.getInventory().getItem(i).is(Items.WHEAT)) {
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
                breadStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
            }

            // 7. Glass Cloth
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

            // 8. Guardian Golem
            var golemOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.GUARDIAN_GOLEM.get());
            if (golemOpt.isPresent()) {
                ItemStack golemStack = golemOpt.get().stack();
                CustomData data = golemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                CompoundTag nbt = data.copyTag();

                int uses = nbt.contains("Uses") ? nbt.getInt("Uses") : 2;

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
                                        if (entity != null) entity.discard();
                                        nbt.remove("ActiveGolemUUID");
                                    }
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                }

                if (uses == 0) {
                    int ironSlot = -1;
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        if (player.getInventory().getItem(i).is(Items.IRON_INGOT)) {
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
                golemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
            }

            // 9. Car Bomb
            var bombOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.CAR_BOMB.get());
            if (bombOpt.isPresent()) {
                ItemStack bombStack = bombOpt.get().stack();
                CustomData data = bombStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                CompoundTag nbt = data.copyTag();

                int uses = nbt.contains("Uses") ? nbt.getInt("Uses") : 3;

                if (uses == 0) {
                    int tntSlot = -1;
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        if (player.getInventory().getItem(i).is(Items.TNT)) {
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
                bombStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
            }

            // 10. Energy Drink
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.ENERGY_DRINK.get()).isPresent()) {
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 39, 0, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 39, 0, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 39, 0, true, false, true));
            }

            // 11. Time Hourglass
            var hourglassOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.TIME_HOURGLASS.get());
            if (hourglassOpt.isPresent()) {
                ItemStack stack = hourglassOpt.get().stack();
                CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                CompoundTag nbt = data.copyTag();

                int uses = nbt.contains("Uses") ? nbt.getInt("Uses") : 1;

                if (uses == 0) {
                    int sandSlot = -1;
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        if (player.getInventory().getItem(i).is(Items.SOUL_SAND)) {
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
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
            }

            // 12. Sponge Ring
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.SPONGE_RING.get()).isPresent()) {
                Level level = player.level();
                BlockPos playerPos = player.blockPosition();
                int radius = 3;

                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            BlockPos pos = playerPos.offset(x, y, z);
                            BlockState state = level.getBlockState(pos);

                            if (state.getBlock() instanceof LiquidBlock || state.getBlock() instanceof BucketPickup) {
                                if (state.getBlock() instanceof BucketPickup pickup) {
                                    pickup.pickupBlock(player, level, pos, state);
                                } else {
                                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
            }

            // 13. Viper's Embrace
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.VIPERS_EMBRACE.get()).isPresent()) {
                if (player.hasEffect(MobEffects.POISON)) {
                    player.removeEffect(MobEffects.POISON);
                }
            }

            // 14. Heart of Resolution
            AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            ResourceLocation heartId = ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "heart_of_resolution");

            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.HEART_OF_RESOLUTION.get()).isPresent()) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 39, 0, true, false, true));

                if (healthAttr != null && !healthAttr.hasModifier(heartId)) {
                    healthAttr.addPermanentModifier(new AttributeModifier(heartId, 20.0D, AttributeModifier.Operation.ADD_VALUE));
                }
            } else {
                if (healthAttr != null && healthAttr.hasModifier(heartId)) {
                    healthAttr.removeModifier(heartId);
                    if (player.getHealth() > player.getMaxHealth()) {
                        player.setHealth(player.getMaxHealth());
                    }
                }
            }

            // 15. Warden Antennas
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.WARDEN_ANTENNAS.get()).isPresent()) {
                AABB searchArea = new AABB(
                        player.getX() - 9.0D, player.getY() - 9.0D, player.getZ() - 9.0D,
                        player.getX() + 9.0D, player.getY() + 9.0D, player.getZ() + 9.0D
                );

                List<LivingEntity> entitiesInRange = player.level().getEntitiesOfClass(
                        LivingEntity.class,
                        searchArea,
                        entity -> entity != player
                );

                for (LivingEntity entity : entitiesInRange) {
                    entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0, true, false, true));
                    entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200, 0, true, false, true));
                }
            }

            // 17. Fairy Wings
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.FAIRY_WINGS.get()).isPresent()) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.LUCK, 40, 0, true, false, true));

                if (!player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = true;
                    player.onUpdateAbilities();
                }
            } else {
                if (!player.isCreative() && !player.isSpectator() && player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();
                }
            }

            // 18. Neptune's Helmet
            var neptuneAnimOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.NEPTUNES_HELMET.get());
            if (neptuneAnimOpt.isPresent()) {
                ItemStack stack = neptuneAnimOpt.get().stack();
                CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                CompoundTag nbt = data.copyTag();

                int waterTicks = nbt.contains("WaterTicks") ? nbt.getInt("WaterTicks") : 12000;

                if (player.isUnderWater() || player.isInWater()) {
                    if (waterTicks > 0) {
                        waterTicks--;
                        player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 100, 0, true, false, true));
                        player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100, 0, true, false, true));
                    } else {
                        int crystalSlot = -1;
                        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                            if (player.getInventory().getItem(i).is(Items.PRISMARINE_CRYSTALS)) {
                                crystalSlot = i;
                                break;
                            }
                        }

                        if (crystalSlot != -1) {
                            player.getInventory().removeItem(crystalSlot, 1);
                            waterTicks = 12000;
                            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.CONDUIT_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
                        }
                    }
                    nbt.putInt("WaterTicks", waterTicks);
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
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
    public static void onLivingHurt(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.IGNITOR_SHIELD.get()).isPresent()) {
                event.getEntity().igniteForSeconds(5);
            }

            var breadOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.RECHARGING_BREAD.get());
            if (breadOpt.isPresent()) {
                ItemStack breadStack = breadOpt.get().stack();
                CustomData data = breadStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                int charge = data.contains("Charge") ? data.copyTag().getInt("Charge") : 0;

                if (charge > 0) {
                    LivingEntity target = event.getEntity();
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(target.level());
                    if (lightning != null) {
                        lightning.setPos(target.getX(), target.getY(), target.getZ());
                        target.level().addFreshEntity(lightning);
                    }
                }
            }

            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.VIPERS_EMBRACE.get()).isPresent()) {
                LivingEntity target = event.getEntity();
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
            }

            boolean isRanged = event.getSource().getDirectEntity() instanceof Projectile;
            if (isRanged) {
                var bombOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.CAR_BOMB.get());
                if (bombOpt.isPresent()) {
                    ItemStack bombStack = bombOpt.get().stack();
                    CustomData data = bombStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                    CompoundTag nbt = data.copyTag();

                    int uses = nbt.contains("Uses") ? nbt.getInt("Uses") : 3;

                    if (uses > 0) {
                        LivingEntity target = event.getEntity();
                        MinecartTNT tntMinecart = EntityType.TNT_MINECART.create(target.level());

                        if (tntMinecart != null) {
                            tntMinecart.setPos(target.getX(), target.getY(), target.getZ());
                            tntMinecart.primeFuse();

                            target.level().addFreshEntity(tntMinecart);
                            nbt.putInt("Uses", uses - 1);
                            bombStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

                            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.METAL_PLACE, SoundSource.PLAYERS, 1.0F, 0.8F);
                        }
                    }
                }
            }
        }

        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker && attacker != player) {
                var golemOpt = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.GUARDIAN_GOLEM.get());
                if (golemOpt.isPresent()) {
                    ItemStack golemStack = golemOpt.get().stack();
                    CustomData data = golemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                    CompoundTag nbt = data.copyTag();

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
                            golemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

                            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.IRON_GOLEM_DEATH, SoundSource.PLAYERS, 1.0F, 1.6F);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();

        if (stack.isEmpty()) return;

        ResourceLocation regName = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (regName != null && regName.getNamespace().equals(RunicCuriosities.MODID)) {
            boolean[] equipped = {false};

            CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
                for (java.util.Map.Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
                    if (equipped[0]) break;

                    String slotType = entry.getKey();
                    net.minecraft.tags.TagKey<net.minecraft.world.item.Item> tagKey = net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", slotType));

                    if (stack.is(tagKey)) {
                        IDynamicStackHandler stackHandler = entry.getValue().getStacks();
                        for (int i = 0; i < stackHandler.getSlots(); i++) {
                            if (stackHandler.getStackInSlot(i).isEmpty()) {
                                if (!player.level().isClientSide()) {
                                    stackHandler.setStackInSlot(i, stack.copy());
                                    stack.shrink(1);
                                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                            SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                                }
                                equipped[0] = true;
                                break;
                            }
                        }
                    }
                }
            });

            if (equipped[0]) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
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
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.SPIDER_BOOTS.get()).isPresent()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlaySoundAtEntity(PlayLevelSoundEvent.AtEntity event) {
        if (event.getEntity() instanceof Player player) {
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.SPIDER_BOOTS.get()).isPresent()) {
                if (event.getSound() != null && event.getSound().value() != null && event.getSound().value().getLocation().getPath().contains("step")) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (event.getItem().getFoodProperties(player) != null) {
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

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Warden) {
            if (new Random().nextFloat() < 0.20f) {
                ItemEntity drop = new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), new ItemStack(ModItems.WARDEN_BEAM.get()));
                event.getDrops().add(drop);
            }
        }
    }

    @EventBusSubscriber(modid = RunicCuriosities.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onMovementInput(MovementInputUpdateEvent event) {
            if (event.getEntity().hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                MobEffectInstance slowness = event.getEntity().getEffect(MobEffects.MOVEMENT_SLOWDOWN);
                if (slowness != null && slowness.getAmplifier() >= 4) {
                    event.getInput().up = false;
                    event.getInput().down = false;
                    event.getInput().left = false;
                    event.getInput().right = false;
                    event.getInput().forwardImpulse = 0.0f;
                    event.getInput().leftImpulse = 0.0f;
                    event.getInput().jumping = false;
                    event.getInput().shiftKeyDown = false;
                }
            }
        }
    }
}