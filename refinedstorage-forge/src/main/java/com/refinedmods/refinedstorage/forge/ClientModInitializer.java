package com.refinedmods.refinedstorage.forge;

import com.refinedmods.refinedstorage.common.AbstractClientModInitializer;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage.common.api.upgrade.AbstractUpgradeItem;
import com.refinedmods.refinedstorage.common.autocrafting.PatternItem;
import com.refinedmods.refinedstorage.common.autocrafting.PatternTooltipCache;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.content.ContentNames;
import com.refinedmods.refinedstorage.common.content.KeyMappings;
import com.refinedmods.refinedstorage.common.storagemonitor.StorageMonitorBlockEntityRenderer;
import com.refinedmods.refinedstorage.common.support.RecipeMapRecipeProvider;
import com.refinedmods.refinedstorage.common.support.tooltip.CompositeClientTooltipComponent;
import com.refinedmods.refinedstorage.common.support.tooltip.HelpClientTooltipComponent;
import com.refinedmods.refinedstorage.common.upgrade.RegulatorUpgradeItem;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinationClientTooltipComponent;
import com.refinedmods.refinedstorage.common.util.IdentifierUtil;
import com.refinedmods.refinedstorage.forge.storage.diskdrive.ForgeDiskDriveBlockEntityRenderer;
import com.refinedmods.refinedstorage.forge.storage.diskinterface.ForgeDiskInterfaceBlockEntityRenderer;
import com.refinedmods.refinedstorage.forge.storage.portablegrid.ForgePortableGridBlockEntityRenderer;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public final class ClientModInitializer extends AbstractClientModInitializer {
    private ClientModInitializer() {
    }

    @Mod.EventBusSubscriber(modid = IdentifierUtil.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent e) {
            AbstractClientModInitializer.initializeClientPlatformApi();
            registerScreens();
            registerBlockEntityRenderer();
            registerResourceRendering();
            registerAlternativeGridHints();
            registerDiskModels();
        }
    }

    @Mod.EventBusSubscriber(modid = IdentifierUtil.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientRegisterEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent e) {
            final KeyMapping.Category category = new KeyMapping.Category(createIdentifier("keymappings"));

            final KeyMapping focusSearchBarKeyBinding = new KeyMapping(
                ContentNames.FOCUS_SEARCH_BAR_TRANSLATION_KEY,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_TAB,
                category
            );
            e.register(focusSearchBarKeyBinding);
            KeyMappings.INSTANCE.setFocusSearchBar(focusSearchBarKeyBinding);

            final KeyMapping clearCraftingGridMatrixToNetwork = new KeyMapping(
                ContentNames.CLEAR_CRAFTING_MATRIX_TO_NETWORK_TRANSLATION_KEY,
                KeyConflictContext.GUI,
                KeyModifier.CONTROL,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                category,
                0
            );
            e.register(clearCraftingGridMatrixToNetwork);
            KeyMappings.INSTANCE.setClearCraftingGridMatrixToNetwork(clearCraftingGridMatrixToNetwork);

            final KeyMapping clearCraftingGridMatrixToInventory = new KeyMapping(
                ContentNames.CLEAR_CRAFTING_MATRIX_TO_INVENTORY_TRANSLATION_KEY,
                InputConstants.UNKNOWN.getValue(),
                category
            );
            e.register(clearCraftingGridMatrixToInventory);
            KeyMappings.INSTANCE.setClearCraftingGridMatrixToInventory(clearCraftingGridMatrixToInventory);

            final KeyMapping openWirelessGrid = new KeyMapping(
                ContentNames.OPEN_WIRELESS_GRID_TRANSLATION_KEY,
                KeyConflictContext.IN_GAME,
                InputConstants.UNKNOWN,
                category,
                0
            );
            e.register(openWirelessGrid);
            KeyMappings.INSTANCE.setOpenWirelessGrid(openWirelessGrid);

            final KeyMapping openPortableGrid = new KeyMapping(
                ContentNames.OPEN_PORTABLE_GRID_TRANSLATION_KEY,
                KeyConflictContext.IN_GAME,
                InputConstants.UNKNOWN,
                category,
                0
            );
            e.register(openPortableGrid);
            KeyMappings.INSTANCE.setOpenPortableGrid(openPortableGrid);

            final KeyMapping openWirelessAutocraftingMonitor = new KeyMapping(
                ContentNames.OPEN_WIRELESS_AUTOCRAFTING_MONITOR_TRANSLATION_KEY,
                KeyConflictContext.IN_GAME,
                InputConstants.UNKNOWN,
                category,
                0
            );
            e.register(openWirelessAutocraftingMonitor);
            KeyMappings.INSTANCE.setOpenWirelessAutocraftingMonitor(openWirelessAutocraftingMonitor);
        }

        @SubscribeEvent
        public static void onRegisterTooltipFactories(final RegisterClientTooltipComponentFactoriesEvent e) {
            e.register(
                AbstractUpgradeItem.UpgradeDestinationTooltipComponent.class,
                component -> new CompositeClientTooltipComponent(List.of(
                    new UpgradeDestinationClientTooltipComponent(component.destinations()),
                    HelpClientTooltipComponent.create(component.helpText())
                ))
            );
            e.register(
                HelpTooltipComponent.class,
                component -> HelpClientTooltipComponent.create(component.text())
            );
            e.register(
                RegulatorUpgradeItem.RegulatorTooltipComponent.class,
                component -> createRegulatorUpgradeClientTooltipComponent(
                    component.destinations(),
                    component.configuredResource(),
                    component.helpText()
                )
            );
            e.register(PatternItem.CraftingPatternTooltipComponent.class, PatternTooltipCache::getComponent);
            e.register(PatternItem.ProcessingPatternTooltipComponent.class, PatternTooltipCache::getComponent);
            e.register(PatternItem.StonecutterPatternTooltipComponent.class, PatternTooltipCache::getComponent);
            e.register(PatternItem.SmithingTablePatternTooltipComponent.class, PatternTooltipCache::getComponent);
        }
    }

    @Mod.EventBusSubscriber(modid = IdentifierUtil.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(final InputEvent.Key e) {
            handleInputEvents();
        }

        @SubscribeEvent
        public static void onMouseInput(final InputEvent.MouseButton.Pre e) {
            handleInputEvents();
        }

        @SubscribeEvent
        public static void onRecipesUpdated(final RecipesUpdatedEvent e) {
            if (Minecraft.getInstance().level != null) {
                Platform.INSTANCE.setClientRecipeProvider(new RecipeMapRecipeProvider(RecipeMap.EMPTY));
            }
        }
    }

    private static void registerScreens() {
        registerScreens(new ScreenRegistration() {
            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                final MenuType<? extends M> type,
                final ScreenConstructor<M, U> factory
            ) {
                MenuScreens.register(type, factory::create);
            }
        });
    }

    private static void registerBlockEntityRenderer() {
        BlockEntityRenderers.register(
            BlockEntities.INSTANCE.getDiskDrive(),
            ctx -> new ForgeDiskDriveBlockEntityRenderer<>()
        );
        BlockEntityRenderers.register(
            BlockEntities.INSTANCE.getDiskInterface(),
            ctx -> new ForgeDiskInterfaceBlockEntityRenderer<>()
        );
        BlockEntityRenderers.register(
            BlockEntities.INSTANCE.getStorageMonitor(),
            ctx -> new StorageMonitorBlockEntityRenderer()
        );
        BlockEntityRenderers.register(
            BlockEntities.INSTANCE.getPortableGrid(),
            ctx -> new ForgePortableGridBlockEntityRenderer<>()
        );
        BlockEntityRenderers.register(
            BlockEntities.INSTANCE.getCreativePortableGrid(),
            ctx -> new ForgePortableGridBlockEntityRenderer<>()
        );
    }
}
