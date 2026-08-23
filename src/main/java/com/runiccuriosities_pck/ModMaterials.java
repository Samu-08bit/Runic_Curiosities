package com.runiccuriosities_pck;

import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;

import java.util.EnumMap;
import java.util.function.Supplier;

public class ModMaterials {

    // --- STATISTICHE DEGLI UTENSILI (Leggermente superiori alla Netherite) ---
    public static final ForgeTier SAVIRITIUM_TIER = new ForgeTier(
            5, // Livello di scavo
            2500, // Durabilità
            10.0f, // Velocità
            5.0f, // Danno
            18, // Incantabilità
            Tags.Blocks.NEEDS_NETHERITE_TOOL,
            () -> Ingredient.of(ModItems.SAVIRITIUM_COMPOUND.get())
    );

    // --- STATISTICHE DELL'ARMATURA (Migliore della Netherite) ---
    public enum SaviritiumArmorMaterial implements ArmorMaterial {
        SAVIRITIUM("saviritium", 40, Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
            map.put(ArmorItem.Type.BOOTS, 4);
            map.put(ArmorItem.Type.LEGGINGS, 7);
            map.put(ArmorItem.Type.CHESTPLATE, 9);
            map.put(ArmorItem.Type.HELMET, 4);
        }), 18, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.5F, 0.15F, () -> Ingredient.of(ModItems.SAVIRITIUM_COMPOUND.get()));

        private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
            map.put(ArmorItem.Type.BOOTS, 13);
            map.put(ArmorItem.Type.LEGGINGS, 15);
            map.put(ArmorItem.Type.CHESTPLATE, 16);
            map.put(ArmorItem.Type.HELMET, 11);
        });

        private final String name;
        private final int durabilityMultiplier;
        private final EnumMap<ArmorItem.Type, Integer> protectionFunctionForType;
        private final int enchantmentValue;
        private final SoundEvent sound;
        private final float toughness;
        private final float knockbackResistance;
        private final Supplier<Ingredient> repairIngredient;

        SaviritiumArmorMaterial(String name, int durability, EnumMap<ArmorItem.Type, Integer> protection, int enchant, SoundEvent sound, float toughness, float knockback, Supplier<Ingredient> repair) {
            this.name = name;
            this.durabilityMultiplier = durability;
            this.protectionFunctionForType = protection;
            this.enchantmentValue = enchant;
            this.sound = sound;
            this.toughness = toughness;
            this.knockbackResistance = knockback;
            this.repairIngredient = repair;
        }

        @Override public int getDurabilityForType(ArmorItem.Type type) { return HEALTH_FUNCTION_FOR_TYPE.get(type) * this.durabilityMultiplier; }
        @Override public int getDefenseForType(ArmorItem.Type type) { return this.protectionFunctionForType.get(type); }
        @Override public int getEnchantmentValue() { return this.enchantmentValue; }
        @Override public SoundEvent getEquipSound() { return this.sound; }
        @Override public Ingredient getRepairIngredient() { return this.repairIngredient.get(); }
        @Override public String getName() { return RunicCuriosities.MODID + ":" + this.name; }
        @Override public float getToughness() { return this.toughness; }
        @Override public float getKnockbackResistance() { return this.knockbackResistance; }
    }
}