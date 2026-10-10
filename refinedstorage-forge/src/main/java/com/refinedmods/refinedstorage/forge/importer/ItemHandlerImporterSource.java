package com.refinedmods.refinedstorage.forge.importer;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.impl.node.importer.ImporterSource;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.forge.storage.ItemHandlerStorage;
import com.refinedmods.refinedstorage.forge.support.CapabilityUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

class ItemHandlerImporterSource implements ImporterSource {
    private final ServerLevel level;
    private final BlockPos pos;
    private final Direction direction;

    ItemHandlerImporterSource(final ServerLevel level, final BlockPos pos, final Direction direction) {
        this.level = level;
        this.pos = pos;
        this.direction = direction;
    }

    public long getAmount(final ResourceKey resource) {
        if (!(resource instanceof ItemResource)) {
            return 0L;
        }
        return storage().getAmount(resource);
    }

    @Override
    public Iterator<ResourceKey> getResources() {
        final IItemHandler handler = resolve();
        if (handler == null) {
            return Collections.emptyIterator();
        }
        final List<ResourceKey> resources = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); ++i) {
            final ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                resources.add(ItemResource.ofItemStack(stack));
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

    private ItemHandlerStorage storage() {
        return new ItemHandlerStorage(this::resolve);
    }

    private IItemHandler resolve() {
        return CapabilityUtil.getItemHandler(level, pos, direction);
    }
}
