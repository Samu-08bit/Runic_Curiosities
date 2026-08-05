package com.runiccuriosities_pck;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

public class ClientModEvents {

    // 1. Bus FORGE: Per gli eventi "in-game" (es. overlay rosso sullo schermo)
    @Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen == null && ModKeyBindings.WARDEN_BEAM_KEY.consumeClick()) {
                PacketHandler.INSTANCE.sendToServer(new WardenBeamPacket());
            }
        }

        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            if (event.getItemStack().isEmpty()) return;

            ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
            if (registryName != null && registryName.getNamespace().equals(RunicCuriosities.MODID)
                    && !registryName.getPath().equals("saviritium_compound")
                    && !registryName.getPath().equals("saviritium_compound_block")) {

                if (Screen.hasAltDown()) {
                    String path = registryName.getPath();
                    switch(path) {
                        case "example_item": // In caso l'ID sia ancora example_item
                        case "talisman_of_intuition":
                            event.getToolTip().add(Component.literal("Provides infinite Glowing status effect while equipped.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "golden_emerald":
                            event.getToolTip().add(Component.literal("Grants infinite Hero of the Village effect. Normal piglins will not attack you.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "egg_of_gluttony":
                            event.getToolTip().add(Component.literal("Inflicts infinite Hunger. Eating food grants Strength for 5 minutes.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "scarlet_eyes":
                            event.getToolTip().add(Component.literal("At night, provides infinite Night Vision and a reddish visual effect.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "ignitor_shield":
                            event.getToolTip().add(Component.literal("Infinite Fire Resistance, projectile immunity, attackers catch fire. Fireproof.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "recharging_bread":
                            event.getToolTip().add(Component.literal("Infinite Saturation. Striking an enemy summons lightning. Recharges with Wheat.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "glass_cloth":
                            event.getToolTip().add(Component.literal("Infinite Invisibility until you touch a liquid.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "guardian_golem":
                            event.getToolTip().add(Component.literal("Summons an iron golem to protect you when hit (max 1 min). Recharges with Iron Ingot.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "car_bomb":
                            event.getToolTip().add(Component.literal("Summons a TNT minecart under a target hit by a projectile. Recharges with TNT.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "energy_drink":
                            event.getToolTip().add(Component.literal("Infinite Haste I, Speed I, and Jump Boost I.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "time_hourglass":
                            event.getToolTip().add(Component.literal("Freezes time in an area for 15 seconds (keybind). 3 min cooldown, recharges with Soul Sand.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "sponge_ring":
                            event.getToolTip().add(Component.literal("Continuously drains liquids in a 4-block radius.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "vipers_embrace":
                            event.getToolTip().add(Component.literal("Poison immunity. Inflicts Poison on attacked enemies (10 seconds).").withStyle(ChatFormatting.GRAY));
                            break;
                        case "heart_of_resolution":
                            event.getToolTip().add(Component.literal("Resistance I and +20 Max Health (+10 hearts). Automatically gains Curse of Binding.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "warden_antennas":
                            event.getToolTip().add(Component.literal("Nearby entities suffer from Darkness and Glowing.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "spider_boots":
                            event.getToolTip().add(Component.literal("Climb walls, no fall damage, completely silent footsteps.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "fairy_wings":
                            event.getToolTip().add(Component.literal("Infinite Regeneration I and Luck I. Grants Creative flight.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "neptunes_helmet":
                            event.getToolTip().add(Component.literal("Grants water-related powers (animates when in fluids).").withStyle(ChatFormatting.GRAY));
                            break;
                        case "random_cauldron":
                            event.getToolTip().add(Component.literal("Every 3 mins, grants a random positive effect with initial Nausea. Recharges with Ghast Tear.").withStyle(ChatFormatting.GRAY));
                            break;
                        case "warden_beam":
                            event.getToolTip().add(Component.literal("Fires a Sonic Boom (30s cooldown) followed by Slowness and Weakness.").withStyle(ChatFormatting.GRAY));
                            break;
                    }
                } else {
                    event.getToolTip().add(Component.literal("Hold [ALT] for details").withStyle(ChatFormatting.DARK_GRAY));
                }
            }
        }

        @SubscribeEvent
        public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
            if (event.getOverlay().id().equals(VanillaGuiOverlay.VIGNETTE.id())) {
                Minecraft mc = Minecraft.getInstance();
                LocalPlayer player = mc.player;

                if (player != null && mc.level != null) {
                    if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                        boolean hasEyes = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.SCARLET_EYES.get()).isPresent();
                        if (hasEyes) {
                            int width = event.getWindow().getGuiScaledWidth();
                            int height = event.getWindow().getGuiScaledHeight();
                            event.getGuiGraphics().fill(RenderType.guiOverlay(), 0, 0, width, height, 0x66FF0000);
                        }
                    }
                }
            }
        }
    }

    // 2. Bus MOD: Per le registrazioni di setup lato client (es. Animazione elmetto)
    @Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientSetupEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                // Registriamo la proprietà "anim_state" per il Neptune's Helmet
                ItemProperties.register(ModItems.NEPTUNES_HELMET.get(), new ResourceLocation(RunicCuriosities.MODID, "anim_state"),
                        (itemStack, clientLevel, livingEntity, seed) -> {
                            if (itemStack.hasTag() && itemStack.getTag().contains("AnimState")) {
                                // Ritorna 0.0, 0.5 o 1.0 a seconda dello stato per cambiare texture
                                return itemStack.getTag().getInt("AnimState") / 2.0F;
                            }
                            return 0.0F;
                        });
            });
        }
    }
}