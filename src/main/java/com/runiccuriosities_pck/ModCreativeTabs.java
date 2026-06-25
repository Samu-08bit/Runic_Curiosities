package com.runiccuriosities_pck;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    // Creiamo il registro per la tab della creativa
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RunicCuriosities.MODID);

    // Creiamo e configuriamo la nostra tab personalizzata
    public static final RegistryObject<CreativeModeTab> RUNIC_CURIOSITIES_TAB = CREATIVE_MODE_TABS.register("runic_curiosities_tab",
            () -> CreativeModeTab.builder()
                    // L'icona che verrà mostrata nella tab in alto (ho messo la clessidra, ma puoi usare quello che vuoi)
                    .icon(() -> new ItemStack(ModItems.TIME_HOURGLASS.get()))
                    .title(Component.translatable("creativetab.runic_curiosities_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        // Aggiungiamo tutti gli oggetti della mod in questa tab
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
                        pOutput.accept(ModItems.SPONGE_RING.get());
                        pOutput.accept(ModItems.VIPERS_EMBRACE.get());
                    })
                    .build());

    // Metodo per registrare i tab nel bus principale del gioco
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}