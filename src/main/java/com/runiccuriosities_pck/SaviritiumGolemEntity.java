package com.runiccuriosities_pck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;

public class SaviritiumGolemEntity extends TamableAnimal implements GeoEntity, RangedAttackMob {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public SimpleContainer inventory;

    private static final EntityDataAccessor<Boolean> DATA_PICKING_UP = SynchedEntityData.defineId(SaviritiumGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SHOOTING = SynchedEntityData.defineId(SaviritiumGolemEntity.class, EntityDataSerializers.BOOLEAN);

    public SaviritiumGolemEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.inventory = new SimpleContainer(27);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.ARMOR, 5.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PICKING_UP, false);
        this.entityData.define(DATA_SHOOTING, false);
    }

    public boolean isPickingUp() { return this.entityData.get(DATA_PICKING_UP); }
    public void setPickingUp(boolean pickingUp) { this.entityData.set(DATA_PICKING_UP, pickingUp); }

    public boolean isShooting() { return this.entityData.get(DATA_SHOOTING); }
    public void setShooting(boolean shooting) { this.entityData.set(DATA_SHOOTING, shooting); }

    @Override
    protected float getStandingEyeHeight(net.minecraft.world.entity.Pose pose, net.minecraft.world.entity.EntityDimensions dimensions) {
        return 0.65F;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new GolemLaserAttackGoal(this));
        this.goalSelector.addGoal(3, new GolemPickupItemGoal(this));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.2D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        // AGGIUNTO: Attacca tutti i mostri (Monster.class include Zombie, Scheletri, Creeper ecc.) a vista
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, true));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.isTame()) {
            if (!this.level().isClientSide) {
                this.tame(player);
                this.navigation.stop();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (this.isOwnedBy(player)) {
            if (player.isShiftKeyDown()) {
                if (!this.level().isClientSide) {
                    player.openMenu(new SimpleMenuProvider(
                            (id, playerInv, p) -> ChestMenu.threeRows(id, playerInv, this.inventory),
                            Component.literal("Saviritium Golem")
                    ));
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else if (player.getItemInHand(hand).isEmpty()) {
                if (!this.level().isClientSide) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.navigation.stop();
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.inventory != null) {
            ListTag listTag = new ListTag();
            for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                ItemStack itemStack = this.inventory.getItem(i);
                if (!itemStack.isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putByte("Slot", (byte) i);
                    itemStack.save(itemTag);
                    listTag.add(itemTag);
                }
            }
            compound.put("Inventory", listTag);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (this.inventory == null) {
            this.inventory = new SimpleContainer(27);
        }
        if (compound.contains("Inventory", 9)) {
            ListTag listTag = compound.getList("Inventory", 10);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag itemTag = listTag.getCompound(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot >= 0 && slot < this.inventory.getContainerSize()) {
                    this.inventory.setItem(slot, ItemStack.of(itemTag));
                }
            }
        }
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.inventory != null) {
            for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                ItemStack itemStack = this.inventory.getItem(i);
                if (!itemStack.isEmpty()) {
                    this.spawnAtLocation(itemStack);
                }
            }
        }
        this.spawnAtLocation(ModItems.SAVIRITIUM_GOLEM_SPAWN_EGG.get());
    }

    public boolean canHoldItem(ItemStack stack) {
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack slotStack = this.inventory.getItem(i);
            if (slotStack.isEmpty()) return true;
            if (ItemStack.isSameItemSameTags(slotStack, stack) && slotStack.getCount() < slotStack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        Arrow arrow = new Arrow(this.level(), this);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - arrow.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        arrow.shoot(d0, d1 + d3 * 0.20D, d2, 1.6F, 0.0F);
        arrow.setBaseDamage(5.0D);

        this.playSound(net.minecraft.sounds.SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(arrow);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {

            if (this.isShooting()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("animation.saviritium_golem.attack"));
                return PlayState.CONTINUE;
            }

            if (this.isPickingUp()) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("animation.saviritium_golem.pickup"));
                return PlayState.CONTINUE;
            }

            if (this.isOrderedToSit()) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.saviritium_golem.idle"));
                return PlayState.CONTINUE;
            }

            if (event.isMoving()) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.saviritium_golem.walk"));
                return PlayState.CONTINUE;
            }

            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.saviritium_golem.idle"));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    class GolemLaserAttackGoal extends Goal {
        private final SaviritiumGolemEntity golem;
        private LivingEntity target;
        private int attackTick = -1;
        private int cooldown = 0;

        public GolemLaserAttackGoal(SaviritiumGolemEntity golem) {
            this.golem = golem;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.golem.isOrderedToSit()) return false;
            LivingEntity livingentity = this.golem.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return (this.canUse() || this.attackTick > 0) && this.target != null && this.target.isAlive();
        }

        @Override
        public void start() {
            this.attackTick = -1;
            this.cooldown = 10;
        }

        @Override
        public void stop() {
            this.target = null;
            this.attackTick = -1;
            this.golem.setShooting(false);
        }

        @Override
        public void tick() {
            if (this.target == null || !this.target.isAlive()) return;

            double distSq = this.golem.distanceToSqr(this.target);
            boolean canSee = this.golem.getSensing().hasLineOfSight(this.target);
            this.golem.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

            if (distSq < 25.0D) {
                Vec3 dir = this.golem.position().subtract(this.target.position()).normalize();
                Vec3 awayPos = this.golem.position().add(dir.scale(4.0));
                this.golem.getNavigation().moveTo(awayPos.x, awayPos.y, awayPos.z, 1.4D);
            } else if (distSq > 144.0D || !canSee) {
                this.golem.getNavigation().moveTo(this.target, 1.25D);
            } else {
                this.golem.getNavigation().stop();
            }

            if (this.cooldown > 0) this.cooldown--;

            if (this.attackTick < 0 && this.cooldown <= 0 && canSee) {
                this.golem.setShooting(true);
                this.attackTick = 1;
            }

            if (this.attackTick > 0) {
                this.attackTick++;

                if (this.attackTick == 35 && !this.golem.level().isClientSide) {
                    this.golem.performRangedAttack(this.target, 1.0f);
                }

                if (this.attackTick >= 60) {
                    this.golem.setShooting(false);
                    this.attackTick = -1;
                    this.cooldown = 15;
                }
            }
        }
    }

    class GolemPickupItemGoal extends Goal {
        private final SaviritiumGolemEntity golem;
        private ItemEntity targetItem;
        private int pickupTick;

        public GolemPickupItemGoal(SaviritiumGolemEntity golem) {
            this.golem = golem;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.golem.isOrderedToSit() || !this.golem.isTame()) return false;
            if (this.golem.isPickingUp() || this.golem.isShooting()) return false;
            if (this.golem.getTarget() != null) return false;

            List<ItemEntity> items = this.golem.level().getEntitiesOfClass(
                    ItemEntity.class,
                    this.golem.getBoundingBox().inflate(8.0D, 3.0D, 8.0D),
                    item -> item.isAlive() && !item.hasPickUpDelay() && this.golem.canHoldItem(item.getItem())
            );

            if (items.isEmpty()) return false;
            this.targetItem = items.get(0);
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.golem.isOrderedToSit()) return false;
            if (this.golem.getTarget() != null) return false;

            if (this.golem.isPickingUp()) {
                return this.pickupTick < 65;
            }
            return this.targetItem != null && this.targetItem.isAlive() && this.golem.canHoldItem(this.targetItem.getItem());
        }

        @Override
        public void start() {
            this.pickupTick = 0;
            this.golem.getNavigation().moveTo(this.targetItem, 1.2D);
        }

        @Override
        public void stop() {
            this.targetItem = null;
            this.golem.setPickingUp(false);
            this.pickupTick = 0;
            this.golem.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.golem.isPickingUp()) {
                this.golem.getNavigation().stop();
                this.pickupTick++;

                if (!this.golem.level().isClientSide && this.pickupTick == 35 && this.targetItem != null && this.targetItem.isAlive()) {
                    ItemStack stackToPickup = this.targetItem.getItem().copy();
                    ItemStack remainder = this.golem.inventory.addItem(stackToPickup);

                    if (remainder.getCount() < this.targetItem.getItem().getCount()) {
                        this.golem.take(this.targetItem, this.targetItem.getItem().getCount() - remainder.getCount());
                        this.golem.playSound(net.minecraft.sounds.SoundEvents.ITEM_PICKUP, 0.2F, (this.golem.getRandom().nextFloat() - this.golem.getRandom().nextFloat()) * 0.2F + 1.0F);
                        if (remainder.isEmpty()) {
                            this.targetItem.discard();
                        } else {
                            this.targetItem.setItem(remainder);
                        }
                    }
                }
                return;
            }

            if (this.targetItem == null || !this.targetItem.isAlive()) return;

            this.golem.getLookControl().setLookAt(this.targetItem, 30.0F, 30.0F);
            double distance = this.golem.distanceToSqr(this.targetItem);

            if (distance > 4.0D) {
                this.golem.getNavigation().moveTo(this.targetItem, 1.2D);
            } else {
                this.golem.getNavigation().stop();
                this.golem.setPickingUp(true);
                this.pickupTick = 0;
            }
        }
    }
}