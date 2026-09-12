package com.runiccuriosities_pck;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GolemLaserEntity extends AbstractArrow implements ItemSupplier {
    public static final ItemStack PROJECTILE_ITEM = new ItemStack(Blocks.AIR);

    public GolemLaserEntity(EntityType<? extends GolemLaserEntity> type, Level world) {
        super(type, world);
        this.setNoGravity(true);
    }

    public GolemLaserEntity(EntityType<? extends GolemLaserEntity> type, double x, double y, double z, Level world) {
        super(type, x, y, z, world, PROJECTILE_ITEM, null);
        this.setNoGravity(true);
    }

    public GolemLaserEntity(EntityType<? extends GolemLaserEntity> type, LivingEntity entity, Level world) {
        super(type, entity, world, PROJECTILE_ITEM, null);
        this.setNoGravity(true);
    }

    @Override
    public boolean isNoGravity() { return true; }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() { return PROJECTILE_ITEM; }

    @Override
    protected ItemStack getDefaultPickupItem() { return PROJECTILE_ITEM; }

    @Override
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        entity.setArrowCount(entity.getArrowCount() - 1);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().igniteForSeconds(5);
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            BlockPos firePos = result.getBlockPos().relative(result.getDirection());
            if (this.level().isEmptyBlock(firePos) || this.level().getBlockState(firePos).canBeReplaced()) {
                this.level().setBlockAndUpdate(firePos, Blocks.FIRE.defaultBlockState());
            }
        }
        this.discard();
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getDefaultHitGroundSoundEvent() {
        return net.minecraft.sounds.SoundEvents.EMPTY;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > 60) this.discard();
    }

    public static GolemLaserEntity shoot(Level world, LivingEntity entity, RandomSource source) {
        return shoot(world, entity, source, 3f, 5, 5);
    }

    public static GolemLaserEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
        return shoot(world, entity, source, pullingPower * 3f, 5, 5);
    }

    public static GolemLaserEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
        GolemLaserEntity entityarrow = new GolemLaserEntity(ModEntities.GOLEM_LASER.get(), entity, world);
        entityarrow.shoot(entity.getViewVector(1).x, entity.getViewVector(1).y, entity.getViewVector(1).z, power * 2, 0);
        entityarrow.setSilent(true);
        entityarrow.setCritArrow(true);
        entityarrow.setBaseDamage(damage);
        world.addFreshEntity(entityarrow);
        return entityarrow;
    }

    public static GolemLaserEntity shoot(LivingEntity entity, LivingEntity target) {
        GolemLaserEntity entityarrow = new GolemLaserEntity(ModEntities.GOLEM_LASER.get(), entity, entity.level());
        double dx = target.getX() - entity.getX();
        double dy = target.getY() + target.getEyeHeight() - 1.1;
        double dz = target.getZ() - entity.getZ();
        entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.2F, dz, 3f * 2, 12.0F);
        entityarrow.setSilent(true);
        entityarrow.setBaseDamage(5);
        entityarrow.setCritArrow(true);
        entity.level().addFreshEntity(entityarrow);
        return entityarrow;
    }
}