package com.refinedmods.refinedstorage.forge.importer;

import com.refinedmods.refinedstorage.api.network.node.importer.ImporterTransferStrategy;
import com.refinedmods.refinedstorage.api.network.impl.node.importer.ImporterTransferStrategyImpl;
import com.refinedmods.refinedstorage.common.api.importer.ImporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeState;
import com.refinedmods.refinedstorage.common.importer.ImporterTransferQuotaProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class FluidHandlerImporterTransferStrategyFactory implements ImporterTransferStrategyFactory {
    private final long singleAmount;

    public FluidHandlerImporterTransferStrategyFactory(final long singleAmount) {
        this.singleAmount = singleAmount;
    }

    @Override
    public ImporterTransferStrategy create(final ServerLevel level,
                                           final BlockPos pos,
                                           final Direction direction,
                                           final UpgradeState upgradeState) {
        final FluidHandlerImporterSource source = new FluidHandlerImporterSource(level, pos, direction);
        final ImporterTransferQuotaProvider transferQuotaProvider = new ImporterTransferQuotaProvider(
            singleAmount,
            upgradeState,
            source::getAmount
        );
        return new ImporterTransferStrategyImpl(source, transferQuotaProvider);
    }
}
