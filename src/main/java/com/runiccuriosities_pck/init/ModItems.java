package com.runiccuriosities_pck;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RunicCuriosities.MODID);

    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GOLDEN_EMERALD = ITEMS.register("golden_emerald",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> EGG_OF_GLUTTONY = ITEMS.register("egg_of_gluttony",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SCARLET_EYES = ITEMS.register("scarlet_eyes",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> IGNITOR_SHIELD = ITEMS.register("ignitor_shield",
            () -> new TalismanItem(new Item.Properties().stacksTo(1).fireResistant()));

    public static final RegistryObject<Item> RECHARGING_BREAD = ITEMS.register("recharging_bread",
            () -> new RechargingBreadItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GLASS_CLOTH = ITEMS.register("glass_cloth",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GUARDIAN_GOLEM = ITEMS.register("guardian_golem",
            () -> new GuardianGolemItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CAR_BOMB = ITEMS.register("car_bomb",
            () -> new CarBombItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ENERGY_DRINK = ITEMS.register("energy_drink",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TIME_HOURGLASS = ITEMS.register("time_hourglass",
            () -> new TimeHourglassItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SPONGE_RING = ITEMS.register("sponge_ring",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> VIPERS_EMBRACE = ITEMS.register("vipers_embrace",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HEART_OF_RESOLUTION = ITEMS.register("heart_of_resolution",
            () -> new HeartOfResolutionItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> WARDEN_ANTENNAS = ITEMS.register("warden_antennas",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SPIDER_BOOTS = ITEMS.register("spider_boots",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FAIRY_WINGS = ITEMS.register("fairy_wings",
            () -> new TalismanItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> NEPTUNES_HELMET = ITEMS.register("neptunes_helmet",
            () -> new NeptunesHelmetItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RANDOM_CAULDRON = ITEMS.register("random_cauldron",
            () -> new RandomCauldronItem());

    public static final RegistryObject<Item> WARDEN_BEAM = ITEMS.register("warden_beam",
            () -> new WardenBeamItem());

    public static final RegistryObject<Item> SAVIRITIUM_COMPOUND = ITEMS.register("saviritium_compound",
            () -> new Item(new Item.Properties().stacksTo(16).fireResistant()));

    public static final RegistryObject<Item> SAVIRITIUM_COMPOUND_BLOCK_ITEM = ITEMS.register("saviritium_compound_block",
            () -> new BlockItem(ModBlocks.SAVIRITIUM_COMPOUND_BLOCK.get(), new Item.Properties().stacksTo(16).fireResistant()));

    public static final RegistryObject<net.minecraft.world.item.Item> SAVIRITIUM_GOLEM_SPAWN_EGG = ITEMS.register("saviritium_golem_spawn_egg",
            () -> new net.minecraftforge.common.ForgeSpawnEggItem(ModEntities.SAVIRITIUM_GOLEM, 0xFFFFFF, 0xFFFFFF, new net.minecraft.world.item.Item.Properties().stacksTo(16).fireResistant()));

    // ==========================================
    // SMITHING TEMPLATE
    // ==========================================
    private static final java.util.List<net.minecraft.resources.ResourceLocation> SAVIRITIUM_UPGRADE_EMPTY_SLOTS = java.util.List.of(
            new net.minecraft.resources.ResourceLocation("item/empty_armor_slot_helmet"),
            new net.minecraft.resources.ResourceLocation("item/empty_slot_sword"),
            new net.minecraft.resources.ResourceLocation("item/empty_armor_slot_chestplate"),
            new net.minecraft.resources.ResourceLocation("item/empty_slot_pickaxe"),
            new net.minecraft.resources.ResourceLocation("item/empty_armor_slot_leggings"),
            new net.minecraft.resources.ResourceLocation("item/empty_slot_axe"),
            new net.minecraft.resources.ResourceLocation("item/empty_armor_slot_boots"),
            new net.minecraft.resources.ResourceLocation("item/empty_slot_hoe"),
            new net.minecraft.resources.ResourceLocation("item/empty_slot_shovel"));

    private static final java.util.List<net.minecraft.resources.ResourceLocation> SAVIRITIUM_UPGRADE_ADDITIONS_SLOTS = java.util.List.of(
            new net.minecraft.resources.ResourceLocation("item/empty_slot_ingot"));

    public static final RegistryObject<Item> SAVIRITIUM_UPGRADE_SMITHING_TEMPLATE = ITEMS.register("saviritium_upgrade_smithing_template",
            () -> new net.minecraft.world.item.SmithingTemplateItem(
                    net.minecraft.network.chat.Component.translatable("item.runic_curiosities.smithing_template.saviritium_upgrade.applies_to").withStyle(net.minecraft.ChatFormatting.BLUE),
                    net.minecraft.network.chat.Component.translatable("item.runic_curiosities.smithing_template.saviritium_upgrade.ingredients").withStyle(net.minecraft.ChatFormatting.BLUE),
                    net.minecraft.network.chat.Component.translatable("upgrade.runic_curiosities.saviritium_upgrade").withStyle(net.minecraft.ChatFormatting.GRAY),
                    net.minecraft.network.chat.Component.translatable("item.runic_curiosities.smithing_template.saviritium_upgrade.base_slot_description"),
                    net.minecraft.network.chat.Component.translatable("item.runic_curiosities.smithing_template.saviritium_upgrade.additions_slot_description"),
                    SAVIRITIUM_UPGRADE_EMPTY_SLOTS,
                    SAVIRITIUM_UPGRADE_ADDITIONS_SLOTS
            ) {
                // Sovrascriviamo la resistenza al fuoco direttamente sulla classe!
                @Override
                public boolean isFireResistant() {
                    return true;
                }
            });

    // ==========================================
    // UTENSILI E ARMI IN SAVIRITIUM
    // ==========================================
    public static final RegistryObject<Item> SAVIRITIUM_SWORD = ITEMS.register("saviritium_sword",
            () -> new net.minecraft.world.item.SwordItem(ModMaterials.SAVIRITIUM_TIER, 3, -2.4F, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> SAVIRITIUM_PICKAXE = ITEMS.register("saviritium_pickaxe",
            () -> new net.minecraft.world.item.PickaxeItem(ModMaterials.SAVIRITIUM_TIER, 1, -2.8F, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> SAVIRITIUM_AXE = ITEMS.register("saviritium_axe",
            () -> new net.minecraft.world.item.AxeItem(ModMaterials.SAVIRITIUM_TIER, 5.0F, -3.0F, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> SAVIRITIUM_SHOVEL = ITEMS.register("saviritium_shovel",
            () -> new net.minecraft.world.item.ShovelItem(ModMaterials.SAVIRITIUM_TIER, 1.5F, -3.0F, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> SAVIRITIUM_HOE = ITEMS.register("saviritium_hoe",
            () -> new net.minecraft.world.item.HoeItem(ModMaterials.SAVIRITIUM_TIER, -4, 0.0F, new Item.Properties().fireResistant()));

    // ==========================================
    // ARMATURA IN SAVIRITIUM
    // ==========================================
    public static final RegistryObject<Item> SAVIRITIUM_HELMET = ITEMS.register("saviritium_helmet",
            () -> new TranslucentArmorItem(ModMaterials.SaviritiumArmorMaterial.SAVIRITIUM, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> SAVIRITIUM_CHESTPLATE = ITEMS.register("saviritium_chestplate",
            () -> new TranslucentArmorItem(ModMaterials.SaviritiumArmorMaterial.SAVIRITIUM, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> SAVIRITIUM_LEGGINGS = ITEMS.register("saviritium_leggings",
            () -> new TranslucentArmorItem(ModMaterials.SaviritiumArmorMaterial.SAVIRITIUM, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> SAVIRITIUM_BOOTS = ITEMS.register("saviritium_boots",
            () -> new TranslucentArmorItem(ModMaterials.SaviritiumArmorMaterial.SAVIRITIUM, net.minecraft.world.item.ArmorItem.Type.BOOTS, new Item.Properties().fireResistant()));
}