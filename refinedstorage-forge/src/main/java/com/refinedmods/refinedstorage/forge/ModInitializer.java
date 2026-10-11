package com.refinedmods.refinedstorage.forge;

import com.refinedmods.refinedstorage.common.AbstractClientModInitializer;
import com.refinedmods.refinedstorage.common.AbstractModInitializer;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.PlatformProxy;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import com.refinedmods.refinedstorage.common.autocrafting.monitor.WirelessAutocraftingMonitorItem;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.content.BlockEntityProvider;
import com.refinedmods.refinedstorage.common.content.BlockEntityProviders;
import com.refinedmods.refinedstorage.common.content.BlockEntityTypeFactory;
import com.refinedmods.refinedstorage.common.content.Blocks;
import com.refinedmods.refinedstorage.common.content.ContentIds;
import com.refinedmods.refinedstorage.common.content.ContentNames;
import com.refinedmods.refinedstorage.common.content.CreativeModeTabItems;
import com.refinedmods.refinedstorage.common.content.ExtendedMenuTypeFactory;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.content.MenuTypeFactory;
import com.refinedmods.refinedstorage.common.content.RegistryCallback;
import com.refinedmods.refinedstorage.common.controller.ControllerBlockItem;
import com.refinedmods.refinedstorage.common.grid.WirelessGridItem;
import com.refinedmods.refinedstorage.common.security.FallbackSecurityCardItem;
import com.refinedmods.refinedstorage.common.security.SecurityCardItem;
import com.refinedmods.refinedstorage.common.storage.FluidStorageVariant;
import com.refinedmods.refinedstorage.common.storage.ItemStorageVariant;
import com.refinedmods.refinedstorage.common.storage.diskinterface.AbstractDiskInterfaceBlockEntity;
import com.refinedmods.refinedstorage.common.storage.portablegrid.PortableGridBlockItem;
import com.refinedmods.refinedstorage.common.storage.portablegrid.PortableGridType;
import com.refinedmods.refinedstorage.common.support.AbstractBaseBlock;
import com.refinedmods.refinedstorage.common.support.packet.PacketHandler;
import com.refinedmods.refinedstorage.common.support.packet.c2s.AutocrafterNameChangePacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.AutocraftingMonitorCancelAllPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.AutocraftingMonitorCancelPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.AutocraftingPreviewCancelRequestPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.AutocraftingPreviewMaxAmountRequestPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.AutocraftingPreviewRequestPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.AutocraftingRequestPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.CraftingGridClearPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.CraftingGridRecipeTransferPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.FilterSlotChangePacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.GridAutocraftingTasksSubscriptionPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.GridExtractPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.GridInsertPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.GridScrollPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.PatternGridAllowedAlternativesChangePacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.PatternGridClearPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.PatternGridCraftingRecipeTransferPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.PatternGridCreatePatternPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.PatternGridProcessingRecipeTransferPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.PatternGridSmithingTableRecipeTransferPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.PatternGridStonecutterRecipeTransferPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.PropertyChangePacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.ResourceFilterSlotChangePacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.ResourceSlotAmountChangePacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.ResourceSlotChangePacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.SecurityCardBoundPlayerPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.SecurityCardPermissionPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.SecurityCardResetPermissionPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.SingleAmountChangePacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.StorageInfoRequestPacket;
import com.refinedmods.refinedstorage.common.support.packet.c2s.UseSlotReferencedItemPacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.AutocrafterLockedUpdatePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.AutocrafterManagerActivePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.AutocrafterNameUpdatePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.AutocraftingMonitorActivePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.AutocraftingMonitorTaskAddedPacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.AutocraftingMonitorTaskRemovedPacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.AutocraftingMonitorTaskStatusChangedPacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.AutocraftingPreviewCancelResponsePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.AutocraftingPreviewMaxAmountResponsePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.AutocraftingPreviewResponsePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.AutocraftingResponsePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.AutocraftingTaskCompletedPacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.AutocraftingTreePreviewResponsePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.EnergyInfoPacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.ExportingIndicatorUpdatePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.GridActivePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.GridAutocraftingTaskAddedPacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.GridAutocraftingTaskRemovedPacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.GridAutocraftingTasksUpdatePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.GridClearPacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.GridUpdatePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.MessagePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.NetworkTransmitterStatusPacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.PatternGridAllowedAlternativesUpdatePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.ResourceSlotUpdatePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.StorageInfoResponsePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.WirelessTransmitterDataPacket;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.upgrade.RegulatorUpgradeItem;
import com.refinedmods.refinedstorage.common.util.IdentifierUtil;
import com.refinedmods.refinedstorage.common.util.ServerListener;
import com.refinedmods.refinedstorage.api.network.energy.EnergyStorage;
import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.controller.ControllerBlockEntity;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridBlockEntity;
import com.refinedmods.refinedstorage.common.iface.InterfaceBlockEntity;
import com.refinedmods.refinedstorage.common.storage.diskdrive.AbstractDiskDriveBlockEntity;
import com.refinedmods.refinedstorage.common.storage.portablegrid.AbstractPortableGridBlockEntity;
import com.refinedmods.refinedstorage.forge.api.RefinedStorageForgeApi;
import com.refinedmods.refinedstorage.forge.api.RefinedStorageForgeApiProxy;
import com.refinedmods.refinedstorage.forge.autocrafting.FluidHandlerExternalPatternSinkStrategyFactoryImpl;
import com.refinedmods.refinedstorage.forge.autocrafting.ItemHandlerExternalPatternSinkStrategyFactoryImpl;
import com.refinedmods.refinedstorage.forge.datagen.DataGenerators;
import com.refinedmods.refinedstorage.forge.constructordestructor.ForgeConstructorBlockEntity;
import com.refinedmods.refinedstorage.forge.constructordestructor.ForgeDestructorBlockEntity;
import com.refinedmods.refinedstorage.forge.debug.DebugStickItem;
import com.refinedmods.refinedstorage.forge.exporter.ForgeExporterBlockEntity;
import com.refinedmods.refinedstorage.forge.exporter.FluidHandlerExporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.forge.exporter.ItemHandlerExporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.forge.grid.strategy.FluidGridExtractionStrategy;
import com.refinedmods.refinedstorage.forge.grid.strategy.FluidGridInsertionStrategy;
import com.refinedmods.refinedstorage.forge.grid.strategy.ItemGridExtractionStrategy;
import com.refinedmods.refinedstorage.forge.grid.strategy.ItemGridScrollingStrategy;
import com.refinedmods.refinedstorage.forge.grid.view.ForgeFluidGridResourceType;
import com.refinedmods.refinedstorage.forge.grid.view.ForgeItemGridResourceType;
import com.refinedmods.refinedstorage.forge.importer.ForgeImporterBlockEntity;
import com.refinedmods.refinedstorage.forge.importer.FluidHandlerImporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.forge.importer.ItemHandlerImporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.forge.networking.ForgeCableBlockEntity;
import com.refinedmods.refinedstorage.forge.storage.diskdrive.ForgeDiskDriveBlockEntity;
import com.refinedmods.refinedstorage.forge.storage.diskinterface.ForgeDiskInterfaceBlockEntity;
import com.refinedmods.refinedstorage.forge.storage.externalstorage.ForgeExternalStorageBlockEntity;
import com.refinedmods.refinedstorage.forge.storage.externalstorage.FluidHandlerPlatformExternalStorageProviderFactory;
import com.refinedmods.refinedstorage.forge.storage.externalstorage.ItemHandlerPlatformExternalStorageProviderFactory;
import com.refinedmods.refinedstorage.forge.storage.portablegrid.ForgePortableGridBlockEntity;
import com.refinedmods.refinedstorage.forge.support.energy.EnergyStorageEnergyHandlerAdapter;
import com.refinedmods.refinedstorage.forge.support.energy.StackEnergyItemContext;
import com.refinedmods.refinedstorage.forge.support.inventory.DiskInterfaceItemHandler;
import com.refinedmods.refinedstorage.forge.support.resource.ResourceContainerFluidHandler;


