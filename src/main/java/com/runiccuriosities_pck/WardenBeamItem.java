package com.runiccuriosities_pck;

import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

public class WardenBeamItem extends TalismanItem implements ICurioItem {

    private static final int MAX_COOLDOWN = 600; // 30 seconds (30 * 20 ticks)

    public WardenBeamItem() {
        // Set the base durability to make the cooldown bar work
        super(new Item.Properties().stacksTo(1).durability(MAX_COOLDOWN));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!slotContext.entity().level().isClientSide()) {
            CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag tag = data.copyTag();
            int cooldown = tag.contains("BeamCooldown") ? tag.getInt("BeamCooldown") : 0;

            if (cooldown > 0) {
                cooldown--;
                tag.putInt("BeamCooldown", cooldown);
                stack.setDamageValue(cooldown);
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
        }
    }

    // Called from the network packet when the player presses the key
    public static void tryShootBeam(Player player) {
        if (player.level().isClientSide()) return;

        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            handler.findFirstCurio(stack -> stack.getItem() instanceof WardenBeamItem).ifPresent(result -> {
                ItemStack stack = result.stack();
                CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                CompoundTag tag = data.copyTag();
                int cooldown = tag.contains("BeamCooldown") ? tag.getInt("BeamCooldown") : 0;

                if (cooldown <= 0) {
                    shootBeam((ServerLevel) player.level(), player);
                    tag.putInt("BeamCooldown", MAX_COOLDOWN);
                    stack.setDamageValue(MAX_COOLDOWN);
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

                    // Apply negative effects: Slowness III for 15 sec, Weakness I for 20 sec
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 15 * 20, 2, true, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 20, 0, true, false, true));
                }
            });
        });
    }

    private static void shootBeam(ServerLevel level, Player player) {
        Vec3 startPos = player.position().add(0, 1.6f, 0); // Chest height roughly
        Vec3 look = player.getLookAngle();
        double distance = 15.0; // Maximum beam distance
        Vec3 endPos = startPos.add(look.scale(distance));

        // Sonic boom sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0F, 1.0F);

        // Visual effects (Sonic Boom particles along the trajectory)
        for (int i = 1; i < Math.floor(distance) * 2; ++i) {
            Vec3 particlePos = startPos.add(look.scale((double) i * 0.5));
            level.sendParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        // Damage and knockback that bypasses armor
        AABB hitBox = new AABB(startPos, endPos).inflate(2.0D);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, hitBox, e -> e != player && e.isAlive());

        for (LivingEntity target : targets) {
            // Check if the entity is close to the center of the beam
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
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.contains("BeamCooldown") && customData.copyTag().getInt("BeamCooldown") > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        int cooldown = customData.contains("BeamCooldown") ? customData.copyTag().getInt("BeamCooldown") : 0;
        return Math.round(13.0F - (float) cooldown * 13.0F / (float) MAX_COOLDOWN);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FF00; // Green, you can change it if you prefer another color
    }
}