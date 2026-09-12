package com.runiccuriosities_pck;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

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
        // 1.21.1: Leggiamo i vecchi dati NBT usando il componente CUSTOM_DATA
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        int uses = 3; // Valore di default

        // Controlliamo se esiste il tag "Uses" all'interno dei Custom Data
        if (customData.contains("Uses")) {
            uses = customData.copyTag().getInt("Uses");
        }

        return Math.round((float) uses * 13.0F / 3.0F);
    }

    // Sets the durability bar color to an explosive bright red
    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFF2222; // Rosso esplosivo
    }
}