package com.refinedmods.refinedstorage.forge.storage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.ExtractableStorage;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.forge.support.resource.VariantUtil;

import java.util.function.Supplier;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerStorage implements ExtractableStorage, InsertableStorage {
    private final Supplier<IItemHandler> handler;

    public ItemHandlerStorage(final Supplier<IItemHandler> handler) {
        this.handler = handler;
    }

    public long getAmount(final ResourceKey resource) {
        if (!(resource instanceof ItemResource itemResource)) {
            return 0L;
        }
        final ItemStack probe = VariantUtil.toItemStack(itemResource, 1);
        final IItemHandler resolved = handler.get();
        if (resolved == null) {
            return 0L;
        }
        long amount = 0L;
        for (int i = 0; i < resolved.getSlots(); ++i) {
            final ItemStack inSlot = resolved.getStackInSlot(i);
            if (!inSlot.isEmpty() && ItemStack.isSameItemSameComponents(inSlot, probe)) {
                amount += inSlot.getCount();
            }
        }
        return amount;
    }

    @Override
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (!(resource instanceof ItemResource itemResource)) {
            return 0L;
        }
        final IItemHandler resolved = handler.get();
        if (resolved == null) {
            return 0L;
        }
        final boolean simulate = action == Action.SIMULATE;
        final ItemStack probe = VariantUtil.toItemStack(itemResource, 1);
        long extracted = 0L;
        for (int i = 0; i < resolved.getSlots() && extracted < amount; ++i) {
            final ItemStack inSlot = resolved.getStackInSlot(i);
            if (inSlot.isEmpty() || !ItemStack.isSameItemSameComponents(inSlot, probe)) {
                continue;
            }
            final int toExtract = (int) Math.min(amount - extracted, Integer.MAX_VALUE);
            extracted += resolved.extractItem(i, toExtract, simulate).getCount();
        }
        return extracted;
    }

    @Override
    public long insert(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (!(resource instanceof ItemResource itemResource)) {
            return 0L;
        }
        final IItemHandler resolved = handler.get();
        if (resolved == null) {
            return 0L;
        }
        final boolean simulate = action == Action.SIMULATE;
        ItemStack toInsert = VariantUtil.toItemStack(itemResource, (int) Math.min(amount, Integer.MAX_VALUE));
        long inserted = 0L;
        for (int i = 0; i < resolved.getSlots() && !toInsert.isEmpty(); ++i) {
            final ItemStack remainder = resolved.insertItem(i, toInsert, simulate);
            inserted += toInsert.getCount() - remainder.getCount();
            toInsert = remainder;
        }
        return inserted;
    }
}
