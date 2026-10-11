package com.refinedmods.refinedstorage.forge.support.model;

import com.refinedmods.refinedstorage.common.content.Blocks;
import com.refinedmods.refinedstorage.common.content.ColorMap;
import com.refinedmods.refinedstorage.common.support.AbstractActiveColoredDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.direction.DefaultDirectionType;
import com.refinedmods.refinedstorage.common.support.direction.DirectionType;
import com.refinedmods.refinedstorage.common.support.direction.OrientedDirection;
import com.refinedmods.refinedstorage.common.support.direction.OrientedDirectionType;
import com.refinedmods.refinedstorage.common.storage.portablegrid.PortableGridBlock;

import com.refinedmods.refinedstorage.forge.mixin.ModelBakerHolder;
import com.refinedmods.refinedstorage.forge.networking.ActiveInactiveCablePartUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.networking.CablePartUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.networking.CableUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.storage.diskdrive.DiskDriveItemModel;
import com.refinedmods.refinedstorage.forge.storage.diskdrive.DiskDriveUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.storage.diskinterface.DiskInterfaceItemModel;
import com.refinedmods.refinedstorage.forge.storage.diskinterface.DiskInterfaceUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.storage.portablegrid.PortableGridItemModel;
import com.refinedmods.refinedstorage.forge.storage.portablegrid.PortableGridUnbakedBlockStateModel;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.event.ModelEvent;
import org.joml.Matrix4f;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

/**
 * Forge 26.1 has no registry for custom unbaked block state or item models, so the dynamic models
 * of the mod are baked here and swapped into the vanilla baking result. Datagen emits vanilla
 * placeholder models for the same content (see {@code ModelProviders}).
 */
public final class ModelSwapHandler {
    private static final DirectionType<Direction> FACING = DefaultDirectionType.FACE_CLICKED;
    private static final Property<Direction> FACING_PROPERTY = FACING.getProperty();
    private static final Property<OrientedDirection> ORIENTED_PROPERTY = OrientedDirectionType.INSTANCE.getProperty();
    private static final BooleanProperty ACTIVE = AbstractActiveColoredDirectionalBlock.ACTIVE;

    private static final Map<Block, DyeColor> CABLE_BLOCKS = new HashMap<>();
    private static final Map<Block, PartBlock> PART_BLOCKS = new HashMap<>();
    private static final Map<Block, DyeColor> DISK_INTERFACE_BLOCKS = new HashMap<>();
    private static final Map<Identifier, ItemModel.Unbaked> ITEM_MODELS = new HashMap<>();
    private static boolean initialized;

    private ModelSwapHandler() {
    }

    public static void onModifyBakingResult(final ModelEvent.ModifyBakingResult event) {
        final ModelBaker baker = ModelBakerHolder.get();
        if (baker == null) {
            return;
        }
        initialize();
        final ItemModel.BakingContext itemContext = new ItemModel.BakingContext(
            baker,
            Minecraft.getInstance().getEntityModels(),
            Minecraft.getInstance().getAtlasManager(),
            Minecraft.getInstance().playerSkinRenderCache(),
            event.getResults().missingModels().item(),
            null
        );
        event.getResults().blockStateModels().replaceAll((state, model) -> {
            final BlockStateModel replacement = blockStateModel(state, baker);
            return replacement != null ? replacement : model;
        });
        event.getResults().itemStackModels().replaceAll((id, model) -> {
            final ItemModel.Unbaked unbaked = ITEM_MODELS.get(id);
            return unbaked != null ? unbaked.bake(itemContext, new Matrix4f()) : model;
        });
    }

