package com.runiccuriosities_pck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SaviritiumGolemEntity extends TamableAnimal implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Il nostro zaino! 27 slot (come una cassa singola)
    public SimpleContainer inventory;

    public SaviritiumGolemEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.inventory = new SimpleContainer(27);
    }

    // 1. STATISTICHE BASE DEL GOLEM
    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR, 5.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    // FIX 1: Diciamo al gioco che questo golem non può riprodursi
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // Niente cuccioli di golem!
    }

    // 2. INTELLIGENZA ARTIFICIALE (AI) E GOAL
    @Override
    protected void registerGoals() {
        // FIX 2: Usa addGoal invece di add
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this)); // Sta fermo se glielo dici
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.2D, 10.0F, 2.0F, false)); // Segue il padrone
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D)); // Passeggia a caso
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F)); // Guarda i giocatori
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    // 3. GESTIONE DEL CLICK COL TASTO DESTRO
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        // Se non ha ancora un padrone, il primo che fa tasto destro diventa il proprietario
        if (!this.isTame()) {
            if (!this.level().isClientSide) {
                this.tame(player);
                this.navigation.stop();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // Se è già addomesticato e chi clicca è il padrone:
        if (this.isOwnedBy(player)) {
            // A) SHIFT + Click Destro -> Apri lo zaino (come un asino)
            if (player.isShiftKeyDown()) {
                if (!this.level().isClientSide) {
                    player.openMenu(new SimpleMenuProvider(
                            (id, playerInv, p) -> ChestMenu.threeRows(id, playerInv, this.inventory),
                            Component.literal("Saviritium Golem")
                    ));
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            // B) Click Destro normale -> Digli di stare fermo o di seguirti
            else if (player.getItemInHand(hand).isEmpty()) {
                if (!this.level().isClientSide) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.navigation.stop(); // Ferma il movimento se gli dici di sedersi
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        return super.mobInteract(player, hand);
    }

    // 4. SALVATAGGIO DELL'INVENTARIO
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

    // 5. ANIMAZIONI GECKOLIB
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {

            // Se gli hai detto di stare fermo, fa un'animazione statica o si disattiva
            if (this.isOrderedToSit()) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.saviritium_golem.idle"));
                return PlayState.CONTINUE;
            }
            // Se si sta muovendo, fai l'animazione della ruota/camminata
            if (event.isMoving()) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.saviritium_golem.walk"));
                return PlayState.CONTINUE;
            }
            // Altrimenti animazione da fermo generica
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.saviritium_golem.idle"));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    // 6. DROP DEGLI OGGETTI ALLA MORTE
    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        // Se l'inventario esiste, lo svuotiamo a terra
        if (this.inventory != null) {
            for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                ItemStack itemStack = this.inventory.getItem(i);
                if (!itemStack.isEmpty()) {
                    // Evoca l'oggetto a terra nella posizione esatta del golem
                    this.spawnAtLocation(itemStack);
                }
            }
        }
    }
}