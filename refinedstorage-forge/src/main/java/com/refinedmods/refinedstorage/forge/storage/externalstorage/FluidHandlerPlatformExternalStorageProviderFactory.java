package com.refinedmods.refinedstorage.forge.storage.externalstorage;

import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;
import com.refinedmods.refinedstorage.common.api.storage.externalstorage.ExternalStorageProviderFactory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class FluidHandlerPlatformExternalStorageProviderFactory implements ExternalStorageProviderFactory {
    @Override
    public ExternalStorageProvider create(final ServerLevel level,
                                           final BlockPos pos,
                                           final Direction direction) {
        return new FluidHandlerExternalStorageProvider(level, pos, direction);
    }
}
