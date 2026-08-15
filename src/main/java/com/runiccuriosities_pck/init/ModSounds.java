package com.runiccuriosities_pck;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RunicCuriosities.MODID);

    // Il suono del laser
    public static final RegistryObject<SoundEvent> LASER_SHOOT = registerSoundEvent("laser_shoot");

    // ---> IL NUOVO SUONO DI MORTE DEL GOLEM <---
    public static final RegistryObject<SoundEvent> GOLEM_DEATH = registerSoundEvent("golem_death");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(RunicCuriosities.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}