import java.util.Arrays;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuildable;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.payload.PayloadConnection;
import net.minecraftforge.network.payload.PayloadFlow;
import net.minecraftforge.network.payload.PayloadProtocol;

import net.minecraftforge.registries.DeferredRegister;

import static com.refinedmods.refinedstorage.common.content.ContentIds.CREATIVE_PORTABLE_GRID;
import static com.refinedmods.refinedstorage.common.content.ContentIds.CREATIVE_WIRELESS_AUTOCRAFTING_MONITOR;
import static com.refinedmods.refinedstorage.common.content.ContentIds.CREATIVE_WIRELESS_GRID;
import static com.refinedmods.refinedstorage.common.content.ContentIds.FALLBACK_SECURITY_CARD;
import static com.refinedmods.refinedstorage.common.content.ContentIds.PORTABLE_GRID;
import static com.refinedmods.refinedstorage.common.content.ContentIds.REGULATOR_UPGRADE;
import static com.refinedmods.refinedstorage.common.content.ContentIds.SECURITY_CARD;
import static com.refinedmods.refinedstorage.common.content.ContentIds.WIRELESS_AUTOCRAFTING_MONITOR;
import static com.refinedmods.refinedstorage.common.content.ContentIds.WIRELESS_GRID;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

@Mod(IdentifierUtil.MOD_ID)
public class ModInitializer extends AbstractModInitializer {
    private static final BlockEntityProviders BLOCK_ENTITY_PROVIDERS = new BlockEntityProviders(
        ForgeDiskDriveBlockEntity::new,
        (pos, state) -> new ForgePortableGridBlockEntity(PortableGridType.NORMAL, pos, state),
        (pos, state) -> new ForgePortableGridBlockEntity(PortableGridType.CREATIVE, pos, state),
        ForgeDiskInterfaceBlockEntity::new,
        ForgeCableBlockEntity::new,
        ForgeExternalStorageBlockEntity::new,
        ForgeExporterBlockEntity::new,
        ForgeImporterBlockEntity::new,
        ForgeConstructorBlockEntity::new,
        ForgeDestructorBlockEntity::new
    );

    public static final int CHANNEL_PROTOCOL_VERSION = 1;
    public static Channel<CustomPacketPayload> CHANNEL;

