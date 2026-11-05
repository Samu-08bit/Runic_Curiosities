package com.runiccuriosities_pck;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, RunicCuriosities.MODID);

    public static final RegistryObject<MenuType<MagicModTableMenu>> MAGIC_MOD_TABLE_MENU =
            MENUS.register("magic_mod_table_menu",
                    () -> IForgeMenuType.create(MagicModTableMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}