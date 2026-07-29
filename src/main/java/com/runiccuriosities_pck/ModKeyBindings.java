package com.runiccuriosities_pck;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
