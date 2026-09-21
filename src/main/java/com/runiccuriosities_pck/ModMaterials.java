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

    // ====
    // TIER
    // ====
    public static final Tier SAVIRITIUM_TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2800,                                  // Durability
            13.0f,                                 // Mining speed
            5.0f,                                  // Attack damage
            18,                                    // Enchantability
            () -> Ingredient.of(ModItems.SAVIRITIUM_COMPOUND.get())
    );

    // ==============
    // ARMOR MATERIAL
    // ==============

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, RunicCuriosities.MODID);

    private static final EnumMap<ArmorItem.Type, Integer> SAVIRITIUM_DEFENSE = Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
        map.put(ArmorItem.Type.BOOTS, 4);
        map.put(ArmorItem.Type.LEGGINGS, 7);
        map.put(ArmorItem.Type.CHESTPLATE, 9);
        map.put(ArmorItem.Type.HELMET, 4);
    });

    public static final Holder<ArmorMaterial> SAVIRITIUM_ARMOR_MATERIAL = ARMOR_MATERIALS.register("saviritium",
            () -> new ArmorMaterial(
                    SAVIRITIUM_DEFENSE,
                    18,
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    () -> Ingredient.of(ModItems.SAVIRITIUM_COMPOUND.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "saviritium"))), // Layers (Texture)
                    3.5F, // Toughness
                    0.15F // Knockback Resistance
            ));
}