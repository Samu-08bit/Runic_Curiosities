package com.runiccuriosities_pck;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = RunicCuriosities.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeyBindings {

    public static final KeyMapping FREEZE_TIME_KEY = new KeyMapping(
            "key.runic_curiosities.freeze_time",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.runic_curiosities"
    );

    public static final KeyMapping WARDEN_BEAM_KEY = new KeyMapping(
            "key.runic_curiosities.warden_beam",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.runic_curiosities"
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(FREEZE_TIME_KEY);
        event.register(WARDEN_BEAM_KEY);
    }
}