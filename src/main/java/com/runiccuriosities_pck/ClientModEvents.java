package com.runiccuriosities_pck;

import com.runiccuriosities_pck.client.model.Modelgolem_laser;
import com.runiccuriosities_pck.client.renderer.GolemLaserRenderer;
import com.runiccuriosities_pck.client.screen.MagicModTableScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

import java.awt.Color;
import java.util.WeakHashMap;

public class ClientModEvents {

    @Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        // Handles custom keybind inputs on the client
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen == null && ModKeyBindings.WARDEN_BEAM_KEY.consumeClick()) {
                PacketHandler.INSTANCE.sendToServer(new WardenBeamPacket());
            }
        }

        // Handles custom tooltips and dynamic descriptions for mod items
        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            if (event.getItemStack().isEmpty()) return;

            ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
            if (registryName != null && registryName.getNamespace().equals(RunicCuriosities.MODID)
                    && !registryName.getPath().equals("saviritium_compound")
                    && !registryName.getPath().equals("saviritium_compound_block")
                    && !registryName.getPath().equals("magic_mod_table")) {

                String path = registryName.getPath();

                if (Screen.hasAltDown()) {
                    long time = System.currentTimeMillis();
                    int rgb = Color.HSBtoRGB((time % 2000L) / 2000.0f, 1.0f, 1.0f);

                    switch(path) {
                        // 1. Talisman of Intuition
                        case "example_item":
                        case "talisman_of_intuition":
                            event.getToolTip().add(Component.literal("Provides infinite Glowing status effect while equipped.").withStyle(ChatFormatting.GOLD));
                            break;
                        // 2. Golden Emerald
                        case "golden_emerald":
                            event.getToolTip().add(Component.literal("Grants infinite Hero of the Village. Piglins will ignore you unless attacked.").withStyle(ChatFormatting.DARK_GREEN));
                            break;
                        // 3. Egg of Gluttony
                        case "egg_of_gluttony":
                            event.getToolTip().add(Component.literal("Inflicts infinite Hunger. Eating food grants Strength for 5 mins (stacks up to Strength V).").withStyle(ChatFormatting.RED));
                            break;
                        // 4. Scarlet Eyes
                        case "scarlet_eyes":
                            event.getToolTip().add(Component.literal("Activates at dusk, providing infinite Night Vision and a reddish vision filter.").withStyle(ChatFormatting.DARK_RED));
                            break;
                        // 5. Ignitor Shield
                        case "ignitor_shield":
                            event.getToolTip().add(Component.literal("Provides infinite Fire Resistance, ignites foes on hit, and deflects most projectiles.").withStyle(ChatFormatting.GOLD));
                            break;
                        // 6. Recharging Bread
                        case "recharging_bread":
                            event.getToolTip().add(Component.literal("Infinite Saturation. Striking foes calls down lightning. Consumes Wheat when discharged.").withStyle(ChatFormatting.YELLOW));
                            break;
                        // 7. Glass Cloth
                        case "glass_cloth":
                            event.getToolTip().add(Component.literal("Grants infinite invisibility (hides armor except boots) until you touch a liquid.").withStyle(ChatFormatting.GRAY));
                            break;
                        // 8. Guardian Golem
                        case "guardian_golem":
                            event.getToolTip().add(Component.literal("Summons an iron golem to protect you when hit (max 1 min). Recharges with Iron Ingot.").withStyle(ChatFormatting.DARK_GREEN));
                            break;
                        // 9. Car Bomb
                        case "car_bomb":
                            event.getToolTip().add(Component.literal("Summons a TNT minecart under a target hit by a projectile. Recharges with TNT.").withStyle(ChatFormatting.RED));
                            break;
                        // 10. Energy Drink
                        case "energy_drink":
                            event.getToolTip().add(Component.literal("Infinite Haste I, Speed I, and Jump Boost I.").withStyle(ChatFormatting.YELLOW));
                            break;
                        // 11. Time Hourglass
                        case "time_hourglass":
                            event.getToolTip().add(Component.literal("Freezes time in an area for 15 seconds (keybind). 3 min cooldown, recharges with Soul Sand.").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb))));
                            break;
                        // 12. Sponge Ring
                        case "sponge_ring":
                            event.getToolTip().add(Component.literal("Continuously drains liquids in a 4-block radius.").withStyle(ChatFormatting.DARK_BLUE));
                            break;
                        // 13. Viper's Embrace
                        case "vipers_embrace":
                            event.getToolTip().add(Component.literal("Poison immunity. Inflicts Poison on attacked enemies (10 seconds).").withStyle(ChatFormatting.DARK_GREEN));
                            break;
                        // 14. Heart of Resolution
                        case "heart_of_resolution":
                            event.getToolTip().add(Component.literal("Resistance I and +20 Max Health (+10 hearts). Automatically gains Curse of Binding.").withStyle(ChatFormatting.DARK_RED));
                            break;
                        // 15. Warden Antennas
                        case "warden_antennas":
                            event.getToolTip().add(Component.literal("Nearby entities suffer from Darkness and Glowing.").withStyle(ChatFormatting.DARK_AQUA));
                            break;
                        // 16. Spider Boots
                        case "spider_boots":
                            event.getToolTip().add(Component.literal("Climb walls, no fall damage, completely silent footsteps.").withStyle(ChatFormatting.DARK_RED));
                            break;
                        // 17. Fairy Wings
                        case "fairy_wings":
                            event.getToolTip().add(Component.literal("Infinite Regeneration I and Luck I. Grants Creative flight.").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb))));
                            break;
                        // 18. Neptune's Helmet
                        case "neptunes_helmet":
                            event.getToolTip().add(Component.literal("Grants water-related powers (animates when in fluids).").withStyle(ChatFormatting.DARK_AQUA));
                            break;
                        // 19. Random Cauldron
                        case "random_cauldron":
                            event.getToolTip().add(Component.literal("Every 3 mins, grants a random positive effect with initial Nausea. Recharges with Ghast Tear.").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb))));
                            break;
                        // 20. Warden Beam
                        case "warden_beam":
                            event.getToolTip().add(Component.literal("Fires a Sonic Boom (30s cooldown) followed by Slowness and Weakness.").withStyle(ChatFormatting.DARK_AQUA));
                            break;
                        // Saviritium Golem Spawn Egg
                        case "saviritium_golem_spawn_egg":
                            event.getToolTip().add(Component.literal("Need a friend who doesn't talk back but carries 27 slots of dirt? Look no further! This one-wheeled wonder will fiercely protect you with its tail laser and follow you everywhere.").withStyle(ChatFormatting.GRAY));
                            break;
                        // Blank Rune
                        case "blank_rune":
                            event.getToolTip().add(Component.literal("A blank runic slate crafted from gold,copper and other materials. Used as the base foundation for all Saviritium Golem upgrade runes.").withStyle(ChatFormatting.GRAY));
                            break;
                    }
                } else {
                    event.getToolTip().add(Component.literal("Hold [ALT] for details").withStyle(ChatFormatting.DARK_GRAY));
                }

                // Displays installed upgrade chips on the Golem Spawn Egg
                if (path.equals("saviritium_golem_spawn_egg")) {
                    NonNullList<ItemStack> upgrades = MagicModTableMenu.getUpgradesFromEgg(event.getItemStack());
                    int installedCount = 0;
                    for (ItemStack s : upgrades) {
                        if (!s.isEmpty()) installedCount++;
                    }
                    if (installedCount > 0) {
                        event.getToolTip().add(Component.empty());
                        event.getToolTip().add(Component.literal("Installed Modules (" + installedCount + "/4):").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                        for (int i = 0; i < 4; i++) {
                            ItemStack chip = upgrades.get(i);
                            if (!chip.isEmpty()) {
                                event.getToolTip().add(Component.literal("• Slot " + (i + 1) + ": ").withStyle(ChatFormatting.YELLOW)
                                        .append(Component.literal(chip.getHoverName().getString()).withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)));
                                String[] info = getModuleInfo(chip);
                                event.getToolTip().add(Component.literal("   Boost: ").withStyle(ChatFormatting.GRAY)
                                        .append(Component.literal(info[0]).withStyle(ChatFormatting.GREEN)));
                                event.getToolTip().add(Component.literal("   " + info[1]).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                            } else {
                                event.getToolTip().add(Component.literal("• Slot " + (i + 1) + ": [Empty]").withStyle(ChatFormatting.DARK_GRAY));
                            }
                        }
                    }
                }
            }
        }

        // Retrieves stat boost and description for installed upgrade modules
        private static String[] getModuleInfo(ItemStack chip) {
            if (chip.isEmpty()) {
                return new String[]{"None", "No module installed."};
            }
            if (chip.hasTag()) {
                CompoundTag tag = chip.getTag();
                if (tag.contains("StatBoost") && tag.contains("MiniDesc")) {
                    return new String[]{tag.getString("StatBoost"), tag.getString("MiniDesc")};
                }
            }
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(chip.getItem());
            String path = key != null ? key.getPath().toLowerCase() : "";
            if (path.contains("blank_rune")) {
                return new String[]{"No Upgrade", "Blank base rune ready to be engraved or upgraded."};
            } else if (path.contains("laser") || path.contains("amplifier")) {
                return new String[]{"+4 Laser Damage, -25% Cooldown", "Amplifies the laser beam emitter attack damage and reduces cooldown."};
            } else if (path.contains("speed") || path.contains("mobility") || path.contains("wheel")) {
                return new String[]{"+30% Movement Speed", "Overclocks the kinetic monowheel drive to increase golem movement speed."};
            } else if (path.contains("repair") || path.contains("nanite") || path.contains("heal")) {
                return new String[]{"+1 Heart every 4 seconds", "Passively repairs golem health over time while out of combat."};
            } else if (path.contains("shield") || path.contains("barrier") || path.contains("defense")) {
                return new String[]{"+20% Projectile Resistance", "Projects a temporary energy barrier that absorbs projectiles directed at the golem."};
            }
            return new String[]{"+1 Active Module", "Upgrade chip installed on the Saviritium Golem."};
        }

        // Temporary storage for player armor when Glass Cloth is active
        private static final java.util.Map<Player, NonNullList<ItemStack>> savedArmor = new WeakHashMap<>();

        // Hides player armor except boots during invisibility with Glass Cloth
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
        public static void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
            Player player = event.getEntity();
            if (player.hasEffect(MobEffects.INVISIBILITY) &&
                    CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.GLASS_CLOTH.get()).isPresent()) {
                NonNullList<ItemStack> currentArmor = player.getInventory().armor;
                NonNullList<ItemStack> copy = NonNullList.withSize(4, ItemStack.EMPTY);
                for (int i = 0; i < 4; i++) {
                    copy.set(i, currentArmor.get(i));
                }
                savedArmor.put(player, copy);
                player.getInventory().armor.set(3, ItemStack.EMPTY);
                player.getInventory().armor.set(2, ItemStack.EMPTY);
                player.getInventory().armor.set(1, ItemStack.EMPTY);
            }
        }

        // Restores player armor pieces after rendering
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST)
        public static void onPlayerRenderPost(RenderPlayerEvent.Post event) {
            Player player = event.getEntity();
            if (savedArmor.containsKey(player)) {
                NonNullList<ItemStack> copy = savedArmor.remove(player);
                if (copy != null) {
                    for (int i = 0; i < 4; i++) {
                        player.getInventory().armor.set(i, copy.get(i));
                    }
                }
            }
        }

        // Draws reddish camera overlay for Scarlet Eyes at night
        @SubscribeEvent
        public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
            if (VanillaGuiOverlay.VIGNETTE.type().equals(event.getOverlay())) {
                Minecraft mc = Minecraft.getInstance();
                LocalPlayer player = mc.player;
                if (player != null && mc.level != null) {
                    boolean hasEyes = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.SCARLET_EYES.get()).isPresent();
                    if (hasEyes && (player.hasEffect(MobEffects.NIGHT_VISION) || player.level().isNight())) {
                        int width = event.getWindow().getGuiScaledWidth();
                        int height = event.getWindow().getGuiScaledHeight();
                        event.getGuiGraphics().fill(0, 0, width, height, 0x66FF0000);
                    }
                }
            }
        }
    }

    // Client setup: registers screens, render layers, and item properties
    @Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientSetupEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(ModMenuTypes.MAGIC_MOD_TABLE_MENU.get(), MagicModTableScreen::new);
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.MAGIC_MOD_TABLE.get(), RenderType.translucent());

                ItemProperties.register(ModItems.NEPTUNES_HELMET.get(), new ResourceLocation(RunicCuriosities.MODID, "anim_state"),
                        (itemStack, clientLevel, livingEntity, seed) -> {
                            CompoundTag tag = itemStack.getTag();
                            if (tag != null && tag.contains("AnimState")) {
                                return tag.getInt("AnimState") / 2.0F;
                            }
                            return 0.0F;
                        });
            });
        }

        // Registers entity renderers for custom entities
        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.SAVIRITIUM_GOLEM.get(), SaviritiumGolemRenderer::new);
            event.registerEntityRenderer(ModEntities.GOLEM_LASER.get(), GolemLaserRenderer::new);
        }

        // Registers entity model layer definitions
        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(Modelgolem_laser.LAYER_LOCATION, Modelgolem_laser::createBodyLayer);
        }
    }
}