package com.refinedmods.refinedstorage.forge.api;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;

import net.minecraftforge.common.capabilities.Capability;

public interface RefinedStorageForgeApi {
    RefinedStorageForgeApi INSTANCE = new RefinedStorageForgeApiProxy();

    Capability<NetworkNodeContainerProvider> getNetworkNodeContainerProviderCapability();

    void addResourceHandlerExternalPatternSinkStrategyFactory(
        Class<? extends ResourceKey> resourceType,
        ResourceHandlerExternalPatternSinkStrategyFactory factory
    );
}
