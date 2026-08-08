package com.runiccuriosities_pck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class NeptunesHelmetItem extends TalismanItem implements ICurioItem {

    public NeptunesHelmetItem(Properties properties) {
        super(properties);
    }

    // 1. Tick when it is in the player's normal inventory
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            updateAnimation(stack, player.isInWater() || player.isUnderWater() || player.isInLava() || player.isInFluidType());
        }
    }

    // 2. Tick when it is equipped in the Curios slot
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            updateAnimation(stack, player.isInWater() || player.isUnderWater() || player.isInLava() || player.isInFluidType());
        }
    }

    // 3. Tick when it is dropped on the ground as an entity
    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        updateAnimation(stack, entity.isInWater() || entity.isUnderWater() || entity.isInLava() || entity.isInFluidType());
        return false; // Returns false to let normal physics calculations continue
    }

    // Centralized logic that handles the NBT transition
    private void updateAnimation(ItemStack stack, boolean inLiquid) {
        CompoundTag nbt = stack.getOrCreateTag();
        int animState = nbt.contains("AnimState") ? nbt.getInt("AnimState") : 0;
        int animTick = nbt.contains("AnimTick") ? nbt.getInt("AnimTick") : 0;

        if (inLiquid) {
            if (animState < 2) {
                animTick++;
                if (animTick >= 4) { // Change frame
                    animState++;
                    animTick = 0;
                }
            } else {
                animTick = 0;
            }
        } else {
            if (animState > 0) {
                animTick++;
                if (animTick >= 4) { // Change frame
                    animState--;
                    animTick = 0;
                }
            } else {
                animTick = 0;
            }
        }
        nbt.putInt("AnimState", animState);
        nbt.putInt("AnimTick", animTick);
    }

    // --- DURABILITY BAR LOGIC (WATER CHARGE) ---

    @Override
    public boolean isBarVisible(ItemStack stack) {
        // Shows the bar only if the "WaterTicks" tag exists
        return stack.hasTag() && stack.getTag().contains("WaterTicks");
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("WaterTicks")) {
            int waterTicks = stack.getTag().getInt("WaterTicks");
            // 12000 is the maximum (10 minutes), 13.0F is the maximum width of the bar in Minecraft
            return Math.round((float) waterTicks * 13.0F / 12000.0F);
        }
        return 13;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00AADD; // Light blue/cyan color that suits Neptune's helmet
    }
}