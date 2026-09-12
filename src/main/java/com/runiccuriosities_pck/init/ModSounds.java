package com.runiccuriosities_pck;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    // In NeoForge 1.21.1 si usa Registries.SOUND_EVENT
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, RunicCuriosities.MODID);

    // laser sound
    public static final Supplier<SoundEvent> LASER_SHOOT = registerSoundEvent("laser_shoot");
    // death sound
    public static final Supplier<SoundEvent> GOLEM_DEATH = registerSoundEvent("golem_death");
    // hurt sound
    public static final Supplier<SoundEvent> GOLEM_HURT = registerSoundEvent("golem_hurt");
    // tame sound
    public static final Supplier<SoundEvent> GOLEM_TAME = registerSoundEvent("golem_tame");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        // Il nuovo metodo 1.21.1 per ResourceLocation
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, name)));
    }

    public static final Supplier<SoundEvent> HOURGLASS_CUSTOM_1 = SOUND_EVENTS.register("hourglass_custom_1",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "hourglass_custom_1")));

    public static final Supplier<SoundEvent> HOURGLASS_CUSTOM_2 = SOUND_EVENTS.register("hourglass_custom_2",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "hourglass_custom_2")));

    public static final Supplier<SoundEvent> HOURGLASS_CUSTOM_3 = SOUND_EVENTS.register("hourglass_custom_3",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(RunicCuriosities.MODID, "hourglass_custom_3")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}