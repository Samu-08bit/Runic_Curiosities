package com.runiccuriosities_pck.init;

import com.runiccuriosities_pck.RunicCuriosities;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

    public class ModItems {
        // Creiamo il registro degli oggetti usando il tuo ModID
        public static final DeferredRegister<Item> ITEMS =
                DeferredRegister.create(ForgeRegistries.ITEMS, RunicCuriosities.MODID);

        // Registriamo il nostro primo talismano (2D, non accumulabile)
        public static final RegistryObject<Item> example_item = ITEMS.register("example_item",
                () -> new Item(new Item.Properties().stacksTo(1)));
    }










