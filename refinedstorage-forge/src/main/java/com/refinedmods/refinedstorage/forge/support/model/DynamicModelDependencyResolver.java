package com.refinedmods.refinedstorage.forge.support.model;

import com.refinedmods.refinedstorage.common.support.direction.OrientedDirection;
import com.refinedmods.refinedstorage.forge.networking.ActiveInactiveCablePartUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.networking.CablePartUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.networking.CableUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.storage.diskdrive.DiskDriveUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.storage.diskinterface.DiskInterfaceUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.storage.portablegrid.PortableGridUnbakedBlockStateModel;

import java.util.function.Consumer;

import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public final class DynamicModelDependencyResolver {
    private DynamicModelDependencyResolver() {
    }

    public static void registerDependencies(final Consumer<ResolvableModel> consumer) {
        for (final DyeColor color : DyeColor.values()) {
            consumer.accept(new CableUnbakedBlockStateModel(color));

            consumer.accept(new CablePartUnbakedBlockStateModel(
                color,
                createIdentifier("block/importer/base"),
                Direction.NORTH
            ));
            consumer.accept(new CablePartUnbakedBlockStateModel(
                color,
                createIdentifier("block/exporter/base"),
                Direction.NORTH
            ));
            consumer.accept(new CablePartUnbakedBlockStateModel(
                color,
                createIdentifier("block/external_storage/base"),
                Direction.NORTH
            ));

            consumer.accept(new ActiveInactiveCablePartUnbakedBlockStateModel(
                color,
                createIdentifier("block/constructor/active"),
                createIdentifier("block/constructor/inactive"),
                Direction.NORTH,
                false
            ));
            consumer.accept(new ActiveInactiveCablePartUnbakedBlockStateModel(
                color,
                createIdentifier("block/destructor/active"),
                createIdentifier("block/destructor/inactive"),
                Direction.NORTH,
                false
            ));

            consumer.accept(new DiskInterfaceUnbakedBlockStateModel(color, OrientedDirection.NORTH, false));
        }

        consumer.accept(new DiskDriveUnbakedBlockStateModel(OrientedDirection.NORTH));
        consumer.accept(new PortableGridUnbakedBlockStateModel(OrientedDirection.NORTH, false));
    }
}