    private final DeferredRegister<Block> blockRegistry =
        DeferredRegister.create(Registries.BLOCK, IdentifierUtil.MOD_ID);
    private final DeferredRegister<Item> itemRegistry =
        DeferredRegister.create(Registries.ITEM, IdentifierUtil.MOD_ID);
    private final DeferredRegister<BlockEntityType<?>> blockEntityTypeRegistry =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, IdentifierUtil.MOD_ID);
    private final DeferredRegister<MenuType<?>> menuTypeRegistry =
        DeferredRegister.create(Registries.MENU, IdentifierUtil.MOD_ID);
    private final DeferredRegister<SoundEvent> soundEventRegistry =
        DeferredRegister.create(Registries.SOUND_EVENT, IdentifierUtil.MOD_ID);
    private final DeferredRegister<RecipeSerializer<?>> recipeSerializerRegistry =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, IdentifierUtil.MOD_ID);
    private final DeferredRegister<DataComponentType<?>> dataComponentTypeRegistry =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, IdentifierUtil.MOD_ID);
    private final DeferredRegister<CreativeModeTab> creativeModeTabRegistry =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IdentifierUtil.MOD_ID);
    private final DeferredRegister<MapCodec<? extends LootItemFunction>> lootFunctionTypeRegistry =
        DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, IdentifierUtil.MOD_ID);

    public ModInitializer(final FMLJavaModLoadingContext context) {
        final BusGroup modBusGroup = context.getModBusGroup();

        PlatformProxy.loadPlatform(new PlatformImpl(context));
        initializePlatformApi();
        ((RefinedStorageForgeApiProxy) RefinedStorageForgeApi.INSTANCE).setDelegate(
            new RefinedStorageForgeApiImpl());
        registerGridResourceTypes();
        registerAdditionalGridInsertionStrategyFactories();
        registerGridExtractionStrategyFactories();
        registerGridScrollingStrategyFactories();
        registerImporterTransferStrategyFactories();
        registerExporterTransferStrategyFactories();
        registerExternalStorageProviderFactories();
        registerExternalPatternSinkStrategyFactories();
        buildChannel();
        registerContent(modBusGroup);
        registerSounds(modBusGroup);
        registerRecipeSerializers(modBusGroup);

        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::onCommonSetup);
        GatherDataEvent.getBus(modBusGroup).addListener(DataGenerators::onGatherData);

        TickEvent.ServerTickEvent.Pre.BUS.addListener(this::onServerTick);
        ServerStartingEvent.BUS.addListener(this::onServerStarting);
        ServerStoppedEvent.BUS.addListener(this::onServerStopped);
        PlayerInteractEvent.RightClickBlock.BUS.addListener(this::registerWrenchingEvent);
        BlockEvent.BreakEvent.BUS.addListener(this::registerSecurityBlockBreakEvent);
    }

    private void registerGridResourceTypes() {
        RefinedStorageApi.INSTANCE.getGridResourceTypeRegistry()
            .register(createIdentifier("item"), ForgeItemGridResourceType.INSTANCE);
        RefinedStorageApi.INSTANCE.getGridResourceTypeRegistry()
            .register(createIdentifier("fluid"), ForgeFluidGridResourceType.INSTANCE);
    }

    private void registerAdditionalGridInsertionStrategyFactories() {
        RefinedStorageApi.INSTANCE.addGridInsertionStrategyFactory(FluidGridInsertionStrategy::new);
    }

    private void registerGridExtractionStrategyFactories() {
        RefinedStorageApi.INSTANCE.addGridExtractionStrategyFactory(ItemGridExtractionStrategy::new);
        RefinedStorageApi.INSTANCE.addGridExtractionStrategyFactory(FluidGridExtractionStrategy::new);
    }

    private void registerGridScrollingStrategyFactories() {
        RefinedStorageApi.INSTANCE.addGridScrollingStrategyFactory(ItemGridScrollingStrategy::new);
    }

    private void registerImporterTransferStrategyFactories() {
        RefinedStorageApi.INSTANCE.getImporterTransferStrategyRegistry().register(
            createIdentifier("item"),
            new ItemHandlerImporterTransferStrategyFactory(1)
        );
        RefinedStorageApi.INSTANCE.getImporterTransferStrategyRegistry().register(
            createIdentifier("fluid"),
            new FluidHandlerImporterTransferStrategyFactory(FluidType.BUCKET_VOLUME)
        );
    }

    private void registerExporterTransferStrategyFactories() {
        RefinedStorageApi.INSTANCE.getExporterTransferStrategyRegistry().register(
            createIdentifier("item"),
            new ItemHandlerExporterTransferStrategyFactory(1, ItemResource.class)
        );
        RefinedStorageApi.INSTANCE.getExporterTransferStrategyRegistry().register(
            createIdentifier("fluid"),
            new FluidHandlerExporterTransferStrategyFactory(
                FluidType.BUCKET_VOLUME,
                FluidResource.class
            )
        );
    }

    private void registerExternalStorageProviderFactories() {
        RefinedStorageApi.INSTANCE.addExternalStorageProviderFactory(
            new ItemHandlerPlatformExternalStorageProviderFactory()
        );
        RefinedStorageApi.INSTANCE.addExternalStorageProviderFactory(
            new FluidHandlerPlatformExternalStorageProviderFactory()
        );
    }

    private void registerExternalPatternSinkStrategyFactories() {
        RefinedStorageForgeApi.INSTANCE.addResourceHandlerExternalPatternSinkStrategyFactory(
            ItemResource.class,
            new ItemHandlerExternalPatternSinkStrategyFactoryImpl()
        );
        RefinedStorageForgeApi.INSTANCE.addResourceHandlerExternalPatternSinkStrategyFactory(
            FluidResource.class,
            new FluidHandlerExternalPatternSinkStrategyFactoryImpl()
        );
    }

    private void registerContent(final BusGroup modBusGroup) {
        registerBlocks(modBusGroup);
        registerItems(modBusGroup);
        registerBlockEntities(modBusGroup);
        registerMenus(modBusGroup);
        registerDataComponents(modBusGroup);
        registerCreativeModeTabs(modBusGroup);
        registerLootFunctions(modBusGroup);
    }

    private void registerBlocks(final BusGroup modBusGroup) {
        registerBlocks(new ForgeRegistryCallback<>(blockRegistry), BLOCK_ENTITY_PROVIDERS);
        blockRegistry.register(modBusGroup);
    }

    private void registerItems(final BusGroup modBusGroup) {
        final RegistryCallback<Item> callback = new ForgeRegistryCallback<>(itemRegistry);
        registerItems(callback);
        Items.INSTANCE.setDebugStick(callback.register(ContentIds.DEBUG_STICK, DebugStickItem::new));
        registerCustomItems(callback);
        itemRegistry.register(modBusGroup);
    }

    private void registerCustomItems(final RegistryCallback<Item> callback) {
        Items.INSTANCE.setRegulatorUpgrade(callback.register(REGULATOR_UPGRADE, () -> new RegulatorUpgradeItem(
            RefinedStorageApi.INSTANCE.getUpgradeRegistry()
        ) {
            @Override
            public boolean shouldCauseReequipAnimation(final ItemStack oldStack,
                                                       final ItemStack newStack,
                                                       final boolean slotChanged) {
                return AbstractModInitializer.allowComponentsUpdateAnimation(oldStack, newStack);
            }
        }));
        Items.INSTANCE.setWirelessGrid(callback.register(WIRELESS_GRID, () -> new WirelessGridItem(false) {
            @Override
            public boolean shouldCauseReequipAnimation(final ItemStack oldStack,
                                                       final ItemStack newStack,
                                                       final boolean slotChanged) {
                return AbstractModInitializer.allowComponentsUpdateAnimation(oldStack, newStack);
            }
        }));
        Items.INSTANCE.setCreativeWirelessGrid(callback.register(
            CREATIVE_WIRELESS_GRID,
            () -> new WirelessGridItem(true) {
                @Override
                public boolean shouldCauseReequipAnimation(final ItemStack oldStack,
                                                           final ItemStack newStack,
                                                           final boolean slotChanged) {
                    return AbstractModInitializer.allowComponentsUpdateAnimation(oldStack, newStack);
                }
            }
        ));
        Items.INSTANCE.setPortableGrid(callback.register(PORTABLE_GRID, () -> new PortableGridBlockItem(
            Blocks.INSTANCE.getPortableGrid(), PortableGridType.NORMAL
        ) {
            @Override
            public boolean shouldCauseReequipAnimation(final ItemStack oldStack,
                                                       final ItemStack newStack,
                                                       final boolean slotChanged) {
                return AbstractModInitializer.allowComponentsUpdateAnimation(oldStack, newStack);
            }
        }));
        Items.INSTANCE.setCreativePortableGrid(callback.register(
            CREATIVE_PORTABLE_GRID,
            () -> new PortableGridBlockItem(Blocks.INSTANCE.getCreativePortableGrid(), PortableGridType.CREATIVE) {
                @Override
                public boolean shouldCauseReequipAnimation(final ItemStack oldStack,
                                                           final ItemStack newStack,
                                                           final boolean slotChanged) {
                    return AbstractModInitializer.allowComponentsUpdateAnimation(oldStack, newStack);
                }
            }
        ));
        Items.INSTANCE.setSecurityCard(callback.register(SECURITY_CARD, () -> new SecurityCardItem() {
            @Override
            public boolean shouldCauseReequipAnimation(final ItemStack oldStack,
                                                       final ItemStack newStack,
                                                       final boolean slotChanged) {
                return AbstractModInitializer.allowComponentsUpdateAnimation(oldStack, newStack);
            }
        }));
        Items.INSTANCE.setFallbackSecurityCard(callback.register(
            FALLBACK_SECURITY_CARD,
            () -> new FallbackSecurityCardItem() {
                @Override
                public boolean shouldCauseReequipAnimation(final ItemStack oldStack,
                                                           final ItemStack newStack,
                                                           final boolean slotChanged) {
                    return AbstractModInitializer.allowComponentsUpdateAnimation(oldStack, newStack);
                }
            }
        ));
        Items.INSTANCE.setWirelessAutocraftingMonitor(callback.register(
            WIRELESS_AUTOCRAFTING_MONITOR,
            () -> new WirelessAutocraftingMonitorItem(false) {
                @Override
                public boolean shouldCauseReequipAnimation(final ItemStack oldStack,
                                                           final ItemStack newStack,
                                                           final boolean slotChanged) {
                    return AbstractModInitializer.allowComponentsUpdateAnimation(oldStack, newStack);
                }
            }
        ));
        Items.INSTANCE.setCreativeWirelessAutocraftingMonitor(callback.register(
            CREATIVE_WIRELESS_AUTOCRAFTING_MONITOR,
            () -> new WirelessAutocraftingMonitorItem(true) {
                @Override
                public boolean shouldCauseReequipAnimation(final ItemStack oldStack,
                                                           final ItemStack newStack,
                                                           final boolean slotChanged) {
                    return AbstractModInitializer.allowComponentsUpdateAnimation(oldStack, newStack);
                }
            }
        ));
    }

    private void registerBlockEntities(final BusGroup modBusGroup) {
        registerBlockEntities(
            new ForgeRegistryCallback<>(blockEntityTypeRegistry),
            new BlockEntityTypeFactory() {
                @Override
                public <T extends BlockEntity> BlockEntityType<T> create(final BlockEntityProvider<T> factory,
                                                                         final Block... allowedBlocks) {
                    return new BlockEntityType<>(factory::create, new HashSet<>(Arrays.asList(allowedBlocks)));
                }
            },
            BLOCK_ENTITY_PROVIDERS
        );
        blockEntityTypeRegistry.register(modBusGroup);
    }

    private void registerMenus(final BusGroup modBusGroup) {
        registerMenus(new ForgeRegistryCallback<>(menuTypeRegistry), new MenuTypeFactory() {
            @Override
            public <T extends AbstractContainerMenu> MenuType<T> create(final MenuSupplier<T> supplier) {
                return new MenuType<>(supplier::create, FeatureFlags.DEFAULT_FLAGS);
            }
        }, new ExtendedMenuTypeFactory() {
            @Override
            public <T extends AbstractContainerMenu, D> MenuType<T> create(final MenuSupplier<T, D> supplier,
                                                                           final StreamCodec<RegistryFriendlyByteBuf, D>
                                                                               streamCodec) {
                return IForgeMenuType.create((syncId, inventory, buf) -> {
                    final D data = streamCodec.decode((RegistryFriendlyByteBuf) buf);
                    return supplier.create(syncId, inventory, data);
                });
            }
        });
        menuTypeRegistry.register(modBusGroup);
    }

    private void registerDataComponents(final BusGroup modBusGroup) {
        final RegistryCallback<DataComponentType<?>> callback = new ForgeRegistryCallback<>(dataComponentTypeRegistry);
        registerDataComponents(callback);
        dataComponentTypeRegistry.register(modBusGroup);
    }

    private void attachCapabilities() {
        AttachCapabilitiesEvent.BlockEntities.BUS.addListener(this::attachBlockEntityCapabilities);
        AttachCapabilitiesEvent.ItemStacks.BUS.addListener(this::attachItemCapabilities);
    }

    private void attachBlockEntityCapabilities(final AttachCapabilitiesEvent.BlockEntities event) {
        final BlockEntity blockEntity = event.getObject();
        if (blockEntity instanceof AbstractNetworkNodeContainerBlockEntity<?> container) {
            attach(event, RefinedStorageForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                createIdentifier("network_node_container"), container.getContainerProvider());
        }
        if (blockEntity.getType() == BlockEntities.INSTANCE.getDiskDrive()) {
            attach(event, ForgeCapabilities.ITEM_HANDLER, createIdentifier("disk_drive"),
                new InvWrapper(((AbstractDiskDriveBlockEntity) blockEntity).getDiskInventory()));
        } else if (blockEntity.getType() == BlockEntities.INSTANCE.getInterface()) {
            final InterfaceBlockEntity iface = (InterfaceBlockEntity) blockEntity;
            attach(event, ForgeCapabilities.ITEM_HANDLER, createIdentifier("interface_items"),
                new InvWrapper(iface.getExportedResourcesAsContainer()));
            attach(event, ForgeCapabilities.FLUID_HANDLER, createIdentifier("interface_fluids"),
                new ResourceContainerFluidHandler(iface.getExportedResources()));
        } else if (blockEntity.getType() == BlockEntities.INSTANCE.getPatternGrid()) {
            attach(event, ForgeCapabilities.ITEM_HANDLER, createIdentifier("pattern_grid"),
                new InvWrapper(((PatternGridBlockEntity) blockEntity).getPatternInput()));
        } else if (blockEntity.getType() == BlockEntities.INSTANCE.getController()
            || blockEntity.getType() == BlockEntities.INSTANCE.getCreativeController()) {
            attach(event, ForgeCapabilities.ENERGY, createIdentifier("controller_energy"),
                new EnergyStorageEnergyHandlerAdapter(
                    ((ControllerBlockEntity) blockEntity).getEnergyStorage()));
        } else if (blockEntity.getType() == BlockEntities.INSTANCE.getPortableGrid()
            || blockEntity.getType() == BlockEntities.INSTANCE.getCreativePortableGrid()) {
            attach(event, ForgeCapabilities.ENERGY, createIdentifier("portable_grid_energy"),
                new EnergyStorageEnergyHandlerAdapter(
                    ((AbstractPortableGridBlockEntity) blockEntity).getEnergyStorage()));
        } else if (blockEntity.getType() == BlockEntities.INSTANCE.getDiskInterface()) {
            attach(event, ForgeCapabilities.ITEM_HANDLER, createIdentifier("disk_interface"),
                new DiskInterfaceItemHandler(
                    ((ForgeDiskInterfaceBlockEntity) blockEntity).getDiskInventory()));
        }
    }

    private void attachItemCapabilities(final AttachCapabilitiesEvent.ItemStacks event) {
        final ItemStack stack = event.getObject();
        final Item item = stack.getItem();
        if (item == Items.INSTANCE.getWirelessGrid()) {
            attachItemEnergy(event, WirelessGridItem.createEnergyStorage(stack, new StackEnergyItemContext(stack)));
        } else if (item == Items.INSTANCE.getWirelessAutocraftingMonitor()) {
            attachItemEnergy(event, WirelessAutocraftingMonitorItem.createEnergyStorage(
                stack, new StackEnergyItemContext(stack)));
        } else if (item == Items.INSTANCE.getPortableGrid()) {
            attachItemEnergy(event, PortableGridBlockItem.createEnergyStorage(stack,
                new StackEnergyItemContext(stack)));
        } else {
            for (final Supplier<ControllerBlockItem> controllerItem : Items.INSTANCE.getControllers()) {
                if (item == controllerItem.get()) {
                    attachItemEnergy(event, ControllerBlockItem.createEnergyStorage(
                        stack, new StackEnergyItemContext(stack)));
                    return;
                }
            }
        }
    }

    private static void attachItemEnergy(final AttachCapabilitiesEvent.ItemStacks event,
                                         final EnergyStorage energyStorage) {
        event.addCapability(createIdentifier("energy"), new ICapabilityProvider() {
            @Override
            public <R> LazyOptional<R> getCapability(final Capability<R> queried, final Direction side) {
                return ForgeCapabilities.ENERGY.orEmpty(
                    queried,
                    LazyOptional.of(() -> new EnergyStorageEnergyHandlerAdapter(energyStorage))
                );
            }
        });
    }

    private static <T> void attach(final AttachCapabilitiesEvent.BlockEntities event,
                                   final Capability<T> capability,
                                   final Identifier identifier,
                                   final T value) {
        event.addCapability(identifier, new ICapabilityProvider() {
            @Override
            public <R> LazyOptional<R> getCapability(final Capability<R> queried, final Direction side) {
                return capability.orEmpty(queried, LazyOptional.of(() -> value));
            }
        });
    }
    private void registerSounds(final BusGroup modBusGroup) {
        registerSounds(new ForgeRegistryCallback<>(soundEventRegistry));
        soundEventRegistry.register(modBusGroup);
    }

    private void registerRecipeSerializers(final BusGroup modBusGroup) {
        registerRecipeSerializers(new ForgeRegistryCallback<>(recipeSerializerRegistry));
        recipeSerializerRegistry.register(modBusGroup);
    }

    private void registerCreativeModeTabs(final BusGroup modBusGroup) {
        creativeModeTabRegistry.register(
            RefinedStorageApi.INSTANCE.getCreativeModeTabId().getPath(),
            () -> CreativeModeTab.builder()
                .title(ContentNames.MOD)
                .icon(() -> new ItemStack(Blocks.INSTANCE.getCreativeController().getDefault()))
                .displayItems((params, output) -> {
                    CreativeModeTabItems.append(output::accept);
                    if (Platform.INSTANCE.getConfig().isDebug()) {
                        output.accept(Items.INSTANCE.getDebugStick());
                    }
                })
                .build()
        );
        creativeModeTabRegistry.register(
            RefinedStorageApi.INSTANCE.getColoredCreativeModeTabId().getPath(),
            () -> CreativeModeTab.builder()
                .title(ContentNames.MOD_COLORIZED)
                .icon(() -> new ItemStack(Blocks.INSTANCE.getCreativeController().get(DyeColor.LIME)))
                .displayItems((params, output) -> CreativeModeTabItems.appendColoredVariants(output::accept))
                .build()
        );
        creativeModeTabRegistry.register(modBusGroup);
    }

    private void registerLootFunctions(final BusGroup modBusGroup) {
        registerLootFunctions(new ForgeRegistryCallback<>(lootFunctionTypeRegistry));
        lootFunctionTypeRegistry.register(modBusGroup);
    }

    public void onCommonSetup(final FMLCommonSetupEvent e) {
        registerUpgradeMappings();
    }

    public boolean registerWrenchingEvent(final PlayerInteractEvent.RightClickBlock e) {
        final Level level = e.getLevel();
        final BlockState state = level.getBlockState(e.getHitVec().getBlockPos());
        if (!(state.getBlock() instanceof AbstractBaseBlock block)) {
            return false;
        }
        return block.tryUseWrench(state, level, e.getHitVec(), e.getEntity(), e.getHand()).or(() ->
            block.tryUpdateColor(
                state,
                level,
                e.getHitVec().getBlockPos(),
                e.getEntity(),
                e.getHand()
            )).map(result -> {
                e.setCancellationResult(result);
                return true;
            }).orElse(false);
    }

    public boolean registerSecurityBlockBreakEvent(final BlockEvent.BreakEvent e) {
        if (!(e.getLevel() instanceof Level level)) {
            return false;
        }
        if (level.isClientSide()) {
            return false;
        }
        final NetworkNodeContainerProvider provider = Platform.INSTANCE.getContainerProvider(level, e.getPos(), null);
        final Player player = e.getPlayer();
        if (provider != null && player instanceof ServerPlayer serverPlayer && !provider.canBuild(serverPlayer)) {
            RefinedStorageApi.INSTANCE.sendNoPermissionMessage(
                serverPlayer,
                createTranslation("misc", "no_permission.build.break", e.getState().getBlock().getName())
            );
            e.setResult(Result.DENY);
            return true;
        }
        return false;
    }

    private static void buildChannel() {
        final PayloadConnection<CustomPacketPayload> connection = ChannelBuilder
            .named(createIdentifier("main"))
            .networkProtocolVersion(CHANNEL_PROTOCOL_VERSION)
            .payloadChannel();
        final PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> play = connection.play();
        registerServerToClientPackets(play.clientbound());
        registerClientToServerPackets(play.serverbound());
        CHANNEL = ((ChannelBuildable<CustomPacketPayload>) play).build();
    }
    private static void registerServerToClientPackets(final PayloadFlow<RegistryFriendlyByteBuf, CustomPacketPayload> clientbound) {
        clientbound.addMain(
            EnergyInfoPacket.PACKET_TYPE,
            EnergyInfoPacket.STREAM_CODEC,
            wrapHandler(EnergyInfoPacket::handle)
        );
        clientbound.addMain(
            GridActivePacket.PACKET_TYPE,
            GridActivePacket.STREAM_CODEC,
            wrapHandler(GridActivePacket::handle)
        );
        clientbound.addMain(
            AutocrafterManagerActivePacket.PACKET_TYPE,
            AutocrafterManagerActivePacket.STREAM_CODEC,
            wrapHandler(AutocrafterManagerActivePacket::handle)
        );
        clientbound.addMain(
            GridClearPacket.PACKET_TYPE,
            GridClearPacket.STREAM_CODEC,
            wrapHandler((packet, ctx) -> GridClearPacket.handle(ctx))
        );
        clientbound.addMain(
            GridUpdatePacket.PACKET_TYPE,
            GridUpdatePacket.STREAM_CODEC,
            wrapHandler(GridUpdatePacket::handle)
        );
        clientbound.addMain(
            NetworkTransmitterStatusPacket.PACKET_TYPE,
            NetworkTransmitterStatusPacket.STREAM_CODEC,
            wrapHandler(NetworkTransmitterStatusPacket::handle)
        );
        clientbound.addMain(
            ResourceSlotUpdatePacket.PACKET_TYPE,
            ResourceSlotUpdatePacket.STREAM_CODEC,
            wrapHandler(ResourceSlotUpdatePacket::handle)
        );
        clientbound.addMain(
            StorageInfoResponsePacket.PACKET_TYPE,
            StorageInfoResponsePacket.STREAM_CODEC,
            wrapHandler((packet, ctx) -> StorageInfoResponsePacket.handle(packet))
        );
        clientbound.addMain(
            WirelessTransmitterDataPacket.PACKET_TYPE,
            WirelessTransmitterDataPacket.STREAM_CODEC,
            wrapHandler(WirelessTransmitterDataPacket::handle)
        );
        clientbound.addMain(
            MessagePacket.PACKET_TYPE,
            MessagePacket.STREAM_CODEC,
            wrapHandler((packet, ctx) -> MessagePacket.handle(packet))
        );
        clientbound.addMain(
            PatternGridAllowedAlternativesUpdatePacket.PACKET_TYPE,
            PatternGridAllowedAlternativesUpdatePacket.STREAM_CODEC,
            wrapHandler(PatternGridAllowedAlternativesUpdatePacket::handle)
        );
        clientbound.addMain(
            AutocrafterNameUpdatePacket.PACKET_TYPE,
            AutocrafterNameUpdatePacket.STREAM_CODEC,
            wrapHandler(AutocrafterNameUpdatePacket::handle)
        );
        clientbound.addMain(
            AutocrafterLockedUpdatePacket.PACKET_TYPE,
            AutocrafterLockedUpdatePacket.STREAM_CODEC,
            wrapHandler(AutocrafterLockedUpdatePacket::handle)
        );
        clientbound.addMain(
            AutocraftingPreviewResponsePacket.PACKET_TYPE,
            AutocraftingPreviewResponsePacket.STREAM_CODEC,
            wrapHandler((packet, ctx) -> AutocraftingPreviewResponsePacket.handle(packet))
        );
        clientbound.addMain(
            AutocraftingTreePreviewResponsePacket.PACKET_TYPE,
            AutocraftingTreePreviewResponsePacket.STREAM_CODEC,
            wrapHandler((packet, ctx) -> AutocraftingTreePreviewResponsePacket.handle(packet))
        );
        clientbound.addMain(
            AutocraftingPreviewCancelResponsePacket.PACKET_TYPE,
            AutocraftingPreviewCancelResponsePacket.STREAM_CODEC,
            wrapHandler((packet, ctx) -> AutocraftingPreviewCancelResponsePacket.handle())
        );
        clientbound.addMain(
            AutocraftingPreviewMaxAmountResponsePacket.PACKET_TYPE,
            AutocraftingPreviewMaxAmountResponsePacket.STREAM_CODEC,
            wrapHandler((packet, ctx) -> AutocraftingPreviewMaxAmountResponsePacket.handle(packet))
        );
        clientbound.addMain(
            AutocraftingResponsePacket.PACKET_TYPE,
            AutocraftingResponsePacket.STREAM_CODEC,
            wrapHandler((packet, ctx) -> AutocraftingResponsePacket.handle(packet))
        );
        clientbound.addMain(
            AutocraftingMonitorTaskAddedPacket.PACKET_TYPE,
            AutocraftingMonitorTaskAddedPacket.STREAM_CODEC,
            wrapHandler(AutocraftingMonitorTaskAddedPacket::handle)
        );
        clientbound.addMain(
            AutocraftingMonitorTaskRemovedPacket.PACKET_TYPE,
            AutocraftingMonitorTaskRemovedPacket.STREAM_CODEC,
            wrapHandler(AutocraftingMonitorTaskRemovedPacket::handle)
        );
        clientbound.addMain(
            AutocraftingMonitorTaskStatusChangedPacket.PACKET_TYPE,
            AutocraftingMonitorTaskStatusChangedPacket.STREAM_CODEC,
            wrapHandler(AutocraftingMonitorTaskStatusChangedPacket::handle)
        );
        clientbound.addMain(
            AutocraftingMonitorActivePacket.PACKET_TYPE,
            AutocraftingMonitorActivePacket.STREAM_CODEC,
            wrapHandler(AutocraftingMonitorActivePacket::handle)
        );
        clientbound.addMain(
            AutocraftingTaskCompletedPacket.PACKET_TYPE,
            AutocraftingTaskCompletedPacket.STREAM_CODEC,
            wrapHandler((packet, ctx) -> AutocraftingTaskCompletedPacket.handle(packet))
        );
        clientbound.addMain(
            ExportingIndicatorUpdatePacket.PACKET_TYPE,
            ExportingIndicatorUpdatePacket.STREAM_CODEC,
            wrapHandler(ExportingIndicatorUpdatePacket::handle)
        );
        clientbound.addMain(
            GridAutocraftingTaskAddedPacket.PACKET_TYPE,
            GridAutocraftingTaskAddedPacket.STREAM_CODEC,
            wrapHandler(GridAutocraftingTaskAddedPacket::handle)
        );
        clientbound.addMain(
            GridAutocraftingTaskRemovedPacket.PACKET_TYPE,
            GridAutocraftingTaskRemovedPacket.STREAM_CODEC,
            wrapHandler(GridAutocraftingTaskRemovedPacket::handle)
        );
        clientbound.addMain(
            GridAutocraftingTasksUpdatePacket.PACKET_TYPE,
            GridAutocraftingTasksUpdatePacket.STREAM_CODEC,
            wrapHandler(GridAutocraftingTasksUpdatePacket::handle)
        );
    }

    private static void registerClientToServerPackets(final PayloadFlow<RegistryFriendlyByteBuf, CustomPacketPayload> serverbound) {
        serverbound.addMain(
            CraftingGridClearPacket.PACKET_TYPE,
            CraftingGridClearPacket.STREAM_CODEC,
            wrapHandler(CraftingGridClearPacket::handle)
        );
        serverbound.addMain(
            PatternGridClearPacket.PACKET_TYPE,
            PatternGridClearPacket.STREAM_CODEC,
            wrapHandler((packet, ctx) -> PatternGridClearPacket.handle(ctx))
        );
        serverbound.addMain(
            PatternGridCreatePatternPacket.PACKET_TYPE,
            PatternGridCreatePatternPacket.STREAM_CODEC,
            wrapHandler((packet, ctx) -> PatternGridCreatePatternPacket.handle(ctx))
        );
        serverbound.addMain(
            CraftingGridRecipeTransferPacket.PACKET_TYPE,
            CraftingGridRecipeTransferPacket.STREAM_CODEC,
            wrapHandler(CraftingGridRecipeTransferPacket::handle)
        );
        serverbound.addMain(
            GridExtractPacket.PACKET_TYPE,
            GridExtractPacket.STREAM_CODEC,
            wrapHandler(GridExtractPacket::handle)
        );
        serverbound.addMain(
            GridInsertPacket.PACKET_TYPE,
            GridInsertPacket.STREAM_CODEC,
            wrapHandler(GridInsertPacket::handle)
        );
        serverbound.addMain(
            GridScrollPacket.PACKET_TYPE,
            GridScrollPacket.STREAM_CODEC,
            wrapHandler(GridScrollPacket::handle)
        );
        serverbound.addMain(
            PropertyChangePacket.PACKET_TYPE,
            PropertyChangePacket.STREAM_CODEC,
            wrapHandler(PropertyChangePacket::handle)
        );
        serverbound.addMain(
            ResourceFilterSlotChangePacket.PACKET_TYPE,
            ResourceFilterSlotChangePacket.STREAM_CODEC,
            wrapHandler(ResourceFilterSlotChangePacket::handle)
        );
        serverbound.addMain(
            ResourceSlotAmountChangePacket.PACKET_TYPE,
            ResourceSlotAmountChangePacket.STREAM_CODEC,
            wrapHandler(ResourceSlotAmountChangePacket::handle)
        );
        serverbound.addMain(
            ResourceSlotChangePacket.PACKET_TYPE,
            ResourceSlotChangePacket.STREAM_CODEC,
            wrapHandler(ResourceSlotChangePacket::handle)
        );
        serverbound.addMain(
            FilterSlotChangePacket.PACKET_TYPE,
            FilterSlotChangePacket.STREAM_CODEC,
            wrapHandler(FilterSlotChangePacket::handle)
        );
        serverbound.addMain(
            SingleAmountChangePacket.PACKET_TYPE,
            SingleAmountChangePacket.STREAM_CODEC,
            wrapHandler(SingleAmountChangePacket::handle)
        );
        serverbound.addMain(
            StorageInfoRequestPacket.PACKET_TYPE,
            StorageInfoRequestPacket.STREAM_CODEC,
            wrapHandler(StorageInfoRequestPacket::handle)
        );
        serverbound.addMain(
            UseSlotReferencedItemPacket.PACKET_TYPE,
            UseSlotReferencedItemPacket.STREAM_CODEC,
            wrapHandler(UseSlotReferencedItemPacket::handle)
        );
        serverbound.addMain(
            SecurityCardPermissionPacket.PACKET_TYPE,
            SecurityCardPermissionPacket.STREAM_CODEC,
            wrapHandler(SecurityCardPermissionPacket::handle)
        );
        serverbound.addMain(
            SecurityCardResetPermissionPacket.PACKET_TYPE,
            SecurityCardResetPermissionPacket.STREAM_CODEC,
            wrapHandler(SecurityCardResetPermissionPacket::handle)
        );
        serverbound.addMain(
            SecurityCardBoundPlayerPacket.PACKET_TYPE,
            SecurityCardBoundPlayerPacket.STREAM_CODEC,
            wrapHandler(SecurityCardBoundPlayerPacket::handle)
        );
        serverbound.addMain(
            PatternGridAllowedAlternativesChangePacket.PACKET_TYPE,
            PatternGridAllowedAlternativesChangePacket.STREAM_CODEC,
            wrapHandler(PatternGridAllowedAlternativesChangePacket::handle)
        );
        serverbound.addMain(
            PatternGridCraftingRecipeTransferPacket.PACKET_TYPE,
            PatternGridCraftingRecipeTransferPacket.STREAM_CODEC,
            wrapHandler(PatternGridCraftingRecipeTransferPacket::handle)
        );
        serverbound.addMain(
            PatternGridProcessingRecipeTransferPacket.PACKET_TYPE,
            PatternGridProcessingRecipeTransferPacket.STREAM_CODEC,
            wrapHandler(PatternGridProcessingRecipeTransferPacket::handle)
        );
        serverbound.addMain(
            PatternGridStonecutterRecipeTransferPacket.PACKET_TYPE,
            PatternGridStonecutterRecipeTransferPacket.STREAM_CODEC,
            wrapHandler(PatternGridStonecutterRecipeTransferPacket::handle)
        );
        serverbound.addMain(
            PatternGridSmithingTableRecipeTransferPacket.PACKET_TYPE,
            PatternGridSmithingTableRecipeTransferPacket.STREAM_CODEC,
            wrapHandler(PatternGridSmithingTableRecipeTransferPacket::handle)
        );
        serverbound.addMain(
            AutocrafterNameChangePacket.PACKET_TYPE,
            AutocrafterNameChangePacket.STREAM_CODEC,
            wrapHandler(AutocrafterNameChangePacket::handle)
        );
        serverbound.addMain(
            AutocraftingPreviewRequestPacket.PACKET_TYPE,
            AutocraftingPreviewRequestPacket.STREAM_CODEC,
            wrapHandler(AutocraftingPreviewRequestPacket::handle)
        );
        serverbound.addMain(
            AutocraftingPreviewCancelRequestPacket.PACKET_TYPE,
            AutocraftingPreviewCancelRequestPacket.STREAM_CODEC,
            wrapHandler((packet, ctx) -> AutocraftingPreviewCancelRequestPacket.handle(ctx))
        );
        serverbound.addMain(
            AutocraftingPreviewMaxAmountRequestPacket.PACKET_TYPE,
            AutocraftingPreviewMaxAmountRequestPacket.STREAM_CODEC,
            wrapHandler(AutocraftingPreviewMaxAmountRequestPacket::handle)
        );
        serverbound.addMain(
            AutocraftingRequestPacket.PACKET_TYPE,
            AutocraftingRequestPacket.STREAM_CODEC,
            wrapHandler(AutocraftingRequestPacket::handle)
        );
        serverbound.addMain(
            AutocraftingMonitorCancelPacket.PACKET_TYPE,
            AutocraftingMonitorCancelPacket.STREAM_CODEC,
            wrapHandler(AutocraftingMonitorCancelPacket::handle)
        );
        serverbound.addMain(
            AutocraftingMonitorCancelAllPacket.PACKET_TYPE,
            AutocraftingMonitorCancelAllPacket.STREAM_CODEC,
            wrapHandler((packet, ctx) -> AutocraftingMonitorCancelAllPacket.handle(ctx))
        );
        serverbound.addMain(
            GridAutocraftingTasksSubscriptionPacket.PACKET_TYPE,
            GridAutocraftingTasksSubscriptionPacket.STREAM_CODEC,
            wrapHandler(GridAutocraftingTasksSubscriptionPacket::handle)
        );
    }

    private static <T extends CustomPacketPayload> BiConsumer<T, CustomPayloadEvent.Context> wrapHandler(
        final PacketHandler<T> handler) {
        return (packet, ctx) -> {
            final ServerPlayer sender = ctx.getSender();
            final Player player = sender != null ? sender : Minecraft.getInstance().player;
            handler.handle(packet, () -> player);
        };
    }

    public void onServerTick(final TickEvent.ServerTickEvent.Pre e) {
        ServerListener.tick(e.server());
    }

    public void onServerStarting(final ServerStartingEvent e) {
        ServerListener.starting();
    }

    public void onServerStopped(final ServerStoppedEvent e) {
        ServerListener.stopped();
    }

    private record ForgeRegistryCallback<T>(DeferredRegister<T> registry) implements RegistryCallback<T> {
        @Override
        public <R extends T> Supplier<R> register(final Identifier id, final Supplier<R> value) {
            return registry.register(id.getPath(), value);
        }
    }
}
