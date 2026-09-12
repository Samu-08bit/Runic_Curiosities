package com.runiccuriosities_pck;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketSyncTimeFreeze implements CustomPacketPayload {

    // 1. Definisci il TYPE univoco
    public static final Type<PacketSyncTimeFreeze> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "sync_time_freeze"));

    // 2. Lo StreamCodec (Encoding e Decoding)
    public static final StreamCodec<FriendlyByteBuf, PacketSyncTimeFreeze> STREAM_CODEC = StreamCodec.ofMember(
            PacketSyncTimeFreeze::write,
            PacketSyncTimeFreeze::new
    );

    private final double x;
    private final double y;
    private final double z;

    public PacketSyncTimeFreeze(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Decoder
    public PacketSyncTimeFreeze(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    // Encoder
    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // Handle (aggiornato per IPayloadContext)
    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientDistHandler.handlePacket(this.x, this.y, this.z);
        });
    }

    // Inner class (Questa va benissimo lasciarla per isolare il codice client)
    private static class ClientDistHandler {
        private static void handlePacket(double x, double y, double z) {
            ClientTimeFreezeManager.addTimeStop(x, y, z);
        }
    }
}