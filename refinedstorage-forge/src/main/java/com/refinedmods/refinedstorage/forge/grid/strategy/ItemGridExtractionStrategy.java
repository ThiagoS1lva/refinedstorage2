package com.refinedmods.refinedstorage.forge.grid.strategy;

import com.refinedmods.refinedstorage.api.network.node.grid.GridExtractMode;
import com.refinedmods.refinedstorage.api.network.node.grid.GridOperations;
import com.refinedmods.refinedstorage.common.api.grid.Grid;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridExtractionStrategy;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;
import com.refinedmods.refinedstorage.forge.storage.ItemHandlerStorage;
import com.refinedmods.refinedstorage.forge.support.inventory.CarriedSlotItemHandler;

import java.util.function.Supplier;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class ItemGridExtractionStrategy implements GridExtractionStrategy {
    private final GridOperations gridOperations;
    private final Supplier<IItemHandler> playerInventory;
    private final Supplier<IItemHandler> playerCursor;

    public ItemGridExtractionStrategy(final AbstractContainerMenu containerMenu,
                                      final ServerPlayer player,
                                      final Grid grid) {
        this.gridOperations = grid.createOperations(ResourceTypes.ITEM, player);
        final Inventory inventory = player.getInventory();
        this.playerInventory = () -> new InvWrapper(inventory);
        this.playerCursor = () -> new CarriedSlotItemHandler(containerMenu);
    }

    @Override
    public boolean onExtract(final PlatformResourceKey resource,
                             final GridExtractMode extractMode,
                             final boolean cursor) {
        if (resource instanceof ItemResource itemResource) {
            gridOperations.extract(
                itemResource,
                extractMode,
                new ItemHandlerStorage(cursor ? playerCursor : playerInventory)
            );
            return true;
        }
        return false;
    }
}
