package com.refinedmods.refinedstorage.forge.grid.strategy;

import com.refinedmods.refinedstorage.api.network.node.grid.GridInsertMode;
import com.refinedmods.refinedstorage.api.network.node.grid.GridOperations;
import com.refinedmods.refinedstorage.common.api.grid.Grid;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridInsertionStrategy;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;
import com.refinedmods.refinedstorage.forge.storage.ItemHandlerStorage;
import com.refinedmods.refinedstorage.forge.support.inventory.CarriedSlotItemHandler;
import com.refinedmods.refinedstorage.forge.support.inventory.SingleSlotItemHandler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ItemGridInsertionStrategy implements GridInsertionStrategy {
    private final AbstractContainerMenu containerMenu;
    private final GridOperations gridOperations;

    public ItemGridInsertionStrategy(final AbstractContainerMenu containerMenu,
                                     final ServerPlayer player,
                                     final Grid grid) {
        this.containerMenu = containerMenu;
        this.gridOperations = grid.createOperations(ResourceTypes.ITEM, player);
    }

    @Override
    public boolean onInsert(final GridInsertMode insertMode, final boolean tryAlternatives) {
        final ItemStack carried = containerMenu.getCarried();
        if (carried.isEmpty()) {
            return false;
        }
        final ItemResource itemResource = ItemResource.ofItemStack(carried);
        gridOperations.insert(
            itemResource,
            insertMode,
            new ItemHandlerStorage(() -> new CarriedSlotItemHandler(containerMenu))
        );
        return true;
    }

    @Override
    public boolean onTransfer(final int slotIndex) {
        final Slot slot = containerMenu.getSlot(slotIndex);
        final ItemStack inSlot = slot.getItem();
        if (inSlot.isEmpty()) {
            return false;
        }
        gridOperations.insert(
            ItemResource.ofItemStack(inSlot),
            GridInsertMode.ENTIRE_RESOURCE,
            new ItemHandlerStorage(() -> new SingleSlotItemHandler(slot.container, slot.getContainerSlot()))
        );
        return true;
    }
}
