package com.runiccuriosities_pck;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class HeartOfResolutionItem extends Item implements ICurioItem {

    public HeartOfResolutionItem(Properties properties) {
        super(properties);
    }

    // Tick when it is in the player's normal inventory (or just picked up)
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        // Automatically adds Curse of Binding if it doesn't have it
        if (!level.isClientSide && stack.getEnchantmentLevel(Enchantments.BINDING_CURSE) == 0) {
            stack.enchant(Enchantments.BINDING_CURSE, 1);
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    // This method replaces the ModEvents "onPlayerTick" for this specific item
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() != null && !slotContext.entity().level().isClientSide()) {
            // Constantly adds Resistance 1 (amplifier 0)
            slotContext.entity().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0, true, false, true));
        }
    }

    // This method handles fixed attributes (like Max Health, Speed, Armor)
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();

        // 10 red hearts = 20.0 base health points (Operation.ADDITION adds fixed points)
        modifiers.put(Attributes.MAX_HEALTH,
                new AttributeModifier(uuid, "Heart of Resolution Max Health", 20.0, AttributeModifier.Operation.ADDITION));

        return modifiers;
    }
}