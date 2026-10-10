package com.refinedmods.refinedstorage.forge.storage.portablegrid;

import com.refinedmods.refinedstorage.common.api.RefinedStorageClientApi;
import com.refinedmods.refinedstorage.common.support.direction.OrientedDirection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.StringRepresentable;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public class PortableGridUnbakedBlockStateModel implements BlockStateModel.Unbaked {
    public static final MapCodec<PortableGridUnbakedBlockStateModel> MODEL_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            StringRepresentable.fromEnum(OrientedDirection::values).fieldOf("direction")
                .forGetter(unbaked -> unbaked.direction),
            Codec.BOOL.fieldOf("active").forGetter(unbaked -> unbaked.active)
        ).apply(instance, PortableGridUnbakedBlockStateModel::new));

    private final OrientedDirection direction;
    private final boolean active;

    public PortableGridUnbakedBlockStateModel(final OrientedDirection direction, final boolean active) {
        this.direction = direction;
        this.active = active;
    }
    public MapCodec<? extends BlockStateModel.Unbaked> codec() {
        return MODEL_CODEC;
    }

    @Override
    public BlockStateModel bake(final ModelBaker modelBaker) {
        final ModelDebugName debugName = getClass()::toString;
        final Material.Baked particleMaterial = modelBaker.materials().get(
            new Material(createIdentifier("block/portable_grid/portable_grid_1")),
            debugName
        );
        return new PortableGridBlockStateModel(particleMaterial, modelBaker, direction, active);
    }

    @Override
    public void resolveDependencies(final Resolver resolver) {
        resolver.markDependency(PortableGridRenderingProperties.ACTIVE_MODEL);
        resolver.markDependency(PortableGridRenderingProperties.INACTIVE_MODEL);
        RefinedStorageClientApi.INSTANCE.getDiskModels().forEach(resolver::markDependency);
    }
}
