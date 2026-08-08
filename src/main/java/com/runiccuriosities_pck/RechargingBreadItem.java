package com.runiccuriosities_pck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RechargingBreadItem extends TalismanItem {
    public RechargingBreadItem(Properties properties) {
        super(properties);
    }

    // Forces the game to show the durability/discharge bar
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    // Calculates the bar width (from 0 to 13 pixels in the Minecraft engine)
    @Override
    public int getBarWidth(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Charge")) {
            int charge = tag.getInt("Charge");
            // 1200 is the maximum charge (1 minute)
            return Math.round((float) charge * 13.0F / 1200.0F);
        }
        return 13;
    }

    // Sets the bar color (a nice orange/gold bread style)
    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFFAA00;
    }
}