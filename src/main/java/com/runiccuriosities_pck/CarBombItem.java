package com.runiccuriosities_pck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class CarBombItem extends TalismanItem {
    public CarBombItem(Properties properties) {
        super(properties);
    }

    // Displays the remaining uses as a durability bar
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    // Calculates the bar width dynamically for 3 maximum uses
    @Override
    public int getBarWidth(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int uses = (tag != null && tag.contains("Uses")) ? tag.getInt("Uses") : 3;
        return Math.round((float) uses * 13.0F / 3.0F);
    }

    // Sets the durability bar color to an explosive bright red
    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFF2222;
    }
}