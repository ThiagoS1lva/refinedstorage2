package com.refinedmods.refinedstorage.forge.storage.externalstorage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.ExtractableStorage;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;
import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;
import com.refinedmods.refinedstorage.forge.storage.FluidHandlerStorage;
import com.refinedmods.refinedstorage.forge.support.CapabilityUtil;
import com.refinedmods.refinedstorage.forge.support.resource.VariantUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

class FluidHandlerExternalStorageProvider implements ExternalStorageProvider {
    private final ServerLevel level;
    private final BlockPos pos;
    private final Direction direction;
    private final InsertableStorage insertTarget;
    private final ExtractableStorage extractTarget;

    FluidHandlerExternalStorageProvider(final ServerLevel level, final BlockPos pos, final Direction direction) {
        this.level = level;
        this.pos = pos;
        this.direction = direction;
        this.insertTarget = new FluidHandlerStorage(this::resolve);
        this.extractTarget = new FluidHandlerStorage(this::resolve);
    }

    @Override
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        return extractTarget.extract(resource, amount, action, actor);
    }

    @Override
    public long insert(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        return insertTarget.insert(resource, amount, action, actor);
    }

    @Override
    public Iterator<ResourceAmount> iterator() {
        final IFluidHandler handler = resolve();
        if (handler == null) {
            return Collections.emptyIterator();
        }
        final List<ResourceAmount> amounts = new ArrayList<>();
        for (int i = 0; i < handler.getTanks(); ++i) {
            final FluidStack stack = handler.getFluidInTank(i);
            if (!stack.isEmpty()) {
                amounts.add(new ResourceAmount(VariantUtil.ofFluidStack(stack), stack.getAmount()));
            }
        }
        return amounts.iterator();
    }

    private IFluidHandler resolve() {
        return CapabilityUtil.getFluidHandler(level, pos, direction);
    }
}
