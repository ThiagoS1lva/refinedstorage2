package com.refinedmods.refinedstorage.forge.storage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.ExtractableStorage;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.forge.support.resource.VariantUtil;

import java.util.function.Supplier;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidHandlerStorage implements ExtractableStorage, InsertableStorage {
    private final Supplier<IFluidHandler> handler;

    public FluidHandlerStorage(final Supplier<IFluidHandler> handler) {
        this.handler = handler;
    }

    public long getAmount(final ResourceKey resource) {
        if (!(resource instanceof FluidResource fluidResource)) {
            return 0L;
        }
        final IFluidHandler resolved = handler.get();
        if (resolved == null) {
            return 0L;
        }
        return resolved.drain(
            VariantUtil.toFluidStack(fluidResource, Integer.MAX_VALUE),
            IFluidHandler.FluidAction.SIMULATE
        ).getAmount();
    }

    @Override
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (!(resource instanceof FluidResource fluidResource)) {
            return 0L;
        }
        final IFluidHandler resolved = handler.get();
        if (resolved == null) {
            return 0L;
        }
        return resolved.drain(
            VariantUtil.toFluidStack(fluidResource, (int) Math.min(amount, Integer.MAX_VALUE)),
            action == Action.SIMULATE ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE
        ).getAmount();
    }

    @Override
    public long insert(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (!(resource instanceof FluidResource fluidResource)) {
            return 0L;
        }
        final IFluidHandler resolved = handler.get();
        if (resolved == null) {
            return 0L;
        }
        return resolved.fill(
            VariantUtil.toFluidStack(fluidResource, (int) Math.min(amount, Integer.MAX_VALUE)),
            action == Action.SIMULATE ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE
        );
    }
}
