package com.runiccuriosities_pck;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.InterModComms;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod(RunicCuriosities.MODID)
public class RunicCuriosities {
    public static final String MODID = "runic_curiosities";

    // In 1.21.1 il ModEventBus viene passato direttamente nel costruttore!
    public RunicCuriosities(IEventBus modEventBus) {

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::enqueueIMC);

        // Registrazione dei Blocchi, Items, ecc.
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        // I nuovi registri della 1.21.1 che abbiamo aggiunto!
        ModMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModCommands.ATTACHMENT_TYPES.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // I pacchetti di rete si registrano automaticamente ora tramite ModMessages,
        // quindi non serve più chiamare PacketHandler o ModMessages.register() qui.
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // Registration for all custom Curios inventory slot types and sizes
        InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.CHARM.getMessageBuilder().size(3).build());

        InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.RING.getMessageBuilder().size(5).build());

        InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.NECKLACE.getMessageBuilder().size(3).build());

        InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.BELT.getMessageBuilder().size(3).build());

        InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.BACK.getMessageBuilder().size(2).build());

        InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.BODY.getMessageBuilder().size(2).build());

        InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.BRACELET.getMessageBuilder().size(2).build());

        InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.HANDS.getMessageBuilder().size(2).build());

        InterModComms.sendTo("curios", "register_type", () ->
                SlotTypePreset.HEAD.getMessageBuilder().size(3).build());

        // Custom slot per i boots con l'icona corretta (ResourceLocation 1.21.1)
        InterModComms.sendTo("curios", "register_type", () ->
                new SlotTypeMessage.Builder("boots")
                        .size(2)
                        .icon(ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots"))
                        .build());
    }
}