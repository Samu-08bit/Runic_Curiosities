package com.runiccuriosities_pck;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, RunicCuriosities.MODID);
    //laser
    public static final DeferredHolder<SoundEvent, SoundEvent> LASER_SHOOT = registerSoundEvent("laser_shoot");
    //death
    public static final DeferredHolder<SoundEvent, SoundEvent> GOLEM_DEATH = registerSoundEvent("golem_death");
    //hurt
    public static final DeferredHolder<SoundEvent, SoundEvent> GOLEM_HURT = registerSoundEvent("golem_hurt");
    //tame
    public static final DeferredHolder<SoundEvent, SoundEvent> GOLEM_TAME = registerSoundEvent("golem_tame");


    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, name)));
    }

    public static final DeferredHolder<SoundEvent, SoundEvent> HOURGLASS_CUSTOM_1 = registerSoundEvent("hourglass_custom_1");

    public static final DeferredHolder<SoundEvent, SoundEvent> HOURGLASS_CUSTOM_2 = registerSoundEvent("hourglass_custom_2");

    public static final DeferredHolder<SoundEvent, SoundEvent> HOURGLASS_CUSTOM_3 = registerSoundEvent("hourglass_custom_3");

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}