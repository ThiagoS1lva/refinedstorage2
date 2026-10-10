package com.refinedmods.refinedstorage.forge.support.energy;

import com.refinedmods.refinedstorage.common.api.support.energy.EnergyItemContext;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public class StackEnergyItemContext implements EnergyItemContext {
    private final ItemStack stack;

    public StackEnergyItemContext(final ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack copyStack() {
        return stack.copy();
    }

    @Override
    public void setStack(final ItemStack newStack) {
        stack.setCount(newStack.getCount());
        copyComponents(newStack.getComponents(), stack);
    }

    private static void copyComponents(final DataComponentMap from, final ItemStack to) {
        for (final DataComponentType<?> type : to.getComponents().keySet()) {
            if (!from.has(type)) {
                to.remove(type);
            }
        }
        for (final DataComponentType<?> type : from.keySet()) {
            copyComponent(from, to, type);
        }
    }

    private static <T> void copyComponent(final DataComponentMap from,
                                          final ItemStack to,
                                          final DataComponentType<T> type) {
        to.set(type, from.get(type));
    }
}
