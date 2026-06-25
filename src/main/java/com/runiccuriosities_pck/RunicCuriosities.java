package com.runiccuriosities_pck;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RunicCuriosities.MODID)
public class RunicCuriosities {
    public static final String MODID = "runic_curiosities";

    public RunicCuriosities() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registriamo la nostra nuova Tab per la Creativa
        ModCreativeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::enqueueIMC);

        ModItems.ITEMS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Registers networking pipeline for packet handling
        event.enqueueWork(ModMessages::register);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // Registration for all custom Curios inventory slot types and sizes
        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                top.theillusivec4.curios.api.SlotTypePreset.CHARM.getMessageBuilder().size(3).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                top.theillusivec4.curios.api.SlotTypePreset.RING.getMessageBuilder().size(5).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                top.theillusivec4.curios.api.SlotTypePreset.NECKLACE.getMessageBuilder().size(3).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                top.theillusivec4.curios.api.SlotTypePreset.BELT.getMessageBuilder().size(3).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                top.theillusivec4.curios.api.SlotTypePreset.BACK.getMessageBuilder().size(2).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                top.theillusivec4.curios.api.SlotTypePreset.BODY.getMessageBuilder().size(2).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                top.theillusivec4.curios.api.SlotTypePreset.BRACELET.getMessageBuilder().size(2).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                top.theillusivec4.curios.api.SlotTypePreset.HANDS.getMessageBuilder().size(2).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                top.theillusivec4.curios.api.SlotTypePreset.HEAD.getMessageBuilder().size(2).build());
    }
}