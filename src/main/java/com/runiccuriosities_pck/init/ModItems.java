package com.runiccuriosities_pck;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.HoeItem;

public class ModItems {
    // In NeoForge 1.21.1 si usa DeferredRegister.Items per gli oggetti
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RunicCuriosities.MODID);

    // Tutti i RegistryObject sono diventati DeferredItem
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.register("example_item",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> GOLDEN_EMERALD = ITEMS.register("golden_emerald",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> EGG_OF_GLUTTONY = ITEMS.register("egg_of_gluttony",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> SCARLET_EYES = ITEMS.register("scarlet_eyes",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> IGNITOR_SHIELD = ITEMS.register("ignitor_shield",
            () -> new TalismanItem(new Item.Properties().stacksTo(1).fireResistant()));

    public static final DeferredItem<Item> RECHARGING_BREAD = ITEMS.register("recharging_bread",
            () -> new RechargingBreadItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> GLASS_CLOTH = ITEMS.register("glass_cloth",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> GUARDIAN_GOLEM = ITEMS.register("guardian_golem",
            () -> new GuardianGolemItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> CAR_BOMB = ITEMS.register("car_bomb",
            () -> new CarBombItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> ENERGY_DRINK = ITEMS.register("energy_drink",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> TIME_HOURGLASS = ITEMS.register("time_hourglass",
            () -> new TimeHourglassItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> SPONGE_RING = ITEMS.register("sponge_ring",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> VIPERS_EMBRACE = ITEMS.register("vipers_embrace",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> HEART_OF_RESOLUTION = ITEMS.register("heart_of_resolution",
            () -> new HeartOfResolutionItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> WARDEN_ANTENNAS = ITEMS.register("warden_antennas",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> SPIDER_BOOTS = ITEMS.register("spider_boots",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> FAIRY_WINGS = ITEMS.register("fairy_wings",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> NEPTUNES_HELMET = ITEMS.register("neptunes_helmet",
            () -> new NeptunesHelmetItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> RANDOM_CAULDRON = ITEMS.register("random_cauldron",
            () -> new RandomCauldronItem());

    public static final DeferredItem<Item> WARDEN_BEAM = ITEMS.register("warden_beam",
            () -> new WardenBeamItem());

    public static final DeferredItem<Item> SAVIRITIUM_COMPOUND = ITEMS.register("saviritium_compound",
            () -> new Item(new Item.Properties().stacksTo(16).fireResistant()));

    public static final DeferredItem<Item> SAVIRITIUM_COMPOUND_BLOCK_ITEM = ITEMS.register("saviritium_compound_block",
            () -> new BlockItem(ModBlocks.SAVIRITIUM_COMPOUND_BLOCK.get(), new Item.Properties().stacksTo(16).fireResistant()));

    // In NeoForge si usa DeferredSpawnEggItem al posto di ForgeSpawnEggItem
    public static final DeferredItem<Item> SAVIRITIUM_GOLEM_SPAWN_EGG = ITEMS.register("saviritium_golem_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SAVIRITIUM_GOLEM, 0xFFFFFF, 0xFFFFFF, new Item.Properties().stacksTo(16).fireResistant()));

    // ==========================================
    // SMITHING TEMPLATE
    // ==========================================
    private static final java.util.List<ResourceLocation> SAVIRITIUM_UPGRADE_EMPTY_SLOTS = java.util.List.of(
            ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet"),
            ResourceLocation.withDefaultNamespace("item/empty_slot_sword"),
            ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate"),
            ResourceLocation.withDefaultNamespace("item/empty_slot_pickaxe"),
            ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings"),
            ResourceLocation.withDefaultNamespace("item/empty_slot_axe"),
            ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots"),
            ResourceLocation.withDefaultNamespace("item/empty_slot_hoe"),
            ResourceLocation.withDefaultNamespace("item/empty_slot_shovel"));

    private static final java.util.List<ResourceLocation> SAVIRITIUM_UPGRADE_ADDITIONS_SLOTS = java.util.List.of(
            ResourceLocation.withDefaultNamespace("item/empty_slot_ingot"));

    public static final DeferredItem<Item> SAVIRITIUM_UPGRADE_SMITHING_TEMPLATE = ITEMS.register("saviritium_upgrade_smithing_template",
            () -> new net.minecraft.world.item.SmithingTemplateItem(
                    net.minecraft.network.chat.Component.translatable("item.runic_curiosities.smithing_template.saviritium_upgrade.applies_to").withStyle(net.minecraft.ChatFormatting.BLUE),
                    net.minecraft.network.chat.Component.translatable("item.runic_curiosities.smithing_template.saviritium_upgrade.ingredients").withStyle(net.minecraft.ChatFormatting.BLUE),
                    net.minecraft.network.chat.Component.translatable("upgrade.runic_curiosities.saviritium_upgrade").withStyle(net.minecraft.ChatFormatting.GRAY),
                    net.minecraft.network.chat.Component.translatable("item.runic_curiosities.smithing_template.saviritium_upgrade.base_slot_description"),
                    net.minecraft.network.chat.Component.translatable("item.runic_curiosities.smithing_template.saviritium_upgrade.additions_slot_description"),
                    SAVIRITIUM_UPGRADE_EMPTY_SLOTS,
                    SAVIRITIUM_UPGRADE_ADDITIONS_SLOTS
            ));

    // ==========================================
    // UTENSILI E ARMI IN SAVIRITIUM (1.21.1 Attribute Update)
    // ==========================================
    public static final DeferredItem<Item> SAVIRITIUM_SWORD = ITEMS.register("saviritium_sword",
            () -> new SwordItem(ModMaterials.SAVIRITIUM_TIER, new Item.Properties().fireResistant().attributes(SwordItem.createAttributes(ModMaterials.SAVIRITIUM_TIER, 3, -2.4F))));

    public static final DeferredItem<Item> SAVIRITIUM_PICKAXE = ITEMS.register("saviritium_pickaxe",
            () -> new PickaxeItem(ModMaterials.SAVIRITIUM_TIER, new Item.Properties().fireResistant().attributes(PickaxeItem.createAttributes(ModMaterials.SAVIRITIUM_TIER, 1, -2.8F))));

    public static final DeferredItem<Item> SAVIRITIUM_AXE = ITEMS.register("saviritium_axe",
            () -> new AxeItem(ModMaterials.SAVIRITIUM_TIER, new Item.Properties().fireResistant().attributes(AxeItem.createAttributes(ModMaterials.SAVIRITIUM_TIER, 5.0F, -3.0F))));

    public static final DeferredItem<Item> SAVIRITIUM_SHOVEL = ITEMS.register("saviritium_shovel",
            () -> new ShovelItem(ModMaterials.SAVIRITIUM_TIER, new Item.Properties().fireResistant().attributes(ShovelItem.createAttributes(ModMaterials.SAVIRITIUM_TIER, 1.5F, -3.0F))));

    public static final DeferredItem<Item> SAVIRITIUM_HOE = ITEMS.register("saviritium_hoe",
            () -> new HoeItem(ModMaterials.SAVIRITIUM_TIER, new Item.Properties().fireResistant().attributes(HoeItem.createAttributes(ModMaterials.SAVIRITIUM_TIER, -4, 0.0F))));

    // ==========================================
    // ARMATURA IN SAVIRITIUM
    // ==========================================
    public static final DeferredItem<Item> SAVIRITIUM_HELMET = ITEMS.register("saviritium_helmet",
            () -> new TranslucentArmorItem(ModMaterials.SAVIRITIUM_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().fireResistant()));

    public static final DeferredItem<Item> SAVIRITIUM_CHESTPLATE = ITEMS.register("saviritium_chestplate",
            () -> new TranslucentArmorItem(ModMaterials.SAVIRITIUM_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant()));

    public static final DeferredItem<Item> SAVIRITIUM_LEGGINGS = ITEMS.register("saviritium_leggings",
            () -> new TranslucentArmorItem(ModMaterials.SAVIRITIUM_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant()));

    public static final DeferredItem<Item> SAVIRITIUM_BOOTS = ITEMS.register("saviritium_boots",
            () -> new TranslucentArmorItem(ModMaterials.SAVIRITIUM_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant()));
}