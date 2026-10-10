package com.refinedmods.refinedstorage.forge.storage.externalstorage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.ExtractableStorage;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;
import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;
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

class ItemHandlerExternalStorageProvider implements ExternalStorageProvider {
    private final ServerLevel level;
    private final BlockPos pos;
    private final Direction direction;
    private final InsertableStorage insertTarget;
    private final ExtractableStorage extractTarget;

    ItemHandlerExternalStorageProvider(final ServerLevel level, final BlockPos pos, final Direction direction) {
        this.level = level;
        this.pos = pos;
        this.direction = direction;
        this.insertTarget = new ItemHandlerStorage(this::resolve);
        this.extractTarget = new ItemHandlerStorage(this::resolve);
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
        final IItemHandler handler = resolve();
        if (handler == null) {
            return Collections.emptyIterator();
        }
        final List<ResourceAmount> amounts = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); ++i) {
            final ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                amounts.add(new ResourceAmount(ItemResource.ofItemStack(stack), stack.getCount()));
            }
        }
        return amounts.iterator();
    }

    private IItemHandler resolve() {
        return CapabilityUtil.getItemHandler(level, pos, direction);
    }
}
