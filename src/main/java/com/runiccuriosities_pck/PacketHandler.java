package com.runiccuriosities_pck;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RunicCuriosities.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, WardenBeamPacket.class, WardenBeamPacket::toBytes, WardenBeamPacket::new, WardenBeamPacket::handle);
        INSTANCE.registerMessage(id++, GolemCommandPacket.class, GolemCommandPacket::toBytes, GolemCommandPacket::new, GolemCommandPacket::handle);
    }
}