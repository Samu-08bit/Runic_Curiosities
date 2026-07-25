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

public class NeptunesHelmetItem extends Item implements ICurioItem {

    public NeptunesHelmetItem(Properties properties) {
        super(properties);
    }

    // 1. Tick quando è nel normale inventario del giocatore
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            updateAnimation(stack, player.isInWater() || player.isUnderWater() || player.isInLava() || player.isInFluidType());
        }
    }

    // 2. Tick quando è equipaggiato nello slot di Curios
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            updateAnimation(stack, player.isInWater() || player.isUnderWater() || player.isInLava() || player.isInFluidType());
        }
    }

    // 3. Tick quando è droppato a terra come entità
    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        updateAnimation(stack, entity.isInWater() || entity.isUnderWater() || entity.isInLava() || entity.isInFluidType());
        return false; // Ritorna false per far continuare i normali calcoli fisici
    }

    // Logica centralizzata che gestisce la transizione dell'NBT
    private void updateAnimation(ItemStack stack, boolean inLiquid) {
        CompoundTag nbt = stack.getOrCreateTag();
        int animState = nbt.contains("AnimState") ? nbt.getInt("AnimState") : 0;
        int animTick = nbt.contains("AnimTick") ? nbt.getInt("AnimTick") : 0;

        if (inLiquid) {
            if (animState < 2) {
                animTick++;
                if (animTick >= 4) { // Cambia frame
                    animState++;
                    animTick = 0;
                }
            } else {
                animTick = 0;
            }
        } else {
            if (animState > 0) {
                animTick++;
                if (animTick >= 4) { // Cambia frame
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

    // --- LOGICA BARRA DURABILITÀ (CARICA ACQUA) ---

    @Override
    public boolean isBarVisible(ItemStack stack) {
        // Mostra la barra solo se il tag "WaterTicks" esiste
        return stack.hasTag() && stack.getTag().contains("WaterTicks");
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("WaterTicks")) {
            int waterTicks = stack.getTag().getInt("WaterTicks");
            // 12000 è il massimo (10 minuti), 13.0F è la larghezza massima della barra in Minecraft
            return Math.round((float) waterTicks * 13.0F / 12000.0F);
        }
        return 13;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00AADD; // Colore azzurro/blu che si addice all'elmo di Nettuno
    }
}