package com.refinedmods.refinedstorage.forge.api;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;

import net.minecraftforge.common.capabilities.Capability;
import org.jspecify.annotations.Nullable;

public class RefinedStorageForgeApiProxy implements RefinedStorageForgeApi {
    @Nullable
    private RefinedStorageForgeApi delegate;

    public void setDelegate(final RefinedStorageForgeApi delegate) {
        if (this.delegate != null) {
            throw new IllegalStateException("Forge API already injected");
        }
        this.delegate = delegate;
    }

    @Override
    public Capability<NetworkNodeContainerProvider> getNetworkNodeContainerProviderCapability() {
        return ensureLoaded().getNetworkNodeContainerProviderCapability();
    }

    @Override
    public void addResourceHandlerExternalPatternSinkStrategyFactory(
        final Class<? extends ResourceKey> resourceType,
        final ResourceHandlerExternalPatternSinkStrategyFactory factory
    ) {
        ensureLoaded().addResourceHandlerExternalPatternSinkStrategyFactory(resourceType, factory);
    }

    private RefinedStorageForgeApi ensureLoaded() {
        if (delegate == null) {
            throw new IllegalStateException("Forge API not loaded yet");
        }
        return delegate;
    }
}
