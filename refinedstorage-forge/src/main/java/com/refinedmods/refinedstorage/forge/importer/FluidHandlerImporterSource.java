package com.refinedmods.refinedstorage.forge.importer;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.impl.node.importer.ImporterSource;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
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

class FluidHandlerImporterSource implements ImporterSource {
    private final ServerLevel level;
    private final BlockPos pos;
    private final Direction direction;

    FluidHandlerImporterSource(final ServerLevel level, final BlockPos pos, final Direction direction) {
        this.level = level;
        this.pos = pos;
        this.direction = direction;
    }

    public long getAmount(final ResourceKey resource) {
        if (!(resource instanceof FluidResource)) {
            return 0L;
        }
        return storage().getAmount(resource);
    }

    @Override
    public Iterator<ResourceKey> getResources() {
        final IFluidHandler handler = resolve();
        if (handler == null) {
            return Collections.emptyIterator();
        }
        final List<ResourceKey> resources = new ArrayList<>();
        for (int i = 0; i < handler.getTanks(); ++i) {
            final FluidStack stack = handler.getFluidInTank(i);
            if (!stack.isEmpty()) {
                resources.add(VariantUtil.ofFluidStack(stack));
            }
        }
        return resources.iterator();
    }

    @Override
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        return storage().extract(resource, amount, action, actor);
    }

    @Override
    public long insert(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        return storage().insert(resource, amount, action, actor);
    }

    private FluidHandlerStorage storage() {
        return new FluidHandlerStorage(this::resolve);
    }

    private IFluidHandler resolve() {
        return CapabilityUtil.getFluidHandler(level, pos, direction);
    }
}
