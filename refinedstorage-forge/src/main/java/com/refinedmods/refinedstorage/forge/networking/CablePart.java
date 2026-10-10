package com.refinedmods.refinedstorage.forge.networking;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import org.jspecify.annotations.Nullable;

public interface CablePart {
    ModelBaker.@Nullable SharedOperationKey<BlockStateModelPart> getKey();
}
