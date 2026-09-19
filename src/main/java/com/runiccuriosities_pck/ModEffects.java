package com.runiccuriosities_pck;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, RunicCuriosities.MODID);

    // Custom status effect: Tolerated by the Piglin
    public static final DeferredHolder<MobEffect, MobEffect> TOLERATED_BY_PIGLINS = MOB_EFFECTS.register("tolerated_by_piglins",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFFD700) {});

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}