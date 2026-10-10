package com.refinedmods.refinedstorage.forge.networking;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

class ActiveInactiveCablePart implements CablePart {
    private final Direction direction;
    private final boolean active;
    private final Identifier activeModel;
    private final Identifier inactiveModel;

    ActiveInactiveCablePart(final Direction direction,
                            final boolean active,
                            final Identifier activeModel,
                            final Identifier inactiveModel) {
        this.direction = direction;
        this.active = active;
        this.activeModel = activeModel;
        this.inactiveModel = inactiveModel;
    }

    @Override
    public ModelBaker.@Nullable SharedOperationKey<BlockStateModelPart> getKey() {
        return new OperationKey(direction, active ? activeModel : inactiveModel);
    }

    private record OperationKey(Direction direction, Identifier model)
        implements ModelBaker.SharedOperationKey<BlockStateModelPart> {
        @Override
        public BlockStateModelPart compute(final ModelBaker modelBaker) {
            return SimpleModelWrapper.bake(modelBaker, model, CableBlockStateModel.getRotation(direction));
        }
    }
}
