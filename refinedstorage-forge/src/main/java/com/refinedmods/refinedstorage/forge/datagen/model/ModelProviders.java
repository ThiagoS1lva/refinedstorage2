package com.refinedmods.refinedstorage.forge.datagen.model;

import com.refinedmods.refinedstorage.common.autocrafting.PatternTypeItemModelProperty;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternType;
import com.refinedmods.refinedstorage.common.configurationcard.ActiveConfigurationCardItemModelProperty;
import com.refinedmods.refinedstorage.common.content.BlockColorMap;
import com.refinedmods.refinedstorage.common.content.Blocks;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.controller.AbstractControllerBlock;
import com.refinedmods.refinedstorage.common.controller.ControllerEnergyLevelItemModelProperty;
import com.refinedmods.refinedstorage.common.controller.ControllerEnergyType;
import com.refinedmods.refinedstorage.common.detector.DetectorBlock;
import com.refinedmods.refinedstorage.common.iface.InterfaceBlock;
import com.refinedmods.refinedstorage.common.misc.ProcessorItem;
import com.refinedmods.refinedstorage.common.networking.ActiveNetworkCardItemModelProperty;
import com.refinedmods.refinedstorage.common.networking.NetworkReceiverBlock;
import com.refinedmods.refinedstorage.common.networking.NetworkTransmitterBlock;
import com.refinedmods.refinedstorage.common.networking.NetworkTransmitterState;
import com.refinedmods.refinedstorage.common.security.ActiveSecurityCardItemModelProperty;
import com.refinedmods.refinedstorage.common.storage.FluidStorageVariant;
import com.refinedmods.refinedstorage.common.storage.ItemStorageVariant;
import com.refinedmods.refinedstorage.common.support.AbstractActiveColoredDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.direction.DefaultDirectionType;
import com.refinedmods.refinedstorage.common.support.direction.HorizontalDirectionType;
import com.refinedmods.refinedstorage.common.support.direction.OrientedDirection;
import com.refinedmods.refinedstorage.common.support.direction.OrientedDirectionType;
import com.refinedmods.refinedstorage.common.support.network.item.NetworkBoundItemModelProperty;
import com.refinedmods.refinedstorage.forge.networking.ActiveInactiveCablePartUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.networking.CablePartUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.networking.CableUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.storage.diskdrive.DiskDriveItemModel;
import com.refinedmods.refinedstorage.forge.storage.diskdrive.DiskDriveUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.storage.diskinterface.DiskInterfaceItemModel;
import com.refinedmods.refinedstorage.forge.storage.diskinterface.DiskInterfaceUnbakedBlockStateModel;
import com.refinedmods.refinedstorage.forge.storage.portablegrid.PortableGridItemModel;
import com.refinedmods.refinedstorage.forge.storage.portablegrid.PortableGridUnbakedBlockStateModel;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.ModelProvider.BlockStateGeneratorCollector;
import net.minecraft.client.data.models.ModelProvider.ItemInfoCollector;
import net.minecraft.client.data.models.ModelProvider.SimpleModelCollector;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.MOD_ID;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public class ModelProviders extends ModelProvider {
    private static final Identifier BOTTOM_TEXTURE = createIdentifier("block/bottom");

    private static MultiVariant plainVariant(final Identifier model) {
        return new MultiVariant(WeightedList.of(new Variant(model)));
    }

    private static ConditionBuilder condition() {
        return new ConditionBuilder();
    }


    public ModelProviders(final PackOutput output) {
        super(output);
    }

    @Override
    protected Stream<Block> getKnownBlocks() {
        return Stream.of();
    }

    @Override
    protected Stream<Item> getKnownItems() {
        return Stream.of();
    }

    @Override
    protected BlockModelGenerators getBlockModelGenerators(final BlockStateGeneratorCollector states,
                                                           final ItemInfoCollector items,
                                                           final SimpleModelCollector models) {
        registerDiskDrive(states, items, models);
        registerDiskInterfaces(states, items, models);
        registerPortableGrids(states, items, models);
        registerCables(states, items, models);
        registerControllers(states, items, models);
        registerImporters(states, items, models);
        registerExporters(states, items, models);
        registerExternalStorages(states, items, models);
        registerConstructors(states, items, models);
        registerDestructors(states, items, models);
        registerWirelessTransmitters(states, items, models);
        registerDetectors(states, items, models);
        registerNetworkReceivers(states, items, models);
        registerNetworkTransmitters(states, items, models);
        registerAutocrafters(states, items, models);
        registerSecurityManagers(states, items, models);
        registerRelays(states, items, models);
        registerMachineCasing(states, items, models);
        registerInterface(states, items, models);
        registerStorageBlocks(states, items, models);
        registerStorageMonitor(states, items, models);
        registerDirectionalBlock(states, items, models, "grid", Blocks.INSTANCE.getGrid());
        registerDirectionalBlock(states, items, models, "crafting_grid", Blocks.INSTANCE.getCraftingGrid());
        registerDirectionalBlock(states, items, models, "pattern_grid", Blocks.INSTANCE.getPatternGrid());
        registerDirectionalBlock(states, items, models, "autocrafter_manager",
            Blocks.INSTANCE.getAutocrafterManager());
        registerDirectionalBlock(states, items, models, "autocrafting_monitor",
            Blocks.INSTANCE.getAutocraftingMonitor());
        registerPatterns(states, items, models);
        registerConfigurationCard(states, items, models);
        registerNetworkCard(states, items, models);
        registerSecurityCard(states, items, models);
        registerNetworkBound(states, items, models, "wireless_grid", Items.INSTANCE.getWirelessGrid(),
            Items.INSTANCE.getCreativeWirelessGrid());
        registerNetworkBound(states, items, models, "wireless_autocrafting_monitor",
            Items.INSTANCE.getWirelessAutocraftingMonitor(),
            Items.INSTANCE.getCreativeWirelessAutocraftingMonitor());
        return new BlockModelGenerators(states, items, models);
    }

    @Override
    protected ItemModelGenerators getItemModelGenerators(final ItemInfoCollector items,
                                                         final SimpleModelCollector models) {
        return new ItemGenerators(items, models);
    }

    private static class ItemGenerators extends ItemModelGenerators {
        ItemGenerators(final ItemModelOutput items,
                       final BiConsumer<Identifier, ModelInstance> models) {
            super(items, models);
            registerSimpleItems();
        }

        private void registerSimpleItems() {
            generateFlatItem(Items.INSTANCE.getSilicon(), ModelTemplates.FLAT_ITEM);
            generateFlatItem(Items.INSTANCE.getWrench(), ModelTemplates.FLAT_ITEM);
            generateFlatItem(Items.INSTANCE.getUpgrade(), ModelTemplates.FLAT_ITEM);
            generateFlatItem(Items.INSTANCE.getRangeUpgrade(), ModelTemplates.FLAT_ITEM);
            generateFlatItem(Items.INSTANCE.getCreativeRangeUpgrade(), ModelTemplates.FLAT_ITEM);
            generateFlatItem(Items.INSTANCE.getRegulatorUpgrade(), ModelTemplates.FLAT_ITEM);
            generateFlatItem(Items.INSTANCE.getAutocraftingUpgrade(), ModelTemplates.FLAT_ITEM);
            generateFlatItem(Items.INSTANCE.getSpeedUpgrade(), ModelTemplates.FLAT_ITEM);
            generateFlatItem(Items.INSTANCE.getStackUpgrade(), ModelTemplates.FLAT_ITEM);
            generateFlatItem(Items.INSTANCE.getSilkTouchUpgrade(), ModelTemplates.FLAT_ITEM);
            generateFlatItem(Items.INSTANCE.getConstructionCore(), ModelTemplates.FLAT_ITEM);
            generateFlatItem(Items.INSTANCE.getDestructionCore(), ModelTemplates.FLAT_ITEM);
            final Identifier fortuneUpgradeId = createIdentifier("item/fortune_upgrade");
            ModelTemplates.FLAT_ITEM.create(fortuneUpgradeId, TextureMapping.layer0(texture(fortuneUpgradeId)),
                modelOutput);
            final ItemModel.Unbaked fortuneUpgrade = ItemModelUtils.plainModel(fortuneUpgradeId);
            itemModelOutput.accept(Items.INSTANCE.getFortune1Upgrade(), fortuneUpgrade);
            itemModelOutput.accept(Items.INSTANCE.getFortune2Upgrade(), fortuneUpgrade);
            itemModelOutput.accept(Items.INSTANCE.getFortune3Upgrade(), fortuneUpgrade);
            for (final ProcessorItem.Type type : ProcessorItem.Type.values()) {
                generateFlatItem(Items.INSTANCE.getProcessor(type), ModelTemplates.FLAT_ITEM);
            }
            generateFlatItem(Items.INSTANCE.getProcessorBinding(), ModelTemplates.FLAT_ITEM);
            generateFlatItem(Items.INSTANCE.getQuartzEnrichedCopper(), ModelTemplates.FLAT_ITEM);
            generateFlatItem(Items.INSTANCE.getQuartzEnrichedIron(), ModelTemplates.FLAT_ITEM);
            for (final ItemStorageVariant variant : ItemStorageVariant.values()) {
                generateFlatItem(Items.INSTANCE.getItemStorageDisk(variant), ModelTemplates.FLAT_ITEM);
                if (variant != ItemStorageVariant.CREATIVE) {
                    generateFlatItem(Items.INSTANCE.getItemStoragePart(variant), ModelTemplates.FLAT_ITEM);
                }
            }
            for (final FluidStorageVariant variant : FluidStorageVariant.values()) {
                generateFlatItem(Items.INSTANCE.getFluidStorageDisk(variant), ModelTemplates.FLAT_ITEM);
                if (variant != FluidStorageVariant.CREATIVE) {
                    generateFlatItem(Items.INSTANCE.getFluidStoragePart(variant), ModelTemplates.FLAT_ITEM);
                }
            }
            generateFlatItem(Items.INSTANCE.getStorageHousing(), ModelTemplates.FLAT_ITEM);
            itemModelOutput.accept(Items.INSTANCE.getDebugStick(),
                ItemModelUtils.plainModel(Identifier.withDefaultNamespace("item/stick")));
            final Identifier fallbackSecurityCardModel = ModelTemplates.FLAT_ITEM.create(
                Items.INSTANCE.getFallbackSecurityCard(),
                TextureMapping.layer0(texture(createIdentifier("item/security_card/fallback"))),
                modelOutput
            );
            itemModelOutput.accept(Items.INSTANCE.getFallbackSecurityCard(),
                ItemModelUtils.plainModel(fallbackSecurityCardModel));
        }
    }

    private void registerDiskDrive(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        itemModelOutput.accept(Blocks.INSTANCE.getDiskDrive().asItem(),
            ItemModelUtils.plainModel(createIdentifier("block/disk_drive/base")));
        blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.INSTANCE.getDiskDrive(),
            plainVariant(createIdentifier("block/disk_drive/base"))));
    }

    private void registerDiskInterfaces(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        Blocks.INSTANCE.getDiskInterface().forEach((color, id, diskInterface) -> {
            itemModelOutput.accept(diskInterface.get().asItem(),
                ItemModelUtils.plainModel(createIdentifier("block/disk_interface/" + color.getName())));

            blockStateOutput.accept(MultiVariantGenerator.dispatch(diskInterface.get(),
                plainVariant(createIdentifier("block/disk_interface/" + color.getName()))));
        });
    }

    private void registerPortableGrids(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.INSTANCE.getPortableGrid(),
            plainVariant(createIdentifier("block/portable_grid/base"))));
        blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.INSTANCE.getCreativePortableGrid(),
            plainVariant(createIdentifier("block/portable_grid/base"))));
        itemModelOutput.accept(Blocks.INSTANCE.getPortableGrid().asItem(),
            ItemModelUtils.plainModel(createIdentifier("block/portable_grid/base")));
        itemModelOutput.accept(Blocks.INSTANCE.getCreativePortableGrid().asItem(),
            ItemModelUtils.plainModel(createIdentifier("block/portable_grid/base")));
    }

    private void registerCables(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        Blocks.INSTANCE.getCable().forEach((color, id, cable) -> {
            itemModelOutput.accept(cable.get().asItem(), ItemModelUtils.plainModel(
                createIdentifier("item/cable/" + color.getName())));

            blockStateOutput.accept(MultiVariantGenerator.dispatch(cable.get(),
                plainVariant(createIdentifier("block/cable/core/" + color.getName()))));
        });
    }

    private void registerControllers(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier off = createIdentifier("block/controller/off");
        final Identifier on = createIdentifier("block/controller/on");
        final Identifier nearlyOff = createIdentifier("block/controller/nearly_off");
        final Identifier nearlyOn = createIdentifier("block/controller/nearly_on");
        Blocks.INSTANCE.getController().forEach((color, id, controller) -> {
            registerControllerBlockStates(blockStateOutput, itemModelOutput, modelOutput, color, controller.get(), off, nearlyOff, nearlyOn);
        });
        Blocks.INSTANCE.getCreativeController().forEach((color, id, controller) ->
            registerControllerBlockStates(blockStateOutput, itemModelOutput, modelOutput, color, controller.get(), off, nearlyOff, nearlyOn));
    }

    private static void registerControllerBlockStates(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput,
                                                      final DyeColor color,
                                                      final AbstractControllerBlock<?> controller,
                                                      final Identifier off, final Identifier nearlyOff,
                                                      final Identifier nearlyOn) {
        final Identifier model = createIdentifier("block/controller/" + color.getName());
        blockStateOutput.accept(MultiPartGenerator.multiPart(controller)
            .with(condition().term(AbstractControllerBlock.ENERGY_TYPE, ControllerEnergyType.OFF),
                plainVariant(off))
            .with(condition().term(AbstractControllerBlock.ENERGY_TYPE, ControllerEnergyType.NEARLY_ON),
                plainVariant(nearlyOn))
            .with(condition().term(AbstractControllerBlock.ENERGY_TYPE, ControllerEnergyType.ON),
                plainVariant(model))
            .with(condition().term(AbstractControllerBlock.ENERGY_TYPE, ControllerEnergyType.NEARLY_OFF),
                plainVariant(nearlyOff)));
        itemModelOutput.accept(controller.asItem(), ItemModelUtils.plainModel(model));
    }

    private void registerImporters(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        Blocks.INSTANCE.getImporter().forEach((color, id, importer) -> {
            itemModelOutput.accept(importer.get().asItem(), ItemModelUtils.plainModel(
                createIdentifier("item/importer/" + color.getName())));

            blockStateOutput.accept(MultiVariantGenerator.dispatch(importer.get(),
                plainVariant(createIdentifier("block/importer/base"))));
        });
    }

    private void registerExporters(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        Blocks.INSTANCE.getExporter().forEach((color, id, exporter) -> {
            itemModelOutput.accept(exporter.get().asItem(), ItemModelUtils.plainModel(
                createIdentifier("item/exporter/" + color.getName())));

            blockStateOutput.accept(MultiVariantGenerator.dispatch(exporter.get(),
                plainVariant(createIdentifier("block/exporter/base"))));
        });
    }

    private void registerExternalStorages(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        Blocks.INSTANCE.getExternalStorage().forEach((color, id, externalStorage) -> {
            itemModelOutput.accept(externalStorage.get().asItem(), ItemModelUtils.plainModel(
                createIdentifier("item/external_storage/" + color.getName())));

            blockStateOutput.accept(MultiVariantGenerator.dispatch(externalStorage.get(),
                plainVariant(createIdentifier("block/external_storage/base"))));
        });
    }

    private void registerConstructors(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier inactiveBlockModel = createIdentifier("block/constructor/inactive");
        final Identifier activeBlockModel = createIdentifier("block/constructor/active");

        Blocks.INSTANCE.getConstructor().forEach((color, id, constructor) -> {
            itemModelOutput.accept(constructor.get().asItem(), ItemModelUtils.plainModel(
                createIdentifier("item/constructor/" + color.getName())));

            blockStateOutput.accept(MultiVariantGenerator.dispatch(constructor.get(),
                plainVariant(inactiveBlockModel)));
        });
    }

    private void registerDestructors(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier inactiveBlockModel = createIdentifier("block/destructor/inactive");
        final Identifier activeBlockModel = createIdentifier("block/destructor/active");

        Blocks.INSTANCE.getDestructor().forEach((color, id, destructor) -> {
            itemModelOutput.accept(destructor.get().asItem(), ItemModelUtils.plainModel(
                createIdentifier("item/destructor/" + color.getName())));

            blockStateOutput.accept(MultiVariantGenerator.dispatch(destructor.get(),
                plainVariant(inactiveBlockModel)));
        });
    }

    private void registerWirelessTransmitters(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier inactiveBlockModel = createIdentifier("block/wireless_transmitter/inactive");

        Blocks.INSTANCE.getWirelessTransmitter().forEach((color, id, transmitter) -> {
            itemModelOutput.accept(transmitter.get().asItem(), ItemModelUtils.plainModel(
                createIdentifier("item/wireless_transmitter/" + color.getName())));

            final Identifier activeBlockModel =
                createIdentifier("block/wireless_transmitter/" + color.getName());

            blockStateOutput.accept(MultiVariantGenerator.dispatch(transmitter.get())
                .with(PropertyDispatch.initial(AbstractActiveColoredDirectionalBlock.ACTIVE)
                    .select(false, plainVariant(inactiveBlockModel))
                    .select(true, plainVariant(activeBlockModel)))
                .with(PropertyDispatch.modify(DefaultDirectionType.FACE_CLICKED.getProperty())
                    .generate(direction -> variant -> variant
                        .withXRot(getXRot(direction))
                        .withYRot(getYRot(direction)))));
        });
    }

    private void registerDetectors(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier unpoweredBlockModel = createIdentifier("block/detector/unpowered");

        Blocks.INSTANCE.getDetector().forEach((color, id, transmitter) -> {
            itemModelOutput.accept(transmitter.get().asItem(), ItemModelUtils.plainModel(
                createIdentifier("item/detector/" + color.getName())));

            final Identifier poweredBlockModel = createIdentifier("block/detector/" + color.getName());

            blockStateOutput.accept(MultiVariantGenerator.dispatch(transmitter.get())
                .with(PropertyDispatch.initial(DetectorBlock.POWERED)
                    .select(false, plainVariant(unpoweredBlockModel))
                    .select(true, plainVariant(poweredBlockModel)))
                .with(PropertyDispatch.modify(DefaultDirectionType.FACE_CLICKED.getProperty())
                    .generate(direction -> variant -> variant
                        .withXRot(getXRot(direction))
                        .withYRot(getYRot(direction)))));
        });
    }

    private void registerNetworkReceivers(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier inactiveModel = createIdentifier("block/network_receiver/inactive");

        Blocks.INSTANCE.getNetworkReceiver().forEach((color, id, receiver) -> {
            final Identifier activeModel = createIdentifier("block/network_receiver/" + color.getName());

            itemModelOutput.accept(receiver.get().asItem(), ItemModelUtils.plainModel(activeModel));

            blockStateOutput.accept(MultiVariantGenerator.dispatch(receiver.get())
                .with(PropertyDispatch.initial(NetworkReceiverBlock.ACTIVE)
                    .select(false, plainVariant(inactiveModel))
                    .select(true, plainVariant(activeModel))));
        });
    }

    private void registerNetworkTransmitters(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier inactiveModel = createIdentifier("block/network_transmitter/inactive");
        final Identifier errorModel = createIdentifier("block/network_transmitter/error");

        Blocks.INSTANCE.getNetworkTransmitter().forEach((color, id, transmitter) -> {
            final Identifier activeModel = createIdentifier("block/network_transmitter/" + color.getName());

            itemModelOutput.accept(transmitter.get().asItem(), ItemModelUtils.plainModel(activeModel));

            blockStateOutput.accept(MultiVariantGenerator.dispatch(transmitter.get())
                .with(PropertyDispatch.initial(NetworkTransmitterBlock.STATE)
                    .select(NetworkTransmitterState.INACTIVE, plainVariant(inactiveModel))
                    .select(NetworkTransmitterState.ERROR, plainVariant(errorModel))
                    .select(NetworkTransmitterState.ACTIVE, plainVariant(activeModel))));
        });
    }

    private void registerAutocrafters(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier inactiveModel = createIdentifier("block/autocrafter/inactive");

        Blocks.INSTANCE.getAutocrafter().forEach((color, id, autocrafter) -> {
            final Identifier activeModel = createIdentifier("block/autocrafter/" + color.getName());

            itemModelOutput.accept(autocrafter.get().asItem(), ItemModelUtils.plainModel(activeModel));

            blockStateOutput.accept(MultiVariantGenerator.dispatch(autocrafter.get())
                .with(PropertyDispatch.initial(AbstractActiveColoredDirectionalBlock.ACTIVE)
                    .select(false, plainVariant(inactiveModel))
                    .select(true, plainVariant(activeModel)))
                .with(PropertyDispatch.modify(DefaultDirectionType.FACE_CLICKED.getProperty())
                    .generate(direction -> variant -> variant
                        .withXRot(getAutocrafterXRot(direction))
                        .withYRot(getAutocrafterYRot(direction)))));
        });
    }

    private void registerSecurityManagers(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier inactiveModel = createIdentifier("block/security_manager/inactive");

        Blocks.INSTANCE.getSecurityManager().forEach((color, id, securityManager) -> {
            final Identifier activeModel = createIdentifier("block/security_manager/" + color.getName());

            itemModelOutput.accept(securityManager.get().asItem(), ItemModelUtils.plainModel(activeModel));

            blockStateOutput.accept(MultiVariantGenerator.dispatch(securityManager.get())
                .with(PropertyDispatch.initial(AbstractActiveColoredDirectionalBlock.ACTIVE)
                    .select(false, plainVariant(inactiveModel))
                    .select(true, plainVariant(activeModel)))
                .with(PropertyDispatch.modify(HorizontalDirectionType.INSTANCE.getProperty())
                    .generate(direction -> variant -> variant
                        .withXRot(getXRot(OrientedDirection.forHorizontalDirection(direction)))
                        .withYRot(getYRot(OrientedDirection.forHorizontalDirection(direction))))));
        });
    }

    private void registerRelays(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier inactiveModel = createIdentifier("block/relay/inactive");

        Blocks.INSTANCE.getRelay().forEach((color, id, relay) -> {
            final Identifier activeModel = createIdentifier("block/relay/" + color.getName());

            itemModelOutput.accept(relay.get().asItem(), ItemModelUtils.plainModel(activeModel));

            blockStateOutput.accept(MultiVariantGenerator.dispatch(relay.get())
                .with(PropertyDispatch.initial(AbstractActiveColoredDirectionalBlock.ACTIVE)
                    .select(false, plainVariant(inactiveModel))
                    .select(true, plainVariant(activeModel)))
                .with(PropertyDispatch.modify(DefaultDirectionType.FACE_CLICKED.getProperty())
                    .generate(direction -> variant -> variant
                        .withXRot(getXRot(OrientedDirection.forDirection(direction)))
                        .withYRot(getYRot(OrientedDirection.forDirection(direction))))));
        });
    }

    private void registerMachineCasing(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final TextureMapping textures = TextureMapping.column(
            texture(createIdentifier("block/machine_casing/side")),
            texture(createIdentifier("block/machine_casing/end"))
        );
        final Identifier machineCasingModel = ModelTemplates.CUBE_COLUMN.create(Blocks.INSTANCE.getMachineCasing(),
            textures, modelOutput);
        blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.INSTANCE.getMachineCasing(),
            plainVariant(machineCasingModel)));
        itemModelOutput.accept(Blocks.INSTANCE.getMachineCasing().asItem(),
            ItemModelUtils.plainModel(machineCasingModel));
    }

    private void registerInterface(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier activeModel = ModelTemplates.CUBE_ALL.create(
            createIdentifier("block/interface/active"),
            TextureMapping.cube(texture(createIdentifier("block/interface/active"))),
            modelOutput
        );
        final Identifier inactiveModel = ModelTemplates.CUBE_ALL.create(
            createIdentifier("block/interface/inactive"),
            TextureMapping.cube(texture(createIdentifier("block/interface/inactive"))),
            modelOutput
        );
        blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.INSTANCE.getInterface())
            .with(PropertyDispatch.initial(InterfaceBlock.ACTIVE)
                .select(true, plainVariant(activeModel))
                .select(false, plainVariant(inactiveModel))));
        itemModelOutput.accept(Blocks.INSTANCE.getInterface().asItem(),
            ItemModelUtils.plainModel(inactiveModel));
    }

    private void registerStorageBlocks(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        for (final ItemStorageVariant variant : ItemStorageVariant.values()) {
            final Identifier blockModel = ModelTemplates.CUBE_ALL.create(
                createIdentifier("block/storage_block/" + variant.getName() + "_storage_block"),
                TextureMapping.cube(texture(
                    createIdentifier("block/storage_block/" + variant.getName() + "_storage_block"))),
                modelOutput
            );
            final Block block = Blocks.INSTANCE.getItemStorageBlock(variant);
            blockStateOutput.accept(MultiVariantGenerator.dispatch(block, plainVariant(blockModel)));
            itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(blockModel));
        }
        for (final FluidStorageVariant variant : FluidStorageVariant.values()) {
            final Identifier blockModel = ModelTemplates.CUBE_ALL.create(
                createIdentifier("block/fluid_storage_block/" + variant.getName() + "_fluid_storage_block"),
                TextureMapping.cube(texture(createIdentifier("block/fluid_storage_block/" + variant.getName()
                    + "_fluid_storage_block"))),
                modelOutput
            );
            final Block block = Blocks.INSTANCE.getFluidStorageBlock(variant);
            blockStateOutput.accept(MultiVariantGenerator.dispatch(block, plainVariant(blockModel)));
            itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(blockModel));
        }
    }

    private void registerStorageMonitor(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier blockModel = ModelTemplates.CUBE.create(
            createIdentifier("block/storage_monitor"),
            new TextureMapping()
                .put(TextureSlot.PARTICLE, texture(createIdentifier("block/side")))
                .put(TextureSlot.NORTH, texture(createIdentifier("block/storage_monitor/front")))
                .put(TextureSlot.EAST, texture(createIdentifier("block/storage_monitor/left")))
                .put(TextureSlot.SOUTH, texture(createIdentifier("block/storage_monitor/back")))
                .put(TextureSlot.WEST, texture(createIdentifier("block/storage_monitor/right")))
                .put(TextureSlot.UP, texture(createIdentifier("block/storage_monitor/top")))
                .put(TextureSlot.DOWN, texture(BOTTOM_TEXTURE)),
            modelOutput
        );
        blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.INSTANCE.getStorageMonitor(),
                plainVariant(blockModel))
            .with(PropertyDispatch.modify(OrientedDirectionType.INSTANCE.getProperty())
                .generate(direction -> variant -> variant
                    .withXRot(getXRot(direction))
                    .withYRot(getYRot(direction)))));
        itemModelOutput.accept(Blocks.INSTANCE.getStorageMonitor().asItem(),
            ItemModelUtils.plainModel(blockModel));
    }

    private void registerDirectionalBlock(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput,
                                          final String name,
                                          final BlockColorMap<?, ?> blocks) {
        final Identifier inactiveBlockModel = createIdentifier("block/" + name + "/inactive");
        blocks.forEach((color, id, block) -> {
            final Identifier blockModel = createIdentifier("block/" + name + "/" + color.getName());
            blockStateOutput.accept(MultiVariantGenerator.dispatch(block.get())
                .with(PropertyDispatch.initial(AbstractActiveColoredDirectionalBlock.ACTIVE)
                    .select(false, plainVariant(inactiveBlockModel))
                    .select(true, plainVariant(blockModel)))
                .with(PropertyDispatch.modify(OrientedDirectionType.INSTANCE.getProperty())
                    .generate(direction -> variant -> variant
                        .withXRot(getXRot(direction))
                        .withYRot(getYRot(direction)))));
            itemModelOutput.accept(block.get().asItem(), ItemModelUtils.plainModel(blockModel));
        });
    }

    private Quadrant getXRot(final OrientedDirection direction) {
        return switch (direction) {
            case NORTH, EAST, SOUTH, WEST -> Quadrant.R0;
            case DOWN_NORTH, DOWN_EAST, DOWN_SOUTH, DOWN_WEST -> Quadrant.R90;
            case UP_NORTH, UP_EAST, UP_SOUTH, UP_WEST -> Quadrant.R270;
        };
    }

    private Quadrant getXRot(final Direction direction) {
        return switch (direction) {
            case DOWN -> Quadrant.R0;
            case UP -> Quadrant.R180;
            case NORTH, SOUTH, WEST, EAST -> Quadrant.R90;
        };
    }

    private Quadrant getYRot(final OrientedDirection direction) {
        return switch (direction) {
            case NORTH, UP_SOUTH, DOWN_NORTH -> Quadrant.R0;
            case EAST, UP_WEST, DOWN_WEST -> Quadrant.R90;
            case SOUTH, UP_NORTH, DOWN_SOUTH -> Quadrant.R180;
            case WEST, UP_EAST, DOWN_EAST -> Quadrant.R270;
        };
    }

    private Quadrant getYRot(final Direction direction) {
        return switch (direction) {
            case DOWN, UP, SOUTH -> Quadrant.R0;
            case NORTH -> Quadrant.R180;
            case EAST -> Quadrant.R270;
            case WEST -> Quadrant.R90;
        };
    }

    private Quadrant getAutocrafterXRot(final Direction direction) {
        return switch (direction) {
            case DOWN -> Quadrant.R180;
            case UP -> Quadrant.R0;
            case NORTH, SOUTH, WEST, EAST -> Quadrant.R90;
        };
    }

    private Quadrant getAutocrafterYRot(final Direction direction) {
        return switch (direction) {
            case DOWN, UP, NORTH -> Quadrant.R0;
            case SOUTH -> Quadrant.R180;
            case EAST -> Quadrant.R90;
            case WEST -> Quadrant.R270;
        };
    }

    private void registerPatterns(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier empty = ModelTemplates.FLAT_ITEM.create(createIdentifier("item/pattern/empty"),
            TextureMapping.layer0(texture(createIdentifier("item/pattern/empty"))), modelOutput);
        final Map<PatternType, Identifier> patternModels = Arrays.stream(PatternType.values())
            .collect(Collectors.toMap(type -> type, type -> ModelTemplates.FLAT_ITEM.create(
                createIdentifier("item/pattern/" + type.getSerializedName()),
                TextureMapping.layer0(texture(createIdentifier("item/pattern/" + type.getSerializedName()))),
                modelOutput
            ), (a, b) -> a, LinkedHashMap::new));
        itemModelOutput.accept(Items.INSTANCE.getPattern(), ItemModelUtils.plainModel(empty));
    }

    private void registerConfigurationCard(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier activeItemModel = ModelTemplates.FLAT_ITEM.create(
            createIdentifier("item/configuration_card/active"),
            TextureMapping.layer0(texture(createIdentifier("item/configuration_card/active"))),
            modelOutput
        );
        final Identifier inactiveItemModel = ModelTemplates.FLAT_ITEM.create(
            createIdentifier("item/configuration_card/inactive"),
            TextureMapping.layer0(texture(createIdentifier("item/configuration_card/inactive"))),
            modelOutput
        );
        itemModelOutput.accept(Items.INSTANCE.getConfigurationCard(),
            ItemModelUtils.plainModel(inactiveItemModel));
    }

    private void registerNetworkCard(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier activeItemModel = ModelTemplates.FLAT_ITEM.create(
            createIdentifier("item/network_card/active"),
            TextureMapping.layer0(texture(createIdentifier("item/network_card/active"))),
            modelOutput
        );
        final Identifier inactiveItemModel = ModelTemplates.FLAT_ITEM.create(
            createIdentifier("item/network_card/inactive"),
            TextureMapping.layer0(texture(createIdentifier("item/network_card/inactive"))),
            modelOutput
        );
        itemModelOutput.accept(Items.INSTANCE.getNetworkCard(),
            ItemModelUtils.plainModel(inactiveItemModel));
    }

    private void registerSecurityCard(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput) {
        final Identifier activeItemModel = ModelTemplates.FLAT_ITEM.create(
            createIdentifier("item/security_card/active"),
            TextureMapping.layer0(texture(createIdentifier("item/security_card/active"))),
            modelOutput
        );
        final Identifier inactiveItemModel = ModelTemplates.FLAT_ITEM.create(
            createIdentifier("item/security_card/inactive"),
            TextureMapping.layer0(texture(createIdentifier("item/security_card/inactive"))),
            modelOutput
        );
        itemModelOutput.accept(Items.INSTANCE.getSecurityCard(),
            ItemModelUtils.plainModel(inactiveItemModel));
    }

    private void registerNetworkBound(final Consumer<BlockModelDefinitionGenerator> blockStateOutput, final ItemModelOutput itemModelOutput, final BiConsumer<Identifier, ModelInstance> modelOutput, final String name, final Item... items) {
        final Identifier activeItemModel = ModelTemplates.FLAT_ITEM.create(
            createIdentifier("item/" + name + "/active"),
            TextureMapping.layer0(texture(createIdentifier("item/" + name + "/active"))),
            modelOutput
        );
        final Identifier inactiveItemModel = ModelTemplates.FLAT_ITEM.create(
            createIdentifier("item/" + name + "/inactive"),
            TextureMapping.layer0(texture(createIdentifier("item/" + name + "/inactive"))),
            modelOutput
        );
        Arrays.stream(items).forEach(item -> itemModelOutput.accept(item,
            ItemModelUtils.plainModel(inactiveItemModel)));
    }


    private static Material texture(final Identifier location) {
        return new Material(location);
    }
}
