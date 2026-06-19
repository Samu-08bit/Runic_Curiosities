package com.runiccuriosities_pck;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientKeyEvents {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (ModKeyBindings.FREEZE_TIME_KEY.consumeClick()) {
                if (Minecraft.getInstance().player != null) {
                    var player = Minecraft.getInstance().player;
                    if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.TIME_HOURGLASS.get()).isPresent()) {
                        ModMessages.sendToServer(new PacketTimeFreeze());
                    }
                }
            }
        }
    }
}