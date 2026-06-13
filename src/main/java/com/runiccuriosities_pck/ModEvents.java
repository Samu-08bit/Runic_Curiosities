package com.runiccuriosities_pck;

import com.runiccuriosities_pck.init.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RunicCuriosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Eseguiamo il controllo solo alla fine del tick e solo sul lato Server (per evitare desincronizzazioni)
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {

            // Chiediamo a Curios se l'oggetto è presente in uno dei suoi slot sul giocatore
            boolean haIlTalismano = top.theillusivec4.curios.api.CuriosApi.getCuriosHelper()
                    .findFirstCurio(event.player, ModItems.example_item.get()).isPresent();

            if (haIlTalismano) {
                // Applichiamo l'effetto Glowing.
                // 210 tick di durata (circa 10 secondi, si rinnova continuamente finché è equipaggiato)
                // Gli ultimi tre parametri (false, false, true) servono a nascondere le particelle fastidiose sullo schermo
                event.player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 210, 0, false, false, true));
            }
        }
    }
}