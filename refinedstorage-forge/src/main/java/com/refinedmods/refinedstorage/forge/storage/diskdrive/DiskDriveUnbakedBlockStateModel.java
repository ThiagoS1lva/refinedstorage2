package com.refinedmods.refinedstorage.forge.storage.diskdrive;

import com.refinedmods.refinedstorage.common.api.RefinedStorageClientApi;
import com.refinedmods.refinedstorage.common.support.direction.OrientedDirection;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.StringRepresentable;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public class DiskDriveUnbakedBlockStateModel implements BlockStateModel.Unbaked {
    public static final MapCodec<DiskDriveUnbakedBlockStateModel> MODEL_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            StringRepresentable.fromEnum(OrientedDirection::values).fieldOf("direction")
                .forGetter(model -> model.direction)
        ).apply(instance, DiskDriveUnbakedBlockStateModel::new));

    private final OrientedDirection direction;

    public DiskDriveUnbakedBlockStateModel(final OrientedDirection direction) {
        this.direction = direction;
    }

    public MapCodec<? extends BlockStateModel.Unbaked> codec() {
        return MODEL_CODEC;
    }

    @Override
    public BlockStateModel bake(final ModelBaker modelBaker) {
        final ModelDebugName debugName = getClass()::toString;
        final Material.Baked particleMaterial = modelBaker.materials().get(
            new Material(createIdentifier("block/disk_drive/top")),
            debugName
        );
        return new DiskDriveBlockStateModel(particleMaterial, modelBaker, direction);
    }

    @Override
    public void resolveDependencies(final Resolver resolver) {
        resolver.markDependency(DiskDriveRenderingProperties.BASE_MODEL);
        resolver.markDependency(DiskDriveRenderingProperties.INACTIVE_LED_MODEL);
        RefinedStorageClientApi.INSTANCE.getDiskModels().forEach(resolver::markDependency);
    }
}
