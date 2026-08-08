package com.runiccuriosities_pck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TimeHourglassItem extends TalismanItem {
    public TimeHourglassItem(Properties properties) {
        super(properties);
    }

    // Renders the single-use tracking bar
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    // Calculates width for 1 maximum use
    @Override
    public int getBarWidth(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int uses = (tag != null && tag.contains("Uses")) ? tag.getInt("Uses") : 1;
        return Math.round((float) uses * 13.0F / 1.0F);
    }

    // Cyan/Blue time-shifting color scheme
    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00E5FF;
    }

    // Handles item right-click usage and applies the 3-minute cooldown
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            CompoundTag tag = stack.getOrCreateTag();
            int uses = tag.contains("Uses") ? tag.getInt("Uses") : 1;

            // Check if the item has available uses and is not currently on cooldown
            if (uses > 0 && !player.getCooldowns().isOnCooldown(this)) {
                // Consume the single available use
                tag.putInt("Uses", uses - 1);

                // Apply a 3-minute cooldown (3 minutes * 60 seconds * 20 ticks = 3600 ticks)
                // This visually greys out the item and prevents usage even if NBT 'Uses' is fully recharged
                player.getCooldowns().addCooldown(this, 3600);

                return InteractionResultHolder.success(stack);
            }
        }

        return InteractionResultHolder.fail(stack);
    }
}