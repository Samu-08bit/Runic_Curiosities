package com.runiccuriosities_pck;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class WardenBeamPacket implements CustomPacketPayload {

    // 1. Definisci il TYPE univoco
    public static final Type<WardenBeamPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "warden_beam"));

    // 2. Lo StreamCodec (vuoto perché è solo un "ping" dal client per dire che ha premuto il tasto)
    public static final StreamCodec<FriendlyByteBuf, WardenBeamPacket> STREAM_CODEC = StreamCodec.ofMember(
            WardenBeamPacket::write,
            WardenBeamPacket::new
    );

    public WardenBeamPacket() {}

    public WardenBeamPacket(FriendlyByteBuf buf) {}

    public void write(FriendlyByteBuf buf) {}

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            // Otteniamo il player dal context
            if (ctx.player() instanceof ServerPlayer player) {
                // Attiva il raggio se il server rileva che il player lo ha equipaggiato
                WardenBeamItem.tryShootBeam(player);
            }
        });
    }
}