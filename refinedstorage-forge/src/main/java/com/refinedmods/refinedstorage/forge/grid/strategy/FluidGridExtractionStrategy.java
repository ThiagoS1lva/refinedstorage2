package com.refinedmods.refinedstorage.forge.grid.strategy;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.node.grid.GridExtractMode;
import com.refinedmods.refinedstorage.api.network.node.grid.GridOperations;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.Storage;
import com.refinedmods.refinedstorage.common.api.grid.Grid;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridExtractionStrategy;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;
import com.refinedmods.refinedstorage.forge.storage.ItemHandlerStorage;
import com.refinedmods.refinedstorage.forge.support.resource.VariantUtil;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jspecify.annotations.Nullable;

public class FluidGridExtractionStrategy implements GridExtractionStrategy {
    private static final ItemResource BUCKET_ITEM_RESOURCE = new ItemResource(Items.BUCKET);

    private final AbstractContainerMenu menu;
    private final Inventory playerInventory;
    private final GridOperations gridOperations;
    private final Storage itemStorage;

    public FluidGridExtractionStrategy(final AbstractContainerMenu containerMenu, final ServerPlayer player,
                                       final Grid grid) {
        this.menu = containerMenu;
        this.playerInventory = player.getInventory();
        this.gridOperations = grid.createOperations(ResourceTypes.FLUID, player);
        this.itemStorage = grid.getItemStorage();
    }

    @Override
    public boolean onExtract(final PlatformResourceKey resource,
                             final GridExtractMode extractMode,
                             final boolean cursor) {
        if (resource instanceof FluidResource fluidResource) {
            final boolean containerOnCursor = isFluidContainerOnCursor();
            final boolean bucketInInventory = hasBucketInInventory();
            final boolean bucketInStorage = hasBucketInStorage();
            if (containerOnCursor) {
                extractWithContainerOnCursor(fluidResource, extractMode);
            } else if (bucketInInventory) {
                extractWithBucketInInventory(fluidResource, extractMode, cursor);
            } else if (bucketInStorage) {
                extractWithBucketInStorage(fluidResource, extractMode, cursor);
            }
            return true;
        }
        return false;
    }

    private void extractWithContainerOnCursor(final FluidResource fluidResource,
                                              final GridExtractMode mode) {
        gridOperations.extract(fluidResource, mode, (resource2, amount, action, actor) -> {
            if (!(resource2 instanceof FluidResource fluidResource2)) {
                return 0;
            }
            final boolean simulate = action == Action.SIMULATE;
            final ItemStack carried = menu.getCarried();
            if (carried.isEmpty()) {
                return 0;
            }
            final ItemStack container = carried.copyWithCount(1);
            final IFluidHandlerItem filler = FluidUtil.getFluidHandler(container).resolve().orElse(null);
            if (filler == null) {
                return 0;
            }
            final int toFill = (int) Math.min(amount, Integer.MAX_VALUE);
            final int fillable = filler.fill(
                VariantUtil.toFluidStack(fluidResource2, toFill),
                IFluidHandler.FluidAction.SIMULATE
            );
            if (fillable <= 0) {
                return 0;
            }
            if (simulate) {
                return fillable;
            }
            filler.fill(
                VariantUtil.toFluidStack(fluidResource2, toFill),
                IFluidHandler.FluidAction.EXECUTE
            );
            final ItemStack result = filler.getContainer();
            carried.shrink(1);
            if (!placeResult(result, true, false)) {
                carried.grow(1);
                return 0;
            }
            return fillable;
        });
    }

