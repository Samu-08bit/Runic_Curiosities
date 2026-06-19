package com.runiccuriosities_pck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RechargingBreadItem extends Item {
    public RechargingBreadItem(Properties properties) {
        super(properties);
    }

    // Forza il gioco a mostrare la barra di scaricamento
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    // Calcola la larghezza della barra (da 0 a 13 pixel nel motore grafico di Minecraft)
    @Override
    public int getBarWidth(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Charge")) {
            int charge = tag.getInt("Charge");
            // 1200 è la carica massima (1 minuto)
            return Math.round((float) charge * 13.0F / 1200.0F);
        }
        return 13;
    }

    // Imposta il colore della barra (un bell'arancione/oro stile pane)
    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFFAA00;
    }
}