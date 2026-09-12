package com.runiccuriosities_pck;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class ModMaterials {

    // ==========================================
    // TIER DEGLI UTENSILI (Sostituisce ForgeTier)
    // ==========================================
    public static final Tier SAVIRITIUM_TIER = new SimpleTier(
            BlockTags.NEEDS_DIAMOND_TOOL, // Tag per determinare cosa può scavare
            2500, // Durabilità
            10.0f, // Velocità
            5.0f, // Danno
            18, // Incantabilità
            () -> Ingredient.of(ModItems.SAVIRITIUM_COMPOUND.get())
    );

    // ==========================================
    // MATERIALE ARMATURA (Sistema completamente nuovo per la 1.21.1)
    // ==========================================

    // 1. Creiamo un registro dedicato ai Materiali dell'Armatura
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, RunicCuriosities.MODID);

    // 2. Mappe di difesa per ogni pezzo
    private static final EnumMap<ArmorItem.Type, Integer> SAVIRITIUM_DEFENSE = Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, 4);
        map.put(ArmorItem.Type.LEGGINGS, 7);
        map.put(ArmorItem.Type.CHESTPLATE, 9);
        map.put(ArmorItem.Type.HELMET, 4);
    });

    // 3. Registriamo il Saviritium come Holder<ArmorMaterial>
    public static final Holder<ArmorMaterial> SAVIRITIUM_ARMOR_MATERIAL = ARMOR_MATERIALS.register("saviritium",
            () -> new ArmorMaterial(
                    SAVIRITIUM_DEFENSE,
                    18, // Incantabilità
                    SoundEvents.ARMOR_EQUIP_NETHERITE, // Suono (in 1.21.1 questo è già un Holder!)
                    () -> Ingredient.of(ModItems.SAVIRITIUM_COMPOUND.get()), // Ingrediente riparazione
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "saviritium"))), // Layers (Texture)
                    3.5F, // Toughness
                    0.15F // Knockback Resistance
            ));
}