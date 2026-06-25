package com.runiccuriosities_pck;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    // Creiamo il registro per le Creative Mode Tabs
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RunicCuriosities.MODID);

    // Creiamo la nostra Tab personalizzata
    public static final RegistryObject<CreativeModeTab> RUNIC_CURIOSITIES_TAB = CREATIVE_MODE_TABS.register("runic_curiosities_tab",
            () -> CreativeModeTab.builder()
                    // L'icona che verrà mostrata per la tab (qui ho messo l'EXAMPLE_ITEM, ma puoi cambiarlo)
                    .icon(() -> new ItemStack(ModItems.EXAMPLE_ITEM.get()))
                    // Il nome della tab che poi tradurremo
                    .title(Component.translatable("creativetab.runic_curiosities_tab"))
                    // Qui aggiungiamo tutti gli oggetti che vogliamo far apparire nella nostra tab
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.EXAMPLE_ITEM.get());
                        pOutput.accept(ModItems.GOLDEN_EMERALD.get());
                        pOutput.accept(ModItems.EGG_OF_GLUTTONY.get());
                        pOutput.accept(ModItems.SCARLET_EYES.get());
                        pOutput.accept(ModItems.IGNITOR_SHIELD.get());
                        pOutput.accept(ModItems.RECHARGING_BREAD.get());
                        pOutput.accept(ModItems.GLASS_CLOTH.get());
                        pOutput.accept(ModItems.GUARDIAN_GOLEM.get());
                        pOutput.accept(ModItems.CAR_BOMB.get());
                        pOutput.accept(ModItems.ENERGY_DRINK.get());
                        pOutput.accept(ModItems.TIME_HOURGLASS.get());
                        // Se in futuro aggiungerai altri item, ti basterà aggiungere altre righe qui!
                    })
                    .build());

    // Metodo per registrare il tutto
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}