    private static synchronized void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        collect(Blocks.INSTANCE.getCable(), CABLE_BLOCKS, null);
        collect(Blocks.INSTANCE.getImporter(), null, PartKind.IMPORTER);
        collect(Blocks.INSTANCE.getExporter(), null, PartKind.EXPORTER);
        collect(Blocks.INSTANCE.getExternalStorage(), null, PartKind.EXTERNAL_STORAGE);
        collect(Blocks.INSTANCE.getConstructor(), null, PartKind.CONSTRUCTOR);
        collect(Blocks.INSTANCE.getDestructor(), null, PartKind.DESTRUCTOR);
        collect(Blocks.INSTANCE.getDiskInterface(), DISK_INTERFACE_BLOCKS, null);
        DISK_INTERFACE_BLOCKS.forEach((block, color) -> ITEM_MODELS.put(
            BuiltInRegistries.ITEM.getKey(block.asItem()),
            new DiskInterfaceItemModel.Unbaked(color)
        ));
        ITEM_MODELS.put(createIdentifier("disk_drive"), new DiskDriveItemModel.Unbaked());
        ITEM_MODELS.put(createIdentifier("portable_grid"), new PortableGridItemModel.Unbaked());
        ITEM_MODELS.put(createIdentifier("creative_portable_grid"), new PortableGridItemModel.Unbaked());
    }

    private static <T extends Block> void collect(final ColorMap<T> blocks,
                                                  final Map<Block, DyeColor> colors,
                                                  final PartKind kind) {
        blocks.forEach((color, id, supplier) -> {
            if (colors != null) {
                colors.put(supplier.get(), color);
            }
            if (kind != null) {
                PART_BLOCKS.put(supplier.get(), new PartBlock(kind, color));
            }
        });
    }

    private static BlockStateModel blockStateModel(final BlockState state, final ModelBaker baker) {
        final Block block = state.getBlock();
        if (block == Blocks.INSTANCE.getDiskDrive()) {
            return new DiskDriveUnbakedBlockStateModel(state.getValue(ORIENTED_PROPERTY)).bake(baker);
        }
        final DyeColor diskInterface = DISK_INTERFACE_BLOCKS.get(block);
        if (diskInterface != null) {
            return new DiskInterfaceUnbakedBlockStateModel(
                diskInterface,
                state.getValue(ORIENTED_PROPERTY),
                state.getValue(ACTIVE)
            ).bake(baker);
        }
        if (block == Blocks.INSTANCE.getPortableGrid() || block == Blocks.INSTANCE.getCreativePortableGrid()) {
            return new PortableGridUnbakedBlockStateModel(
                state.getValue(ORIENTED_PROPERTY),
                state.getValue(PortableGridBlock.ACTIVE)
            ).bake(baker);
        }
        final DyeColor cable = CABLE_BLOCKS.get(block);
        if (cable != null) {
            return new CableUnbakedBlockStateModel(cable).bake(baker);
        }
        final PartBlock part = PART_BLOCKS.get(block);
        if (part != null) {
            return part.bake(state, baker);
        }
        return null;
    }

    private record PartBlock(PartKind kind, DyeColor color) {
        BlockStateModel bake(final BlockState state, final ModelBaker baker) {
            final String path = "block/" + kind.path;
            if (kind.active) {
                return new ActiveInactiveCablePartUnbakedBlockStateModel(
                    color,
                    createIdentifier(path + "/active"),
                    createIdentifier(path + "/inactive"),
                    state.getValue(FACING_PROPERTY),
                    state.getValue(ACTIVE)
                ).bake(baker);
            }
            return new CablePartUnbakedBlockStateModel(
                color,
                createIdentifier(path + "/base"),
                state.getValue(FACING_PROPERTY)
            ).bake(baker);
        }
    }

    private enum PartKind {
        IMPORTER("importer", false),
        EXPORTER("exporter", false),
        EXTERNAL_STORAGE("external_storage", false),
        CONSTRUCTOR("constructor", true),
        DESTRUCTOR("destructor", true);

        private final String path;
        private final boolean active;

        PartKind(final String path, final boolean active) {
            this.path = path;
            this.active = active;
        }
    }
}
