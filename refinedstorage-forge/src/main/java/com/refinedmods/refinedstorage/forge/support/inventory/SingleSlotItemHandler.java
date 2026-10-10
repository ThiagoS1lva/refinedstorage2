package com.refinedmods.refinedstorage.forge.support.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class SingleSlotItemHandler implements IItemHandlerModifiable {
    private final Container container;
    private final int index;

    public SingleSlotItemHandler(final Container container, final int index) {
        this.container = container;
        this.index = index;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(final int slot) {
        return container.getItem(index);
    }

    @Override
    public void setStackInSlot(final int slot, final ItemStack stack) {
        container.setItem(index, stack);
    }

    @Override
    public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
        if (stack.isEmpty() || !container.canPlaceItem(index, stack)) {
            return stack;
        }
        final ItemStack inSlot = container.getItem(index);
        if (inSlot.isEmpty()) {
            final int toMove = Math.min(stack.getCount(), getSlotLimit(slot));
            if (!simulate) {
                container.setItem(index, stack.copyWithCount(toMove));
            }
            final ItemStack remainder = stack.copy();
            remainder.shrink(toMove);
            return remainder;
        }
        if (!ItemStack.isSameItemSameComponents(inSlot, stack)) {
            return stack;
        }
        final int space = Math.max(0, Math.min(inSlot.getMaxStackSize(), container.getMaxStackSize()) - inSlot.getCount());
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
        final ItemStack inSlot = container.getItem(index);
        if (inSlot.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final int toMove = Math.min(amount, inSlot.getCount());
        if (simulate) {
            return inSlot.copyWithCount(toMove);
        }
        return container.removeItem(index, toMove);
    }

    @Override
    public int getSlotLimit(final int slot) {
        return container.getMaxStackSize();
    }

    @Override
    public boolean isItemValid(final int slot, final ItemStack stack) {
        return container.canPlaceItem(index, stack);
    }
}
