package com.refinedmods.refinedstorage.forge.support.resource;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

public final class VariantUtil {
    private VariantUtil() {
    }

    public static ItemStack toItemStack(final ItemResource resource, final int amount) {
        final ItemStack stack = new ItemStack(resource.item(), amount);
        stack.applyComponents(resource.components());
        return stack;
    }

    public static FluidResource ofFluidStack(final FluidStack stack) {
        return new FluidResource(stack.getFluid());
    }

    public static FluidStack toFluidStack(final FluidResource resource, final int amount) {
        return new FluidStack(resource.fluid(), amount);
    }

    public static @Nullable ItemStack optionalItemToStack(final ResourceKey resource, final int amount) {
        if (!(resource instanceof ItemResource itemResource)) {
            return null;
        }
        return toItemStack(itemResource, amount);
    }

    public static @Nullable FluidStack optionalFluidToStack(final ResourceKey resource, final int amount) {
        if (!(resource instanceof FluidResource fluidResource)) {
            return null;
        }
        return toFluidStack(fluidResource, amount);
    }
}
