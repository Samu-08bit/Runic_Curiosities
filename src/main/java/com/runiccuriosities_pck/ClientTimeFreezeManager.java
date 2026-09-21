package com.runiccuriosities_pck;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientTimeFreezeManager {

    public static class TimeStopInstance {
        public final double x, y, z;
        public int ticksRemaining;

        public TimeStopInstance(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.ticksRemaining = 300; // 15 sec
        }
    }

    public static final List<TimeStopInstance> activeStops = new ArrayList<>();

    public static void addTimeStop(double x, double y, double z) {
        activeStops.add(new TimeStopInstance(x, y, z));
    }

    public static void tick() {
        if (activeStops.isEmpty()) return;

        Iterator<TimeStopInstance> iterator = activeStops.iterator();
        while (iterator.hasNext()) {
            TimeStopInstance instance = iterator.next();

            if (instance.ticksRemaining > 0) {
                //SOUND EFFECT
                if (instance.ticksRemaining % 20 == 0) {
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.level != null) {
                        float pitch = (instance.ticksRemaining % 40 == 0) ? 1.0F : 1.4F;
                        mc.level.playLocalSound(
                                instance.x,
                                instance.y,
                                instance.z,
                                SoundEvents.UI_BUTTON_CLICK.value(), // In 1.21.1 si usa il .value() per estrarre il SoundEvent dall'Holder
                                SoundSource.AMBIENT,
                                1.0F,
                                pitch,
                                false
                        );
                    }
                }
                instance.ticksRemaining--;
            } else {
                // Remove Time Stop
                iterator.remove();
            }
        }
    }
}