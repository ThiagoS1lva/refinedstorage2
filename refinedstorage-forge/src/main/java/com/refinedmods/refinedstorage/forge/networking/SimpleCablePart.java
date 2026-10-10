package com.refinedmods.refinedstorage.forge.networking;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

class SimpleCablePart implements CablePart {
    private final Direction direction;
    private final Identifier model;

    SimpleCablePart(final Direction direction, final Identifier model) {
        this.direction = direction;
        this.model = model;
    }

    @Override
    public ModelBaker.@Nullable SharedOperationKey<BlockStateModelPart> getKey() {
        return new OperationKey(direction, model);
    }

    private record OperationKey(Direction direction, Identifier model)
        implements ModelBaker.SharedOperationKey<BlockStateModelPart> {
        @Override
        public BlockStateModelPart compute(final ModelBaker modelBaker) {
            return SimpleModelWrapper.bake(modelBaker, model, CableBlockStateModel.getRotation(direction));
        }
    }
}
