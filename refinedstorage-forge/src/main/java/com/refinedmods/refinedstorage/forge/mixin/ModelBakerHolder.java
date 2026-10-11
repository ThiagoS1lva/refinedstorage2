package com.refinedmods.refinedstorage.forge.mixin;

import net.minecraft.client.resources.model.ModelBaker;

import org.jspecify.annotations.Nullable;

public final class ModelBakerHolder {
    private static volatile ModelBaker baker;

    private ModelBakerHolder() {
    }

    public static void set(final ModelBaker value) {
        baker = value;
    }

    @Nullable
    public static ModelBaker get() {
        return baker;
    }
}
