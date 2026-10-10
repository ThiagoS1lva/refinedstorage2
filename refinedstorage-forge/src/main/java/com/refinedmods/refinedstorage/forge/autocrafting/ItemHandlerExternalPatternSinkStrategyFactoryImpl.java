package com.refinedmods.refinedstorage.forge.autocrafting;

import com.refinedmods.refinedstorage.forge.api.ResourceHandlerExternalPatternSinkStrategy;
import com.refinedmods.refinedstorage.forge.api.ResourceHandlerExternalPatternSinkStrategyFactory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class ItemHandlerExternalPatternSinkStrategyFactoryImpl
    implements ResourceHandlerExternalPatternSinkStrategyFactory {
    @Override
    public ResourceHandlerExternalPatternSinkStrategy create(final ServerLevel level,
                                                             final BlockPos pos,
                                                             final Direction direction) {
        return new ItemHandlerExternalPatternSinkStrategyImpl(level, pos, direction);
    }
}
