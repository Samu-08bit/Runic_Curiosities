package com.runiccuriosities_pck;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketSyncTimeFreeze {
    private final double x;
    private final double y;
    private final double z;

    // Main constructor used when sending the packet from the server
    public PacketSyncTimeFreeze(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Decoder constructor reading from buffer, required by ModMessages
    public PacketSyncTimeFreeze(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    // Encoder method expected by ModMessages to write data into the network stream
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    public static void handle(PacketSyncTimeFreeze msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            // Distribute and safe-run the data sync strictly on the client render thread
            ClientDistHandler.handlePacket(msg.x, msg.y, msg.z);
        });
        context.setPacketHandled(true);
    }

    // Inner class to isolate client-only code and prevent logical side crashes
    private static class ClientDistHandler {
        private static void handlePacket(double x, double y, double z) {
            // Updated to add the new Time Stop zone to the active list instead of overwriting a single variable
            ClientTimeFreezeManager.addTimeStop(x, y, z);
        }
    }
}