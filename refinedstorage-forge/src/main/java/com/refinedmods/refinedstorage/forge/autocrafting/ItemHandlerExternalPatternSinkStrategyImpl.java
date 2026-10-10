package com.refinedmods.refinedstorage.forge.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.task.ExternalPatternSink;
import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.forge.api.ResourceHandlerExternalPatternSinkStrategy;
import com.refinedmods.refinedstorage.forge.storage.ItemHandlerStorage;
import com.refinedmods.refinedstorage.forge.support.CapabilityUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

class ItemHandlerExternalPatternSinkStrategyImpl implements ResourceHandlerExternalPatternSinkStrategy {
    private final ServerLevel level;
    private final BlockPos pos;
    private final Direction direction;

    ItemHandlerExternalPatternSinkStrategyImpl(final ServerLevel level,
                                               final BlockPos pos,
                                               final Direction direction) {
        this.level = level;
        this.pos = pos;
        this.direction = direction;
    }

    @Override
    public ExternalPatternSink.Result insert(final ResourceAmount resourceAmount, final Action action) {
        if (!(resourceAmount.resource() instanceof ItemResource itemResource)) {
            return ExternalPatternSink.Result.SKIPPED;
        }
        final IItemHandler handler = resolve();
        if (handler == null) {
            return ExternalPatternSink.Result.SKIPPED;
        }
        final ItemHandlerStorage storage = new ItemHandlerStorage(() -> handler);
        if (storage.insert(itemResource, resourceAmount.amount(), Action.SIMULATE, Actor.EMPTY)
            != resourceAmount.amount()) {
            return ExternalPatternSink.Result.REJECTED;
        }
        if (action == Action.SIMULATE) {
            return ExternalPatternSink.Result.ACCEPTED;
        }
        return storage.insert(itemResource, resourceAmount.amount(), Action.EXECUTE, Actor.EMPTY)
            == resourceAmount.amount()
            ? ExternalPatternSink.Result.ACCEPTED
            : ExternalPatternSink.Result.REJECTED;
    }

    @Override
    public boolean isEmpty() {
        final IItemHandler handler = resolve();
        if (handler == null) {
            return true;
        }
        for (int i = 0; i < handler.getSlots(); ++i) {
            final ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private IItemHandler resolve() {
        return CapabilityUtil.getItemHandler(level, pos, direction);
    }
}
