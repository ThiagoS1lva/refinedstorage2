package com.refinedmods.refinedstorage.forge.storage.diskinterface;

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
import net.minecraft.world.item.DyeColor;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public class DiskInterfaceUnbakedBlockStateModel implements BlockStateModel.Unbaked {
    public static final MapCodec<DiskInterfaceUnbakedBlockStateModel> MODEL_CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            DyeColor.CODEC.fieldOf("color").forGetter(unbaked -> unbaked.color),
            StringRepresentable.fromEnum(OrientedDirection::values).fieldOf("direction")
                .forGetter(unbaked -> unbaked.direction),
            Codec.BOOL.fieldOf("active").forGetter(unbaked -> unbaked.active)
        ).apply(instance, DiskInterfaceUnbakedBlockStateModel::new));

    private final DyeColor color;
    private final OrientedDirection direction;
    private final boolean active;

    public DiskInterfaceUnbakedBlockStateModel(final DyeColor color, final OrientedDirection direction,
                                               final boolean active) {
        this.color = color;
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
            new Material(createIdentifier("block/disk_interface/top")),
            debugName
        );
        return new DiskInterfaceBlockStateModel(particleMaterial, modelBaker, color, direction, active);
    }

    @Override
    public void resolveDependencies(final Resolver resolver) {
        resolver.markDependency(DiskInterfaceRenderingProperties.INACTIVE_BASE_MODEL);
        resolver.markDependency(DiskInterfaceRenderingProperties.getActiveBaseModel(color));
        resolver.markDependency(DiskInterfaceRenderingProperties.INACTIVE_LED_MODEL);
        RefinedStorageClientApi.INSTANCE.getDiskModels().forEach(resolver::markDependency);
    }
}
