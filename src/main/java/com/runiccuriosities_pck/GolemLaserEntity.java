package com.runiccuriosities_pck;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class GolemLaserEntity extends AbstractArrow implements ItemSupplier {
	public static final ItemStack PROJECTILE_ITEM = new ItemStack(Blocks.AIR);

	public GolemLaserEntity(PlayMessages.SpawnEntity packet, Level world) {
		super(ModEntities.GOLEM_LASER.get(), world);
		this.setNoGravity(true);
	}

	public GolemLaserEntity(EntityType<? extends GolemLaserEntity> type, Level world) {
		super(type, world);
		this.setNoGravity(true);
	}

	public GolemLaserEntity(EntityType<? extends GolemLaserEntity> type, double x, double y, double z, Level world) {
		super(type, x, y, z, world);
		this.setNoGravity(true);
	}

	public GolemLaserEntity(EntityType<? extends GolemLaserEntity> type, LivingEntity entity, Level world) {
		super(type, entity, world);
		this.setNoGravity(true);
	}

	@Override
	public boolean isNoGravity() {
		return true;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem() {
		return PROJECTILE_ITEM;
	}

	@Override
	protected ItemStack getPickupItem() {
		return PROJECTILE_ITEM;
	}

	@Override
	protected void doPostHurtEffects(LivingEntity entity) {
		super.doPostHurtEffects(entity);
		entity.setArrowCount(entity.getArrowCount() - 1);
	}

	// --- NUOVA MECCANICA: QUANDO COLPISCE UN'ENTITÀ ---
	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		// Dà fuoco al bersaglio per 5 secondi
		result.getEntity().setSecondsOnFire(5);

		// Il laser si distrugge IMMEDIATAMENTE (non rimbalza se colpisce uno scudo)
		this.discard();
	}

	// --- NUOVA MECCANICA: QUANDO COLPISCE UN BLOCCO ---
	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);

		if (!this.level().isClientSide) {
			// Trova la posizione esatta contro cui ha sbattuto
			BlockPos firePos = result.getBlockPos().relative(result.getDirection());

			// Se il blocco vicino è vuoto (aria), ci piazza il fuoco!
			if (this.level().isEmptyBlock(firePos) || this.level().getBlockState(firePos).canBeReplaced()) {
				this.level().setBlockAndUpdate(firePos, Blocks.FIRE.defaultBlockState());
			}
		}

		// Il laser scompare (così non attraversa i muri o si incastra)
		this.discard();
	}

	@Override
	protected net.minecraft.sounds.SoundEvent getDefaultHitGroundSoundEvent() {
		return net.minecraft.sounds.SoundEvents.EMPTY; // Nessun suono legnoso
	}

	@Override
	public void tick() {
		super.tick();
		// Distrugge il laser dopo un po' se vola all'infinito per non far laggare il mondo
		if (this.tickCount > 60) {
			this.discard();
		}
	}

	// I metodi shoot statici generati da MCreator...
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
		entityarrow.setKnockback(knockback);
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
		entityarrow.setKnockback(5);
		entityarrow.setCritArrow(true);
		entity.level().addFreshEntity(entityarrow);
		return entityarrow;
	}
}