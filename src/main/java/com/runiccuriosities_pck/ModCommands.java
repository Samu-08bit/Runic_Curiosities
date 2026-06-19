package com.runiccuriosities_pck;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = RunicCuriosities.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("runic_curiosities")
                // Requires OP level 2 to execute
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("timestop")
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            ServerPlayer player = source.getPlayerOrException();
                            ServerLevel level = source.getLevel();

                            // 1. Apply absolute Slowness: Amplifier 255 is CRITICAL to trigger TimeFreezeEvents logic
                            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 255, false, false));

                            // 2. Apply secondary visual effects
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 4, false, false));
                            player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 0, false, false));

                            // 3. Time Stop sound effects
                            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 1.5F, 0.5F);
                            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 2.0F, 0.1F);

                            // 4. Send the visual sync packet to spawn the rainbow cube around the player
                            ModMessages.INSTANCE.send(PacketDistributor.ALL.noArg(),
                                    new PacketSyncTimeFreeze(player.getX(), player.getY(), player.getZ()));

                            // 5. Send chat feedback to the executor
                            source.sendSuccess(() -> Component.literal("§b[Runic Curiosities] §fTime Stop forced on yourself!"), false);

                            return 1;
                        })
                )
        );
    }
}