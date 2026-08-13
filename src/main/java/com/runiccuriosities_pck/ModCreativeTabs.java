package com.runiccuriosities_pck;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RunicCuriosities.MODID);

    public static final RegistryObject<CreativeModeTab> RUNIC_CURIOSITIES_TAB = CREATIVE_MODE_TABS.register("runic_curiosities_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SAVIRITIUM_COMPOUND.get()))
                    .title(Component.translatable("creativetab.runic_curiosities_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.SAVIRITIUM_COMPOUND.get());
                        pOutput.accept(ModItems.SAVIRITIUM_COMPOUND_BLOCK_ITEM.get());
                        pOutput.accept(ModItems.SAVIRITIUM_GOLEM_SPAWN_EGG.get());
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

                        ItemStack heartStack = new ItemStack(ModItems.HEART_OF_RESOLUTION.get());
                        heartStack.enchant(net.minecraft.world.item.enchantment.Enchantments.BINDING_CURSE, 1);
                        pOutput.accept(heartStack);

                        pOutput.accept(ModItems.WARDEN_ANTENNAS.get());
                        pOutput.accept(ModItems.SPIDER_BOOTS.get());
                        pOutput.accept(ModItems.FAIRY_WINGS.get());
                        pOutput.accept(ModItems.NEPTUNES_HELMET.get());
                        pOutput.accept(ModItems.RANDOM_CAULDRON.get());
                        pOutput.accept(ModItems.WARDEN_BEAM.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}