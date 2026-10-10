package com.refinedmods.refinedstorage.forge.support.resource;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class ResourceContainerFluidHandler implements IFluidHandler {
    private final ResourceContainer container;

    public ResourceContainerFluidHandler(final ResourceContainer container) {
        this.container = container;
    }

    @Override
    public int getTanks() {
        return container.size();
    }

    @Override
    public FluidStack getFluidInTank(final int tank) {
        final ResourceAmount content = container.get(tank);
        if (content == null || !(content.resource() instanceof FluidResource fluidResource)) {
            return FluidStack.EMPTY;
        }
        return VariantUtil.toFluidStack(fluidResource, saturated(content.amount()));
    }

    @Override
    public int getTankCapacity(final int tank) {
        final ResourceAmount content = container.get(tank);
        if (content == null || !(content.resource() instanceof FluidResource fluidResource)) {
            return Integer.MAX_VALUE;
        }
        return saturated(container.getMaxAmount(fluidResource));
    }

    @Override
    public boolean isFluidValid(final int tank, final FluidStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return container.isValid(VariantUtil.ofFluidStack(stack));
    }

    @Override
    public int fill(final FluidStack resource, final FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }
        final FluidResource fluidResource = VariantUtil.ofFluidStack(resource);
        if (!container.isValid(fluidResource)) {
            return 0;
        }
        int remaining = resource.getAmount();
        final List<TankFill> plan = new ArrayList<>();
        for (int i = 0; i < container.size() && remaining > 0; ++i) {
            final ResourceAmount content = container.get(i);
            if (content != null && content.resource().equals(fluidResource)) {
                final int space = saturated(container.getMaxAmount(fluidResource) - content.amount());
                final int taking = Math.min(space, remaining);
                if (taking > 0) {
                    plan.add(new TankFill(i, taking));
                    remaining -= taking;
                }
            }
        }
        for (int i = 0; i < container.size() && remaining > 0; ++i) {
            if (container.get(i) == null) {
                final int taking = Math.min(saturated(container.getMaxAmount(fluidResource)), remaining);
                if (taking > 0) {
                    plan.add(new TankFill(i, taking));
                    remaining -= taking;
                }
            }
        }
        final int filled = resource.getAmount() - remaining;
        if (action == FluidAction.EXECUTE) {
            for (final TankFill fill : plan) {
                final ResourceAmount content = container.get(fill.tank());
                if (content == null) {
                    container.set(fill.tank(), new ResourceAmount(fluidResource, fill.amount()));
                } else {
                    container.grow(fill.tank(), fill.amount());
                }
            }
        }
        return filled;
    }

    @Override
    public FluidStack drain(final FluidStack resource, final FluidAction action) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }
        final FluidResource fluidResource = VariantUtil.ofFluidStack(resource);
        int remaining = resource.getAmount();
        final List<TankFill> plan = new ArrayList<>();
        for (int i = 0; i < container.size() && remaining > 0; ++i) {
            final ResourceAmount content = container.get(i);
            if (content != null && content.resource().equals(fluidResource)) {
                final int taking = (int) Math.min(content.amount(), remaining);
                if (taking > 0) {
                    plan.add(new TankFill(i, taking));
                    remaining -= taking;
                }
            }
        }
        final int drained = resource.getAmount() - remaining;
        if (drained <= 0) {
            return FluidStack.EMPTY;
        }
        if (action == FluidAction.EXECUTE) {
            for (final TankFill fill : plan) {
                container.shrink(fill.tank(), fill.amount());
            }
        }
        return VariantUtil.toFluidStack(fluidResource, drained);
    }

    @Override
    public FluidStack drain(final int maxDrain, final FluidAction action) {
        for (int i = 0; i < container.size(); ++i) {
            final ResourceAmount content = container.get(i);
            if (content != null && content.resource() instanceof FluidResource fluidResource) {
                return drain(VariantUtil.toFluidStack(fluidResource, maxDrain), action);
            }
        }
        return FluidStack.EMPTY;
    }

    private static int saturated(final long amount) {
        return (int) Math.min(amount, Integer.MAX_VALUE);
    }

    private record TankFill(int tank, int amount) {
    }
}
