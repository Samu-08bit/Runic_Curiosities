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
}