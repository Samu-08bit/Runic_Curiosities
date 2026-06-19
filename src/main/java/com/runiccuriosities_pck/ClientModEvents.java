package com.runiccuriosities_pck;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        // Ci agganciamo alla Vignette, l'overlay ambientale a tutto schermo perfetto per i filtri visivi
        if (event.getOverlay().id().equals(VanillaGuiOverlay.VIGNETTE.id())) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            if (player != null && mc.level != null) {
                // Controlliamo se il giocatore ha l'effetto Night Vision attivo (sincronizzato dal server)
                if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                    // Verifichiamo che l'effetto derivi effettivamente dal nostro amuleto equipaggiato
                    boolean hasEyes = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.SCARLET_EYES.get()).isPresent();

                    if (hasEyes) {
                        int width = event.getWindow().getGuiScaledWidth();
                        int height = event.getWindow().getGuiScaledHeight();

                        // RenderType.guiOverlay() forza il motore grafico ad attivare il blend alpha
                        // e a disegnare sopra l'intero scenario senza subire il clipping del mondo
                        event.getGuiGraphics().fill(RenderType.guiOverlay(), 0, 0, width, height, 0x66FF0000);
                    }
                }
            }
        }
    }
}