package com.refinedmods.refinedstorage.forge.networking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public class ActiveInactiveCablePartUnbakedBlockStateModel implements BlockStateModel.Unbaked {
    public static final MapCodec<ActiveInactiveCablePartUnbakedBlockStateModel> MODEL_CODEC =
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            DyeColor.CODEC.fieldOf("color").forGetter(model -> model.color),
            Identifier.CODEC.fieldOf("active_part_model").forGetter(model -> model.activePartModel),
            Identifier.CODEC.fieldOf("inactive_part_model").forGetter(model -> model.inactivePartModel),
            Direction.CODEC.fieldOf("direction").forGetter(model -> model.direction),
            Codec.BOOL.fieldOf("active").forGetter(model -> model.active)
        ).apply(instance, ActiveInactiveCablePartUnbakedBlockStateModel::new));

    private final DyeColor color;
    private final Identifier activePartModel;
    private final Identifier inactivePartModel;
    private final Direction direction;
    private final boolean active;
    private final Identifier coreModel;
    private final Identifier extensionModel;

    public ActiveInactiveCablePartUnbakedBlockStateModel(final DyeColor color,
                                                         final Identifier activePartModel,
                                                         final Identifier inactivePartModel,
                                                         final Direction direction,
                                                         final boolean active) {
        this.color = color;
        this.activePartModel = activePartModel;
        this.inactivePartModel = inactivePartModel;
        this.direction = direction;
        this.active = active;
        this.coreModel = createIdentifier("block/cable/core/" + color.getName());
        this.extensionModel = createIdentifier("block/cable/extension/" + color.getName());
    }

    @Override
    public void resolveDependencies(final Resolver resolver) {
        resolver.markDependency(coreModel);
        resolver.markDependency(extensionModel);
        resolver.markDependency(activePartModel);
        resolver.markDependency(inactivePartModel);
    }
    public MapCodec<? extends BlockStateModel.Unbaked> codec() {
        return MODEL_CODEC;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockStateModel bake(final ModelBaker modelBaker) {
        final ModelDebugName debugName = getClass()::toString;
        final Material.Baked particleMaterial = modelBaker.materials().get(
            new Material(createIdentifier("block/cable/" + color.getName())),
            debugName
        );
        return new CableBlockStateModel(particleMaterial, modelBaker, color,
            new ActiveInactiveCablePart(direction, active, activePartModel, inactivePartModel));
    }
}
