package com.runiccuriosities_pck;

import com.mojang.logging.LogUtils;
import com.runiccuriosities_pck.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RunicCuriosities.MODID)
public class RunicCuriosities
{
    public static final String MODID = "runic_curiosities";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RunicCuriosities(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Registra il setup comune
        modEventBus.addListener(this::commonSetup);

        // REGISTRAZIONE DELLO DEFERRED REGISTER DEI TUOI OGGETTI
        ModItems.ITEMS.register(modEventBus);

        // Dice a Forge di ascoltare il metodo per registrare gli slot multipli
        modEventBus.addListener(this::enqueueIMC);

        // Registro gli eventi principali di Forge e della porta creativa
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    // IL METODO ORA È AL POSTO GIUSTO: Fuori dal costruttore, indipendente all'interno della classe
    private void enqueueIMC(final net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent event) {
        // 1. Slot CHARM (Talismano) - Quantità: 1
        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                top.theillusivec4.curios.api.SlotTypePreset.CHARM.getMessageBuilder().size(1).build());

        // 2. Slot RING (Anello) - Quantità: 2 (Il giocatore potrà equipaggiare due anelli!)
        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                top.theillusivec4.curios.api.SlotTypePreset.RING.getMessageBuilder().size(2).build());

        // 3. Slot NECKLACE (Collana) - Quantità: 1
        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                top.theillusivec4.curios.api.SlotTypePreset.NECKLACE.getMessageBuilder().size(1).build());

        // 4. Slot BELT (Cintura) - Quantità: 1
        net.minecraftforge.fml.InterModComms.sendTo("curios", "register_type", () ->
                top.theillusivec4.curios.api.SlotTypePreset.BELT.getMessageBuilder().size(1).build());
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.example_item);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
