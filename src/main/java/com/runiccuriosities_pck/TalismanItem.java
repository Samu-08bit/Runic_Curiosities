package com.runiccuriosities_pck;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class TalismanItem extends Item implements ICurioItem {

    public TalismanItem(Properties properties) {
        super(properties);
    }

    // In 1.21.1 si controlla se l'oggetto supporta incantamenti
    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return false;
    }

    // isBookEnchantable è stato deprecato/rimosso in molti contesti NeoForge 1.21.1
    // NeoForge 1.21.1 gestisce le riparazioni in modo diverso, ma questo metodo rimane valido
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return false;
    }
}