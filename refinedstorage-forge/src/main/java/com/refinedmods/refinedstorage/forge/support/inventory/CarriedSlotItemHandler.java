package com.refinedmods.refinedstorage.forge.support.inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class CarriedSlotItemHandler implements IItemHandlerModifiable {
    private final AbstractContainerMenu menu;

    public CarriedSlotItemHandler(final AbstractContainerMenu menu) {
        this.menu = menu;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(final int slot) {
        return menu.getCarried();
    }

    @Override
    public void setStackInSlot(final int slot, final ItemStack stack) {
        menu.setCarried(stack);
    }

    @Override
    public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final ItemStack carried = menu.getCarried();
        if (carried.isEmpty()) {
            if (!simulate) {
                menu.setCarried(stack.copy());
            }
            return ItemStack.EMPTY;
        }
        if (!ItemStack.isSameItemSameComponents(carried, stack)) {
            return stack;
        }
        final int space = Math.max(0, carried.getMaxStackSize() - carried.getCount());
        final int toMove = Math.min(space, stack.getCount());
        if (toMove <= 0) {
            return stack;
        }
        if (!simulate) {
            carried.grow(toMove);
        }
        final ItemStack remainder = stack.copy();
        remainder.shrink(toMove);
        return remainder;
    }

    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
        final ItemStack carried = menu.getCarried();
        if (carried.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final int toMove = Math.min(amount, carried.getCount());
        final ItemStack result = carried.copyWithCount(toMove);
        if (!simulate) {
            carried.shrink(toMove);
        }
        return result;
    }

    @Override
    public int getSlotLimit(final int slot) {
        final ItemStack carried = menu.getCarried();
        return carried.isEmpty() ? 64 : carried.getMaxStackSize();
    }

    @Override
    public boolean isItemValid(final int slot, final ItemStack stack) {
        return true;
    }
}
