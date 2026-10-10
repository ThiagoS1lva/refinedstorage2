package com.refinedmods.refinedstorage.forge.grid.strategy;

import com.refinedmods.refinedstorage.api.network.node.grid.GridExtractMode;
import com.refinedmods.refinedstorage.api.network.node.grid.GridInsertMode;
import com.refinedmods.refinedstorage.api.network.node.grid.GridOperations;
import com.refinedmods.refinedstorage.api.storage.ExtractableStorage;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;
import com.refinedmods.refinedstorage.common.api.grid.Grid;
import com.refinedmods.refinedstorage.common.api.grid.GridScrollMode;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridScrollingStrategy;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;
import com.refinedmods.refinedstorage.forge.storage.ItemHandlerStorage;
import com.refinedmods.refinedstorage.forge.support.inventory.CarriedSlotItemHandler;
import com.refinedmods.refinedstorage.forge.support.inventory.SingleSlotItemHandler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.items.wrapper.InvWrapper;

public class ItemGridScrollingStrategy implements GridScrollingStrategy {
    private final AbstractContainerMenu containerMenu;
    private final Inventory playerInventory;
    private final GridOperations gridOperations;

    public ItemGridScrollingStrategy(final AbstractContainerMenu containerMenu,
                                     final ServerPlayer player,
                                     final Grid grid) {
        this.containerMenu = containerMenu;
        this.playerInventory = player.getInventory();
        this.gridOperations = grid.createOperations(ResourceTypes.ITEM, player);
    }

    @Override
    public boolean onScroll(final PlatformResourceKey resource, final GridScrollMode scrollMode, final int slotIndex) {
        if (resource instanceof ItemResource itemResource) {
            switch (scrollMode) {
                case GRID_TO_INVENTORY -> handleGridToInventoryScroll(
                    itemResource,
                    slotIndex >= 0
                        ? new ItemHandlerStorage(() -> new SingleSlotItemHandler(playerInventory, slotIndex))
                        : new ItemHandlerStorage(() -> new InvWrapper(playerInventory))
                );
                case INVENTORY_TO_GRID -> handleInventoryToGridScroll(
                    itemResource,
                    slotIndex >= 0
                        ? new ItemHandlerStorage(() -> new SingleSlotItemHandler(playerInventory, slotIndex))
                        : new ItemHandlerStorage(() -> new InvWrapper(playerInventory))
                );
                case GRID_TO_CURSOR -> handleGridToInventoryScroll(
                    itemResource,
                    new ItemHandlerStorage(() -> new CarriedSlotItemHandler(containerMenu))
                );
            }
            return true;
        }
        return false;
    }

    private void handleInventoryToGridScroll(final ItemResource itemResource,
                                             final ExtractableStorage extractableStorage) {
        gridOperations.insert(itemResource, GridInsertMode.SINGLE_RESOURCE, extractableStorage);
    }

    private void handleGridToInventoryScroll(final ItemResource itemResource,
                                             final InsertableStorage insertableStorage) {
        gridOperations.extract(itemResource, GridExtractMode.SINGLE_RESOURCE, insertableStorage);
    }
}
