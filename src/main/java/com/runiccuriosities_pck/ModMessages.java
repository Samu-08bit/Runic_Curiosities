package com.runiccuriosities_pck;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    // Changed from private to public to allow external packets to access the network channel
    public static SimpleChannel INSTANCE;
    private static int packetId = 0;
    private static int id() { return packetId++; }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(RunicCuriosities.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // Message 1: Client to Server (Request time freeze activation)
        net.registerMessage(id(), PacketTimeFreeze.class,
                PacketTimeFreeze::toBytes,
                PacketTimeFreeze::new,
                PacketTimeFreeze::handle);

        // Message 2: Server to Client (Sync particle/render effects across dimension)
        net.registerMessage(id(), PacketSyncTimeFreeze.class,
                PacketSyncTimeFreeze::toBytes,
                PacketSyncTimeFreeze::new,
                PacketSyncTimeFreeze::handle);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}