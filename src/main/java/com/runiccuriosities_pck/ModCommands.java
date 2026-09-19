package com.runiccuriosities_pck;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = RunicCuriosities.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModCommands {

    private static final List<String> AVAILABLE_SOUNDS = List.of("default", "custom_1", "custom_2", "custom_3");

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, RunicCuriosities.MODID);

    public static final Supplier<AttachmentType<String>> HOURGLASS_SOUND = ATTACHMENT_TYPES.register("hourglass_sound", () -> AttachmentType.builder(() -> "default").serialize(com.mojang.serialization.Codec.STRING).build());
    public static final Supplier<AttachmentType<Boolean>> QUEUED_TO_DIE = ATTACHMENT_TYPES.register("queued_to_die", () -> AttachmentType.builder(() -> false).build());
    public static final Supplier<AttachmentType<Float>> STORED_LETHAL_DAMAGE = ATTACHMENT_TYPES.register("stored_lethal_damage", () -> AttachmentType.builder(() -> 0.0f).build());
    public static final Supplier<AttachmentType<Integer>> RUNIC_TIME_FREEZE = ATTACHMENT_TYPES.register("runic_time_freeze", () -> AttachmentType.builder(() -> 0).build());
    public static final Supplier<AttachmentType<Boolean>> HAD_GRAVITY_DISABLED = ATTACHMENT_TYPES.register("had_gravity_disabled", () -> AttachmentType.builder(() -> false).build());
    public static final Supplier<AttachmentType<Boolean>> HAD_AI_DISABLED = ATTACHMENT_TYPES.register("had_ai_disabled", () -> AttachmentType.builder(() -> false).build());
    public static final Supplier<AttachmentType<Boolean>> SCARLET_EYES_EFFECT = ATTACHMENT_TYPES.register("scarlet_eyes_effect", () -> AttachmentType.builder(() -> true)
            .serialize(com.mojang.serialization.Codec.BOOL)
            .sync(net.minecraft.network.codec.ByteBufCodecs.BOOL)
            .copyOnDeath()
            .build());

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("runic_curiosities")
                .then(Commands.literal("scarlet_eyes")
                        .then(Commands.literal("effect")
                                .then(Commands.argument("enabled", BoolArgumentType.bool())
                                        .executes(context -> setScarletEyesEffect(context.getSource(), BoolArgumentType.getBool(context, "enabled"))))
                                .executes(context -> getScarletEyesEffect(context.getSource()))))
                .then(Commands.literal("time_hourglass")
                        .then(Commands.literal("timestop").requires(source -> source.hasPermission(2)).executes(context -> executeTimeStop(context.getSource())))
                        .then(Commands.literal("soundlist").executes(ModCommands::listSounds).then(Commands.argument("sound_name", StringArgumentType.word()).suggests((context, builder) -> SharedSuggestionProvider.suggest(AVAILABLE_SOUNDS, builder)).executes(ModCommands::setSound)))));
    }

    private static int setScarletEyesEffect(CommandSourceStack source, boolean enabled) {
        if (source.getEntity() instanceof ServerPlayer player) {
            player.setData(SCARLET_EYES_EFFECT.get(), enabled);
            source.sendSuccess(() -> Component.literal("Scarlet Eyes visual effect " + (enabled ? "enabled." : "disabled.")), false);
            return 1;
        } else {
            source.sendFailure(Component.literal("Only a player can use this command!"));
            return 0;
        }
    }

    private static int getScarletEyesEffect(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            boolean enabled = !player.hasData(SCARLET_EYES_EFFECT.get()) || player.getData(SCARLET_EYES_EFFECT.get());
            source.sendSuccess(() -> Component.literal("Scarlet Eyes visual effect is currently " + (enabled ? "enabled." : "disabled.") + ". Use /runic_curiosities scarlet_eyes effect <true|false> to toggle."), false);
            return 1;
        } else {
            source.sendFailure(Component.literal("Only a player can use this command!"));
            return 0;
        }
    }

    private static int executeTimeStop(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            Vec3 pos = player.position();
            ServerLevel level = player.serverLevel();
            net.minecraft.sounds.SoundEvent chosenSound = getHourglassSound(player, net.minecraft.sounds.SoundEvents.END_PORTAL_SPAWN);
            level.playSound(null, pos.x, pos.y, pos.z, chosenSound, net.minecraft.sounds.SoundSource.PLAYERS, 1.5F, 0.5F);
            level.playSound(null, pos.x, pos.y, pos.z, net.minecraft.sounds.SoundEvents.BELL_RESONATE, net.minecraft.sounds.SoundSource.PLAYERS, 2.0F, 0.1F);
            double radius = 12.0;
            AABB freezeArea = player.getBoundingBox().inflate(radius);
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, freezeArea);
            for (LivingEntity entity : targets) {
                entity.setData(QUEUED_TO_DIE.get(), false);
                entity.setData(STORED_LETHAL_DAMAGE.get(), 0.0f);
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 127, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 300, 200, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 4, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 0, false, false));
                entity.setData(RUNIC_TIME_FREEZE.get(), 300);
                entity.setData(HAD_GRAVITY_DISABLED.get(), true);
                entity.setNoGravity(true);
            }
            source.sendSuccess(() -> Component.literal("Time stop activated!"), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("Only one player can use this command!"));
            return 0;
        }
    }

    private static int listSounds(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("§6[Time Hourglass Sounds]§r\n - §bdefault§r\n - §bcustom_1§r\n - §bcustom_2§r\n - §bcustom_3§r"), false);
        return 1;
    }

    private static int setSound(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String soundName = StringArgumentType.getString(context, "sound_name");
        if (!AVAILABLE_SOUNDS.contains(soundName)) {
            source.sendFailure(Component.literal("Invalid sound."));
            return 0;
        }
        if (source.getEntity() instanceof ServerPlayer player) {
            player.setData(HOURGLASS_SOUND.get(), soundName);
            source.sendSuccess(() -> Component.literal("Time Hourglass activation sound set to: " + soundName), false);
            return 1;
        }
        return 0;
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player clone = event.getEntity();
        if (original.hasData(HOURGLASS_SOUND.get())) {
            clone.setData(HOURGLASS_SOUND.get(), original.getData(HOURGLASS_SOUND.get()));
        }
        if (original.hasData(SCARLET_EYES_EFFECT.get())) {
            clone.setData(SCARLET_EYES_EFFECT.get(), original.getData(SCARLET_EYES_EFFECT.get()));
        }
    }

    public static net.minecraft.sounds.SoundEvent getHourglassSound(Player player, net.minecraft.sounds.SoundEvent defaultSound) {
        if (player.hasData(HOURGLASS_SOUND.get())) {
            String choice = player.getData(HOURGLASS_SOUND.get());
            switch (choice) {
                case "default": return defaultSound;
                case "custom_1": return ModSounds.HOURGLASS_CUSTOM_1.get();
                case "custom_2": return ModSounds.HOURGLASS_CUSTOM_2.get();
                case "custom_3": return ModSounds.HOURGLASS_CUSTOM_3.get();
            }
        }
        return defaultSound;
    }
}