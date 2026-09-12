package com.runiccuriosities_pck;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class GuardianGolemItem extends TalismanItem {
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
        // 1.21.1: Lettura dei Custom Data invece degli NBT
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        int uses = 2; // Valore di default

        if (customData.contains("Uses")) {
            uses = customData.copyTag().getInt("Uses");
        }

        return Math.round((float) uses * 13.0F / 2.0F);
    }

    // Sets the durability bar color to an iron gray hue
    @Override
    public int getBarColor(ItemStack stack) {
        return 0xCCCCCC;
    }
}