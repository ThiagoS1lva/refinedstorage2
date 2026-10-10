package com.refinedmods.refinedstorage.forge.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.task.ExternalPatternSink;
import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.forge.api.ResourceHandlerExternalPatternSinkStrategy;
import com.refinedmods.refinedstorage.forge.storage.FluidHandlerStorage;
import com.refinedmods.refinedstorage.forge.support.CapabilityUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fluids.capability.IFluidHandler;

class FluidHandlerExternalPatternSinkStrategyImpl implements ResourceHandlerExternalPatternSinkStrategy {
    private final ServerLevel level;
    private final BlockPos pos;
    private final Direction direction;

    FluidHandlerExternalPatternSinkStrategyImpl(final ServerLevel level,
                                                final BlockPos pos,
                                                final Direction direction) {
        this.level = level;
        this.pos = pos;
        this.direction = direction;
    }

    @Override
    public ExternalPatternSink.Result insert(final ResourceAmount resourceAmount, final Action action) {
        if (!(resourceAmount.resource() instanceof FluidResource fluidResource)) {
            return ExternalPatternSink.Result.SKIPPED;
        }
        final IFluidHandler handler = resolve();
        if (handler == null) {
            return ExternalPatternSink.Result.SKIPPED;
        }
        final FluidHandlerStorage storage = new FluidHandlerStorage(() -> handler);
        if (storage.insert(fluidResource, resourceAmount.amount(), Action.SIMULATE, Actor.EMPTY)
            != resourceAmount.amount()) {
            return ExternalPatternSink.Result.REJECTED;
        }
        if (action == Action.SIMULATE) {
            return ExternalPatternSink.Result.ACCEPTED;
        }
        return storage.insert(fluidResource, resourceAmount.amount(), Action.EXECUTE, Actor.EMPTY)
            == resourceAmount.amount()
            ? ExternalPatternSink.Result.ACCEPTED
            : ExternalPatternSink.Result.REJECTED;
    }

    @Override
    public boolean isEmpty() {
        final IFluidHandler handler = resolve();
        if (handler == null) {
            return true;
        }
        for (int i = 0; i < handler.getTanks(); ++i) {
            if (!handler.getFluidInTank(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private IFluidHandler resolve() {
        return CapabilityUtil.getFluidHandler(level, pos, direction);
    }
}
