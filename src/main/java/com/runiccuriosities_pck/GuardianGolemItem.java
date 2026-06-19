package com.runiccuriosities_pck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GuardianGolemItem extends Item {
    public GuardianGolemItem(Properties properties) {
        super(properties);
    }

    // Displays the durability bar under the necklace icon
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    // Calculates bar width based on remaining uses (0 to 2)
    @Override
    public int getBarWidth(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int uses = (tag != null && tag.contains("Uses")) ? tag.getInt("Uses") : 2;
        return Math.round((float) uses * 13.0F / 2.0F);
    }

    // Sets the durability bar color to an iron gray hue
    @Override
    public int getBarColor(ItemStack stack) {
        return 0xCCCCCC;
    }
}