package com.runiccuriosities_pck;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientTimeFreezeManager {

    // A class to represent a single Time Stop zone
    public static class TimeStopInstance {
        public final double x, y, z;
        public int ticksRemaining;

        public TimeStopInstance(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.ticksRemaining = 300; // 15 seconds per zone
        }
    }

    // List of all currently active Time Stops in the world
    public static final List<TimeStopInstance> activeStops = new ArrayList<>();

    // Method to spawn a new Time Stop zone
    public static void addTimeStop(double x, double y, double z) {
        activeStops.add(new TimeStopInstance(x, y, z));
    }

    public static void tick() {
        if (activeStops.isEmpty()) return;

        Iterator<TimeStopInstance> iterator = activeStops.iterator();
        while (iterator.hasNext()) {
            TimeStopInstance instance = iterator.next();

            if (instance.ticksRemaining > 0) {
                // === CLOCK TICKING SOUND EFFECT ===
                if (instance.ticksRemaining % 20 == 0) {
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.level != null) {
                        float pitch = (instance.ticksRemaining % 40 == 0) ? 1.0F : 1.4F;
                        mc.level.playLocalSound(
                                instance.x,
                                instance.y,
                                instance.z,
                                SoundEvents.UI_BUTTON_CLICK.get(),
                                SoundSource.AMBIENT,
                                1.0F,
                                pitch,
                                false
                        );
                    }
                }
                instance.ticksRemaining--;
            } else {
                // Remove the Time Stop when its timer reaches 0
                iterator.remove();
            }
        }
    }
}