package com.refinedmods.refinedstorage.forge.support;

import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import com.refinedmods.refinedstorage.forge.api.RefinedStorageForgeApi;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jspecify.annotations.Nullable;

public final class CapabilityUtil {
    private CapabilityUtil() {
    }

    @Nullable
    public static NetworkNodeContainerProvider getContainerProvider(
        final Level level,
        final BlockPos pos,
        @Nullable final Direction direction
    ) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return null;
        }
        if (blockEntity instanceof AbstractNetworkNodeContainerBlockEntity<?> container) {
            return container.getContainerProvider();
        }
        return blockEntity.getCapability(
            RefinedStorageForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
            direction
        ).orElse(null);
    }

    @Nullable
    public static IItemHandler getItemHandler(final Level level, final BlockPos pos,
                                              @Nullable final Direction direction) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return null;
        }
        return blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction).orElse(null);
    }

    @Nullable
    public static IFluidHandler getFluidHandler(final Level level, final BlockPos pos,
                                                @Nullable final Direction direction) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return null;
        }
        return blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, direction).orElse(null);
    }

    @Nullable
    public static IEnergyStorage getEnergyHandler(final Level level, final BlockPos pos,
                                                  @Nullable final Direction direction) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return null;
        }
        return blockEntity.getCapability(ForgeCapabilities.ENERGY, direction).orElse(null);
    }

    @Nullable
    public static IFluidHandler getFluidHandler(final ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
    }

    public static boolean isValid(final ServerLevel level, final BlockPos pos) {
        return level.isLoaded(pos);
    }
}
