package com.runiccuriosities_pck;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = RunicCuriosities.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModMessages {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Creiamo il "Registrar" che si occupa di dire al gioco quali pacchetti esistono
        final PayloadRegistrar registrar = event.registrar("1.0");

        // Message 1: Client to Server (Golem Commands)
        registrar.playToServer(
                GolemCommandPacket.TYPE,
                GolemCommandPacket.STREAM_CODEC,
                (payload, context) -> payload.handle(context)
        );

        // Message 2: Client to Server (Warden Beam)
        registrar.playToServer(
                WardenBeamPacket.TYPE,
                WardenBeamPacket.STREAM_CODEC,
                (payload, context) -> payload.handle(context)
        );

        // Message 3: Client to Server (Request time freeze activation)
        registrar.playToServer(
                PacketTimeFreeze.TYPE,
                PacketTimeFreeze.STREAM_CODEC,
                (payload, context) -> payload.handle(context)
        );

        // Message 4: Server to Client (Sync particle/render effects across dimension)
        registrar.playToClient(
                PacketSyncTimeFreeze.TYPE,
                PacketSyncTimeFreeze.STREAM_CODEC,
                (payload, context) -> payload.handle(context)
        );
    }
}