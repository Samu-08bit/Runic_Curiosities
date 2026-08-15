package com.runiccuriosities_pck;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod(RunicCuriosities.MODID)
public class RunicCuriosities {
    public static final String MODID = "runic_curiosities";

    public RunicCuriosities() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registriamo la nostra nuova Tab per la Creativa
        ModCreativeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::enqueueIMC);

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Registers networking pipeline for packet handling
        event.enqueueWork(() -> {
            PacketHandler.register();
            ModMessages.register();
        });
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // Registration for all custom Curios inventory slot types and sizes
        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.CHARM.getMessageBuilder().size(3).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.RING.getMessageBuilder().size(5).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.NECKLACE.getMessageBuilder().size(3).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.BELT.getMessageBuilder().size(3).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.BACK.getMessageBuilder().size(2).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.BODY.getMessageBuilder().size(2).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.BRACELET.getMessageBuilder().size(2).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.HANDS.getMessageBuilder().size(2).build());

        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.HEAD.getMessageBuilder().size(3).build());

        // Custom slot per i boots con l'icona corretta
        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                new SlotTypeMessage.Builder("boots")
                        .size(2)
                        .icon(new ResourceLocation("minecraft", "item/empty_armor_slot_boots"))
                        .build());
    }
}