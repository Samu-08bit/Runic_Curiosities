package com.runiccuriosities_pck;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class HeartOfResolutionItem extends Item implements ICurioItem {

    public HeartOfResolutionItem(Properties properties) {
        super(properties);
    }

    // Questo metodo sostituisce il ModEvents "onPlayerTick" per questo specifico oggetto
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() != null && !slotContext.entity().level().isClientSide()) {
            // Aggiunge Resistance 1 (amplifier 0) in modo costante
            slotContext.entity().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0, true, false, true));
        }
    }

    // Questo metodo gestisce attributi fissi (come Salute Max, Velocità, Armatura)
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();

        // 10 cuori rossi = 20.0 punti salute base (Operation.ADDITION somma i punti fissi)
        modifiers.put(Attributes.MAX_HEALTH,
                new AttributeModifier(uuid, "Heart of Resolution Max Health", 20.0, AttributeModifier.Operation.ADDITION));

        return modifiers;
    }
}