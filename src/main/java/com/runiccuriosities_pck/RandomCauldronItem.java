package com.runiccuriosities_pck;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.util.RandomSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.ArrayList;

public class RandomCauldronItem extends TalismanItem implements ICurioItem {

    // 3 minutes in ticks (20 ticks * 60 seconds * 3 = 3600)
    private static final int MAX_COOLDOWN = 3600;

    public RandomCauldronItem() {
        super(new Item.Properties().stacksTo(1).defaultDurability(1));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();
        int timer = tag.getInt("CauldronTimer");

        // If the talisman is discharged (durability at 0 = damage at 1)
        if (stack.getDamageValue() >= 1) {
            if (consumeGhastTear(player)) {
                stack.setDamageValue(0); // Repairs
                tag.putInt("CauldronTimer", 0); // Resets the timer
            }
        } else {
            // If it is charged, increase the timer
            timer++;
            if (timer >= MAX_COOLDOWN) {
                applyRandomPositiveEffect(player);
                // 5 seconds of nausea (100 ticks)
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, true, false, true));

                stack.setDamageValue(1); // Discharges the talisman
                timer = 0;
            }
            tag.putInt("CauldronTimer", timer);
        }
    }

    private boolean consumeGhastTear(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.getItem() == Items.GHAST_TEAR) {
                invStack.shrink(1);
                return true;
            }
        }
        return false;
    }

    private void applyRandomPositiveEffect(Player player) {
        List<MobEffect> positiveEffects = new ArrayList<>();
        positiveEffects.add(MobEffects.MOVEMENT_SPEED);
        positiveEffects.add(MobEffects.DIG_SPEED);
        positiveEffects.add(MobEffects.DAMAGE_BOOST);
        positiveEffects.add(MobEffects.HEAL);
        positiveEffects.add(MobEffects.JUMP);
        positiveEffects.add(MobEffects.REGENERATION);
        positiveEffects.add(MobEffects.DAMAGE_RESISTANCE);
        positiveEffects.add(MobEffects.FIRE_RESISTANCE);
        positiveEffects.add(MobEffects.WATER_BREATHING);
        positiveEffects.add(MobEffects.INVISIBILITY);
        positiveEffects.add(MobEffects.NIGHT_VISION);
        positiveEffects.add(MobEffects.HEALTH_BOOST);
        positiveEffects.add(MobEffects.ABSORPTION);
        positiveEffects.add(MobEffects.SATURATION);
        positiveEffects.add(MobEffects.GLOWING);
        positiveEffects.add(MobEffects.LUCK);
        positiveEffects.add(MobEffects.SLOW_FALLING);
        positiveEffects.add(MobEffects.CONDUIT_POWER);
        positiveEffects.add(MobEffects.DOLPHINS_GRACE);
        positiveEffects.add(MobEffects.HERO_OF_THE_VILLAGE);

        RandomSource random = player.level().getRandom();
        MobEffect effect = positiveEffects.get(random.nextInt(positiveEffects.size()));

        // Assigns the random effect for 3 minutes (3600 ticks)
        player.addEffect(new MobEffectInstance(effect, 3600, 0, true, false, true));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (stack.getDamageValue() >= 1) {
            return 0; // If it's discharged, empty bar
        }
        CompoundTag tag = stack.getTag();
        int timer = tag != null ? tag.getInt("CauldronTimer") : 0;
        // The maximum width of the bar is 13
        return Math.round(13.0F * timer / (float) MAX_COOLDOWN);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // Rainbow color based on system time (rotates every 2 seconds)
        float hue = (System.currentTimeMillis() % 2000L) / 2000.0F;
        return Mth.hsvToRgb(hue, 1.0F, 1.0F);
    }
}