package com.runiccuriosities_pck;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TalismanItem extends Item {
    public TalismanItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return false;
    }
}