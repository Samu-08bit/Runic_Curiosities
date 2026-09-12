package com.runiccuriosities_pck;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;

// Noterai che i pacchetti sono passati a "net.neoforged"
@EventBusSubscriber(modid = RunicCuriosities.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientKeyEvents {

    // In NeoForge 1.21.1 usiamo ClientTickEvent.Post invece di controllare Phase.END
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        // Consumiamo il click del tasto finché è premuto
        while (ModKeyBindings.FREEZE_TIME_KEY.consumeClick()) {
            if (Minecraft.getInstance().player != null) {
                var player = Minecraft.getInstance().player;
                // Controlliamo se il giocatore ha il Talismano equipaggiato nei Curios
                if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.TIME_HOURGLASS.get()).isPresent()) {
                    // Nuovo sistema di rete NeoForge 1.21.1 per inviare pacchetti al Server
                    PacketDistributor.sendToServer(new PacketTimeFreeze());
                }
            }
        }

        // Fai lo stesso anche per il WARDEN BEAM se lo avevi qui o in un altro file!
        // Esempio (se c'era):
        // while (ModKeyBindings.WARDEN_BEAM_KEY.consumeClick()) {
        //     if (Minecraft.getInstance().player != null && CuriosApi.getCuriosHelper().findFirstCurio(Minecraft.getInstance().player, ModItems.WARDEN_BEAM.get()).isPresent()) {
        //         PacketDistributor.sendToServer(new PacketWardenBeam());
        //     }
        // }
    }
}