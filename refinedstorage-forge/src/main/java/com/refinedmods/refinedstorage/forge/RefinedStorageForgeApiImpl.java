package com.refinedmods.refinedstorage.forge;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import com.refinedmods.refinedstorage.forge.api.RefinedStorageForgeApi;
import com.refinedmods.refinedstorage.forge.api.ResourceHandlerExternalPatternSinkStrategyFactory;
import com.refinedmods.refinedstorage.forge.autocrafting.ResourceHandlerPatternProviderExternalPatternSinkFactory;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class RefinedStorageForgeApiImpl implements RefinedStorageForgeApi {
    private final Capability<NetworkNodeContainerProvider> networkNodeContainerProviderCapability =
        CapabilityManager.get(new CapabilityToken<NetworkNodeContainerProvider>() {
        });

    @Override
    public Capability<NetworkNodeContainerProvider> getNetworkNodeContainerProviderCapability() {
        return networkNodeContainerProviderCapability;
    }

    @Override
    public void addResourceHandlerExternalPatternSinkStrategyFactory(
        final Class<? extends ResourceKey> resourceType,
        final ResourceHandlerExternalPatternSinkStrategyFactory factory
    ) {
        ((ResourceHandlerPatternProviderExternalPatternSinkFactory)
            Platform.INSTANCE.getPatternProviderExternalPatternSinkFactory()).addFactory(resourceType, factory);
    }
}
