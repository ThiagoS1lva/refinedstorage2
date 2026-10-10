package com.refinedmods.refinedstorage.forge.api;

import com.refinedmods.refinedstorage.api.autocrafting.task.ExternalPatternSink;
import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;

public interface ResourceHandlerExternalPatternSinkStrategy {
    ExternalPatternSink.Result insert(ResourceAmount resourceAmount, Action action);

    boolean isEmpty();
}
