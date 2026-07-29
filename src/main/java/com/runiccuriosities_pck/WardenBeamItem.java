package com.runiccuriosities_pck;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

public class WardenBeamItem extends Item implements ICurioItem {

    private static final int MAX_COOLDOWN = 600; // 30 secondi (30 * 20 ticks)

    public WardenBeamItem() {
        // Impostiamo la durabilità di base in modo da far funzionare la barra del cooldown
        super(new Item.Properties().stacksTo(1).defaultDurability(MAX_COOLDOWN));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!slotContext.entity().level().isClientSide()) {
            CompoundTag tag = stack.getOrCreateTag();
            int cooldown = tag.getInt("BeamCooldown");
            if (cooldown > 0) {
                cooldown--;
                tag.putInt("BeamCooldown", cooldown);
                stack.setDamageValue(cooldown);
            }
        }
    }

    // Viene chiamato dal pacchetto di rete quando il giocatore preme il tasto
    public static void tryShootBeam(Player player) {
        if (player.level().isClientSide()) return;

        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            handler.findFirstCurio(stack -> stack.getItem() instanceof WardenBeamItem).ifPresent(result -> {
                ItemStack stack = result.stack();
                CompoundTag tag = stack.getOrCreateTag();
                if (tag.getInt("BeamCooldown") <= 0) {
                    shootBeam((ServerLevel) player.level(), player);
                    tag.putInt("BeamCooldown", MAX_COOLDOWN);
                    stack.setDamageValue(MAX_COOLDOWN);

                    // Applico gli effetti negativi: Slowness III per 15 sec, Weakness I per 20 sec
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 15 * 20, 2, true, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 20, 0, true, false, true));
                }
            });
        });
    }

    private static void shootBeam(ServerLevel level, Player player) {
        Vec3 startPos = player.position().add(0, 1.6f, 0); // Altezza del petto circa
        Vec3 look = player.getLookAngle();
        double distance = 15.0; // Distanza massima del raggio
        Vec3 endPos = startPos.add(look.scale(distance));

        // Suono del sonic boom
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0F, 1.0F);

        // Effetti visivi (particelle Sonic Boom lungo la traiettoria)
        for (int i = 1; i < Math.floor(distance) * 2; ++i) {
            Vec3 particlePos = startPos.add(look.scale((double) i * 0.5));
            level.sendParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        // Danno e knockback che bypassano l'armatura
        AABB hitBox = new AABB(startPos, endPos).inflate(2.0D);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, hitBox, e -> e != player && e.isAlive());

        for (LivingEntity target : targets) {
            // Controlla se l'entità è vicina al centro del raggio
            Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2, 0);
            double distToLine = targetPos.subtract(startPos).cross(look).length();
            if (distToLine < 1.5) {
                target.hurt(level.damageSources().sonicBoom(player), 10.0F);

                // Knockback
                double dx = target.getX() - player.getX();
                double dz = target.getZ() - player.getZ();
                double length = Math.sqrt(dx * dx + dz * dz);
                if (length > 0.0) {
                    target.push(dx / length * 1.5, 0.5, dz / length * 1.5);
                }
            }
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getOrCreateTag().getInt("BeamCooldown") > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int cooldown = stack.getOrCreateTag().getInt("BeamCooldown");
        return Math.round(13.0F - (float) cooldown * 13.0F / (float) MAX_COOLDOWN);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FF00; // Verde, puoi cambiarlo se preferisci un altro colore
    }
}