    private void extractWithBucketInStorage(final FluidResource fluidResource,
                                            final GridExtractMode mode,
                                            final boolean cursor) {
        gridOperations.extract(fluidResource, mode, (resource, amount, action, actor) -> {
            if (!(resource instanceof FluidResource fluidResource2)) {
                return 0;
            }
            final boolean simulate = action == Action.SIMULATE;
            final FillResult fill = fillBucket(VariantUtil.toFluidStack(
                fluidResource2,
                (int) Math.min(amount, Integer.MAX_VALUE)
            ));
            if (fill == null) {
                return 0;
            }
            if (simulate) {
                return placeResult(fill.result(), cursor, true) ? fill.filled() : 0;
            }
            if (itemStorage.extract(BUCKET_ITEM_RESOURCE, 1, Action.EXECUTE, actor) != 1) {
                return 0;
            }
            if (!placeResult(fill.result(), cursor, false)) {
                itemStorage.insert(BUCKET_ITEM_RESOURCE, 1, Action.EXECUTE, actor);
                return 0;
            }
            return fill.filled();
        });
    }

    private void extractWithBucketInInventory(final FluidResource fluidResource,
                                              final GridExtractMode mode,
                                              final boolean cursor) {
        gridOperations.extract(fluidResource, mode, (resource, amount, action, actor) -> {
            if (!(resource instanceof FluidResource fluidResource2)) {
                return 0;
            }
            final boolean simulate = action == Action.SIMULATE;
            final FillResult fill = fillBucket(VariantUtil.toFluidStack(
                fluidResource2,
                (int) Math.min(amount, Integer.MAX_VALUE)
            ));
            if (fill == null) {
                return 0;
            }
            final ItemHandlerStorage inventory = new ItemHandlerStorage(() -> new InvWrapper(playerInventory));
            if (simulate) {
                return placeResult(fill.result(), cursor, true) ? fill.filled() : 0;
            }
            if (inventory.extract(BUCKET_ITEM_RESOURCE, 1, Action.EXECUTE, actor) != 1) {
                return 0;
            }
            if (!placeResult(fill.result(), cursor, false)) {
                inventory.insert(BUCKET_ITEM_RESOURCE, 1, Action.EXECUTE, actor);
                return 0;
            }
            return fill.filled();
        });
    }

    @Nullable
    private static FillResult fillBucket(final FluidStack fluid) {
        final IFluidHandlerItem filler = FluidUtil.getFluidHandler(new ItemStack(Items.BUCKET))
            .resolve().orElse(null);
        if (filler == null) {
            return null;
        }
        final int fillable = filler.fill(fluid, IFluidHandler.FluidAction.SIMULATE);
        if (fillable <= 0) {
            return null;
        }
        filler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
        return new FillResult(filler.getContainer(), fillable);
    }

    private boolean placeResult(final ItemStack result, final boolean toCursor, final boolean simulate) {
        if (toCursor) {
            final ItemStack carried = menu.getCarried();
            if (carried.isEmpty()) {
                if (!simulate) {
                    menu.setCarried(result.copy());
                }
                return true;
            }
            if (ItemStack.isSameItemSameComponents(carried, result)
                && carried.getCount() + result.getCount() <= carried.getMaxStackSize()) {
                if (!simulate) {
                    carried.grow(result.getCount());
                }
                return true;
            }
        }
        final ItemHandlerStorage inventory = new ItemHandlerStorage(() -> new InvWrapper(playerInventory));
        final ItemResource resultResource = ItemResource.ofItemStack(result);
        if (simulate) {
            final ItemStack probe = result.copy();
            final long simulated = inventory.insert(resultResource, probe.getCount(), Action.SIMULATE, Actor.EMPTY);
            return simulated == probe.getCount();
        }
        return inventory.insert(resultResource, result.getCount(), Action.EXECUTE, Actor.EMPTY)
            == result.getCount();
    }

    private boolean isFluidContainerOnCursor() {
        final ItemStack carried = menu.getCarried();
        if (carried.isEmpty()) {
            return false;
        }
        return FluidUtil.getFluidHandler(carried.copy()).resolve().isPresent();
    }

    private boolean hasBucketInInventory() {
        return new ItemHandlerStorage(() -> new InvWrapper(playerInventory))
            .getAmount(BUCKET_ITEM_RESOURCE) >= 1;
    }

    private boolean hasBucketInStorage() {
        return itemStorage.extract(BUCKET_ITEM_RESOURCE, 1, Action.SIMULATE, Actor.EMPTY) == 1;
    }

    private record FillResult(ItemStack result, int filled) {
    }
}
