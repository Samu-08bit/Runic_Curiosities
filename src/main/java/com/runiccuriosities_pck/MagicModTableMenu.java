package com.runiccuriosities_pck;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MagicModTableMenu extends AbstractContainerMenu {
    public static final String UPGRADES_TAG = "GolemUpgrades";

    private final Container container;
    private boolean isSyncing = false;
    private ItemStack lastEgg = ItemStack.EMPTY;

    public MagicModTableMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, new SimpleContainer(5));
    }

    public MagicModTableMenu(int containerId, Inventory playerInventory, Container container) {
        super(ModMenuTypes.MAGIC_MOD_TABLE_MENU.get(), containerId);
        checkContainerSize(container, 5);
        this.container = container;
        container.startOpen(playerInventory.player);

        this.addSlot(new Slot(container, 0, 36, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.SAVIRITIUM_GOLEM_SPAWN_EGG.get());
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                onEggTaken(stack);
                super.onTake(player, stack);
            }
        });

        this.addSlot(new Slot(container, 1, 112, 24) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isEggPresent();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        this.addSlot(new Slot(container, 2, 134, 24) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isEggPresent();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        this.addSlot(new Slot(container, 3, 112, 46) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isEggPresent();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        this.addSlot(new Slot(container, 4, 134, 46) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isEggPresent();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        ItemStack initialEgg = this.container.getItem(0);
        if (initialEgg.is(ModItems.SAVIRITIUM_GOLEM_SPAWN_EGG.get())) {
            this.lastEgg = initialEgg;
            this.syncUpgrades();
        }
    }

    private boolean isEggPresent() {
        ItemStack egg = this.container.getItem(0);
        return !egg.isEmpty() && egg.is(ModItems.SAVIRITIUM_GOLEM_SPAWN_EGG.get());
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        this.syncUpgrades();
    }

    private void syncUpgrades() {
        if (this.isSyncing) return;
        this.isSyncing = true;
        try {
            ItemStack egg = this.container.getItem(0);
            if (egg.is(ModItems.SAVIRITIUM_GOLEM_SPAWN_EGG.get())) {
                if (this.lastEgg.isEmpty()) {
                    NonNullList<ItemStack> saved = getUpgradesFromEgg(egg);
                    for (int i = 0; i < 4; i++) {
                        this.container.setItem(i + 1, saved.get(i).copy());
                    }
                    this.lastEgg = egg;
                } else {
                    NonNullList<ItemStack> current = NonNullList.withSize(4, ItemStack.EMPTY);
                    for (int i = 0; i < 4; i++) {
                        current.set(i, this.container.getItem(i + 1).copy());
                    }
                    setUpgradesToEgg(egg, current);
                    this.container.setChanged();
                }
            } else {
                this.lastEgg = ItemStack.EMPTY;
            }
        } finally {
            this.isSyncing = false;
        }
    }

    private void onEggTaken(ItemStack takenEgg) {
        NonNullList<ItemStack> current = NonNullList.withSize(4, ItemStack.EMPTY);
        for (int i = 0; i < 4; i++) {
            current.set(i, this.container.getItem(i + 1).copy());
        }
        setUpgradesToEgg(takenEgg, current);

        this.isSyncing = true;
        try {
            for (int i = 1; i <= 4; i++) {
                this.container.setItem(i, ItemStack.EMPTY);
            }
            this.lastEgg = ItemStack.EMPTY;
        } finally {
            this.isSyncing = false;
        }
    }

    public static NonNullList<ItemStack> getUpgradesFromEgg(ItemStack eggStack) {
        NonNullList<ItemStack> upgrades = NonNullList.withSize(4, ItemStack.EMPTY);
        if (!eggStack.isEmpty() && eggStack.hasTag()) {
            CompoundTag tag = eggStack.getTag();
            if (tag.contains(UPGRADES_TAG, Tag.TAG_COMPOUND)) {
                ContainerHelper.loadAllItems(tag.getCompound(UPGRADES_TAG), upgrades);
            }
        }
        return upgrades;
    }

    public static void setUpgradesToEgg(ItemStack eggStack, NonNullList<ItemStack> upgrades) {
        if (eggStack.isEmpty()) return;
        boolean hasAny = false;
        for (ItemStack s : upgrades) {
            if (!s.isEmpty()) {
                hasAny = true;
                break;
            }
        }
        CompoundTag tag = eggStack.getOrCreateTag();
        if (hasAny) {
            CompoundTag upgradesTag = new CompoundTag();
            ContainerHelper.saveAllItems(upgradesTag, upgrades);
            tag.put(UPGRADES_TAG, upgradesTag);
        } else {
            tag.remove(UPGRADES_TAG);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index == 0) {
                if (!this.moveItemStackTo(stackInSlot, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemstack);
            } else if (index >= 1 && index <= 4) {
                if (!this.moveItemStackTo(stackInSlot, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stackInSlot.is(ModItems.SAVIRITIUM_GOLEM_SPAWN_EGG.get())) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (isEggPresent()) {
                    if (!this.moveItemStackTo(stackInSlot, 1, 5, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
            this.syncUpgrades();
        }
        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}