package com.runiccuriosities_pck;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class GoldenEmeraldItem extends TalismanItem implements ICurioItem {

    public GoldenEmeraldItem(Properties properties) {
        super(properties);
    }

    // Curios API hook: makes Piglins consider the wearer as wearing gold
    @Override
    public boolean makesPiglinsNeutral(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    // NeoForge Item extension hook: makes Piglins neutral
    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return true;
    }
}