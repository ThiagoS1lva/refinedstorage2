package com.refinedmods.refinedstorage.forge.exporter;

import com.refinedmods.refinedstorage.api.network.impl.node.exporter.ExporterTransferStrategyImpl;
import com.refinedmods.refinedstorage.api.network.impl.node.exporter.MissingResourcesListeningExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.network.node.exporter.ExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;
import com.refinedmods.refinedstorage.common.api.exporter.ExporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.common.api.storage.root.FuzzyRootStorage;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeState;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.exporter.ExporterTransferQuotaProvider;
import com.refinedmods.refinedstorage.forge.storage.ItemHandlerStorage;
import com.refinedmods.refinedstorage.forge.support.CapabilityUtil;

import java.util.function.ToLongFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import static com.refinedmods.refinedstorage.api.network.impl.node.exporter.MissingResourcesListeningExporterTransferStrategy.OnMissingResources.scheduleAutocrafting;

public class ItemHandlerExporterTransferStrategyFactory implements ExporterTransferStrategyFactory {
    private final long singleAmount;
    private final Class<? extends ResourceKey> resourceKeyType;

    public ItemHandlerExporterTransferStrategyFactory(final long singleAmount,
                                                      final Class<? extends ResourceKey> resourceKeyType) {
        this.singleAmount = singleAmount;
        this.resourceKeyType = resourceKeyType;
    }

    @Override
    public Class<? extends ResourceKey> getResourceType() {
        return resourceKeyType;
    }

    @Override
    public ExporterTransferStrategy create(final ServerLevel level,
                                           final BlockPos pos,
                                           final Direction direction,
                                           final UpgradeState upgradeState,
                                           final boolean fuzzyMode) {
        final ItemHandlerStorage destination = new ItemHandlerStorage(
            () -> CapabilityUtil.getItemHandler(level, pos, direction)
        );
        final ExporterTransferStrategy strategy = create(
            fuzzyMode,
            destination,
            new ExporterTransferQuotaProvider(singleAmount, upgradeState, destination::getAmount, true)
        );
        if (upgradeState.has(Items.INSTANCE.getAutocraftingUpgrade())) {
            return new MissingResourcesListeningExporterTransferStrategy(strategy, scheduleAutocrafting(
                new ExporterTransferQuotaProvider(singleAmount, upgradeState, destination::getAmount, false)));
        }
        return strategy;
    }

    private ExporterTransferStrategy create(final boolean fuzzyMode,
                                            final InsertableStorage destination,
                                            final ToLongFunction<ResourceKey> transferQuotaProvider) {
        if (fuzzyMode) {
            return new ExporterTransferStrategyImpl(destination, transferQuotaProvider, FuzzyRootStorage.expander());
        }
        return new ExporterTransferStrategyImpl(destination, transferQuotaProvider);
    }
}
