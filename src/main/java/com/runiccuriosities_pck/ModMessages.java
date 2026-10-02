package com.runiccuriosities_pck;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;


@EventBusSubscriber(modid = RunicCuriosities.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModMessages {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0");

        // Golem
        registrar.playToServer(
                GolemCommandPacket.TYPE,
                GolemCommandPacket.STREAM_CODEC,
                (payload, context) -> payload.handle(context)
        );

        // Warden
        registrar.playToServer(
                WardenBeamPacket.TYPE,
                WardenBeamPacket.STREAM_CODEC,
                (payload, context) -> payload.handle(context)
        );


        // Freeze
        registrar.playToServer(
                PacketTimeFreeze.TYPE,
                PacketTimeFreeze.STREAM_CODEC,
                (payload, context) -> payload.handle(context)
        );

        // Sync
        registrar.playToClient(
                PacketSyncTimeFreeze.TYPE,
                PacketSyncTimeFreeze.STREAM_CODEC,
                (payload, context) -> payload.handle(context)
        );
    }
}