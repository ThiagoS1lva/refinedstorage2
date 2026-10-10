package com.refinedmods.refinedstorage.forge.support.containermenu;

import com.refinedmods.refinedstorage.common.support.containermenu.TransferDestination;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public record ContainerTransferDestination(Container destination) implements TransferDestination {
    @Override
    public ItemStack transfer(final ItemStack stack) {
        final IItemHandler target = new InvWrapper(destination);
        ItemStack remainder = stack.copy();
        for (int i = 0; i < target.getSlots() && !remainder.isEmpty(); ++i) {
            remainder = target.insertItem(i, remainder, false);
        }
        return remainder;
    }
}
