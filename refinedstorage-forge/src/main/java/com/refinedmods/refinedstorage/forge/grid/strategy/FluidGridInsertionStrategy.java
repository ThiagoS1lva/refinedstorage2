package com.refinedmods.refinedstorage.forge.grid.strategy;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.node.grid.GridInsertMode;
import com.refinedmods.refinedstorage.api.network.node.grid.GridOperations;
import com.refinedmods.refinedstorage.common.api.grid.Grid;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridInsertionStrategy;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;
import com.refinedmods.refinedstorage.forge.support.resource.VariantUtil;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FluidGridInsertionStrategy implements GridInsertionStrategy {
    private final AbstractContainerMenu menu;
    private final GridOperations gridOperations;

    public FluidGridInsertionStrategy(final AbstractContainerMenu menu, final ServerPlayer player, final Grid grid) {
        this.menu = menu;
        this.gridOperations = grid.createOperations(ResourceTypes.FLUID, player);
    }

    @Override
    public boolean onInsert(final GridInsertMode insertMode, final boolean tryAlternatives) {
        final ItemStack carried = menu.getCarried();
        if (carried.isEmpty()) {
            return false;
        }
        final IFluidHandlerItem probe = FluidUtil.getFluidHandler(carried.copy()).resolve().orElse(null);
        if (probe == null) {
            return false;
        }
        final FluidStack available = probe.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (available.isEmpty() || available.hasTag()) {
            return false;
        }
        final FluidResource fluidResource = VariantUtil.ofFluidStack(available);
        gridOperations.insert(fluidResource, insertMode, (resource, amount, action, actor) -> {
            if (!(resource instanceof FluidResource fluidResource2)) {
                return 0;
            }
            final boolean simulate = action == Action.SIMULATE;
            final ItemStack live = menu.getCarried();
            final IFluidHandlerItem handler = FluidUtil.getFluidHandler(simulate ? live.copy() : live)
                .resolve().orElse(null);
            if (handler == null) {
                return 0;
            }
            final FluidStack drained = handler.drain(
                VariantUtil.toFluidStack(fluidResource2, (int) Math.min(amount, Integer.MAX_VALUE)),
                simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE
            );
            if (!simulate && !drained.isEmpty()) {
                menu.setCarried(handler.getContainer());
            }
            return drained.getAmount();
        });
        return true;
    }

    @Override
    public boolean onTransfer(final int slotIndex) {
        throw new UnsupportedOperationException();
    }
}
