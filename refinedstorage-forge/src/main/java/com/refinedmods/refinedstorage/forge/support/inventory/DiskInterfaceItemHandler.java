package com.refinedmods.refinedstorage.forge.support.inventory;

import com.refinedmods.refinedstorage.common.storage.diskinterface.AbstractDiskInterfaceBlockEntity;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class DiskInterfaceItemHandler implements IItemHandler {
    private static final int INPUT_START = 0;
    private static final int INPUT_END = AbstractDiskInterfaceBlockEntity.AMOUNT_OF_DISKS / 2;
    private static final int OUTPUT_START = INPUT_END;
    private static final int OUTPUT_END = AbstractDiskInterfaceBlockEntity.AMOUNT_OF_DISKS;

    private final Container inventory;

    public DiskInterfaceItemHandler(final Container inventory) {
        this.inventory = inventory;
    }

    @Override
    public int getSlots() {
        return inventory.getContainerSize();
    }

    @Override
    public ItemStack getStackInSlot(final int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
        if (stack.isEmpty() || slot < INPUT_START || slot >= INPUT_END
            || !inventory.canPlaceItem(slot, stack)) {
            return stack;
        }
        final ItemStack inSlot = inventory.getItem(slot);
        if (inSlot.isEmpty()) {
            final int toMove = Math.min(stack.getCount(), getSlotLimit(slot));
            if (!simulate) {
                inventory.setItem(slot, stack.copyWithCount(toMove));
            }
            final ItemStack remainder = stack.copy();
            remainder.shrink(toMove);
            return remainder;
        }
        if (!ItemStack.isSameItemSameComponents(inSlot, stack)) {
            return stack;
        }
        final int space = Math.max(0, Math.min(inSlot.getMaxStackSize(), getSlotLimit(slot)) - inSlot.getCount());
        final int toMove = Math.min(space, stack.getCount());
        if (toMove <= 0) {
            return stack;
        }
        if (!simulate) {
            inSlot.grow(toMove);
        }
        final ItemStack remainder = stack.copy();
        remainder.shrink(toMove);
        return remainder;
    }

    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
        if (slot < OUTPUT_START || slot >= OUTPUT_END) {
            return ItemStack.EMPTY;
        }
        final ItemStack inSlot = inventory.getItem(slot);
        if (inSlot.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final int toMove = Math.min(amount, inSlot.getCount());
        if (simulate) {
            return inSlot.copyWithCount(toMove);
        }
        return inventory.removeItem(slot, toMove);
    }

    @Override
    public int getSlotLimit(final int slot) {
        return inventory.getMaxStackSize();
    }

    @Override
    public boolean isItemValid(final int slot, final ItemStack stack) {
        return slot >= INPUT_START && slot < INPUT_END && inventory.canPlaceItem(slot, stack);
    }
}
