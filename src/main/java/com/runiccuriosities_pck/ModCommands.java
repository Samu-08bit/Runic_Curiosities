package com.runiccuriosities_pck;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {

    // Lista degli slot audio: il default della mod + 3 slot personalizzabili dai giocatori
    private static final List<String> AVAILABLE_SOUNDS = List.of(
            "default",
            "custom_1",
            "custom_2",
            "custom_3"
    );

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("runic_curiosities")
                        .then(Commands.literal("time_hourglass")
                                .then(Commands.literal("timestop")
                                        .requires(source -> source.hasPermission(2))
                                        .executes(context -> executeTimeStop(context.getSource()))
                                )
                                .then(Commands.literal("soundlist")
                                        .executes(ModCommands::listSounds)
                                        .then(Commands.argument("sound_name", StringArgumentType.word())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(AVAILABLE_SOUNDS, builder))
                                                .executes(ModCommands::setSound)
                                        )
                                )
                        )
        );
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
                entity.getPersistentData().remove("QueuedToDie");
                entity.getPersistentData().remove("StoredLethalDamage");

                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 127, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 300, 200, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 4, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 0, false, false));

                entity.getPersistentData().putInt("RunicTimeFreeze", 300);
                entity.getPersistentData().putBoolean("HadGravityDisabled", true);
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
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal(
                "§6[Time Hourglass Sounds]§r\n" +
                        "You can use Resource Packs to add custom sounds!\n" +
                        " - §bdefault§r (Mod original sound)\n" +
                        " - §bcustom_1§r (Needs Resource Pack)\n" +
                        " - §bcustom_2§r (Needs Resource Pack)\n" +
                        " - §bcustom_3§r (Needs Resource Pack)"
        ), false);
        return 1;
    }

    private static int setSound(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String soundName = StringArgumentType.getString(context, "sound_name");

        if (!AVAILABLE_SOUNDS.contains(soundName)) {
            source.sendFailure(Component.literal("Invalid sound. Type /runic_curiosities time_hourglass soundlist to see available options."));
            return 0;
        }

        if (source.getEntity() instanceof ServerPlayer player) {
            player.getPersistentData().putString("RunicCuriosities_TimeHourglassSound", soundName);
            source.sendSuccess(() -> Component.literal("Time Hourglass activation sound set to: " + soundName), false);
            return 1;
        } else {
            source.sendFailure(Component.literal("This command can only be executed by a player."));
            return 0;
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player clone = event.getEntity();
        if (original.getPersistentData().contains("RunicCuriosities_TimeHourglassSound")) {
            clone.getPersistentData().putString(
                    "RunicCuriosities_TimeHourglassSound",
                    original.getPersistentData().getString("RunicCuriosities_TimeHourglassSound")
            );
        }
    }

    public static net.minecraft.sounds.SoundEvent getHourglassSound(Player player, net.minecraft.sounds.SoundEvent defaultSound) {
        if (player.getPersistentData().contains("RunicCuriosities_TimeHourglassSound")) {
            String choice = player.getPersistentData().getString("RunicCuriosities_TimeHourglassSound");
            switch (choice) {
                case "default": return defaultSound;
                case "custom_1": return ModSounds.HOURGLASS_CUSTOM_1.get();
                case "custom_2": return ModSounds.HOURGLASS_CUSTOM_2.get();
                case "custom_3": return ModSounds.HOURGLASS_CUSTOM_3.get();
                default: return defaultSound;
            }
        }
        return defaultSound;
    }
}