package com.runiccuriosities_pck;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class TimeHourglassItem extends TalismanItem implements ICurioItem {
    public static final int COOLDOWN_TICKS = 3600;

    public TimeHourglassItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        int uses = 1;
        if (customData.contains("Uses")) {
            uses = customData.copyTag().getInt("Uses");
        }
        return Math.round((float) uses * 13.0F / 1.0F);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00E5FF;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player) {
            handleRecharge(stack, player);
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player && !player.level().isClientSide()) {
            handleRecharge(stack, player);
        }
    }

    private void handleRecharge(ItemStack stack, Player player) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        int uses = tag.contains("Uses") ? tag.getInt("Uses") : 1;

        if (uses <= 0) {
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
                tag.putInt("Uses", 1);
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    public static void tryFreezeTime(Player player) {
        if (player.level().isClientSide()) return;

        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            handler.findFirstCurio(stack -> stack.getItem() instanceof TimeHourglassItem).ifPresent(result -> {
                ItemStack stack = result.stack();
                CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                CompoundTag tag = customData.copyTag();
                int uses = tag.contains("Uses") ? tag.getInt("Uses") : 1;

                if (uses > 0 && !player.getCooldowns().isOnCooldown(stack.getItem())) {
                    tag.putInt("Uses", 0);
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                    player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN_TICKS);

                    executeTimeFreeze(player);
                }
            });
        });
    }

    private static void executeTimeFreeze(Player player) {
        if (!(player.level() instanceof ServerLevel level)) return;

        Vec3 pos = player.position();
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 1.5F, 0.5F);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 2.0F, 0.1F);

        double radius = 12.0;
        AABB freezeArea = player.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, freezeArea);

        for (LivingEntity entity : targets) {
            if (entity == player) continue;
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 127, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 300, 200, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 4, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 0, false, false));
        }
    }
}