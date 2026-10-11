package com.refinedmods.refinedstorage.forge;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.energy.EnergyStorage;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.AbstractPlatform;
import com.refinedmods.refinedstorage.common.Config;
import com.refinedmods.refinedstorage.common.api.autocrafting.PatternProviderExternalPatternSinkFactory;
import com.refinedmods.refinedstorage.common.api.grid.view.GridResourceType;
import com.refinedmods.refinedstorage.common.api.support.energy.EnergyItemContext;
import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import com.refinedmods.refinedstorage.common.api.support.resource.FluidOperationResult;
import com.refinedmods.refinedstorage.common.support.RecipeProvider;
import com.refinedmods.refinedstorage.common.support.containermenu.TransferManager;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.util.CustomBlockPlaceContext;
import com.refinedmods.refinedstorage.forge.api.RefinedStorageForgeApi;
import com.refinedmods.refinedstorage.forge.autocrafting.ResourceHandlerPatternProviderExternalPatternSinkFactory;
import com.refinedmods.refinedstorage.forge.grid.strategy.ItemGridInsertionStrategy;
import com.refinedmods.refinedstorage.forge.grid.view.ForgeFluidGridResourceType;
import com.refinedmods.refinedstorage.forge.grid.view.ForgeItemGridResourceType;
import com.refinedmods.refinedstorage.forge.support.CapabilityUtil;
import com.refinedmods.refinedstorage.forge.support.FakePlayerFactory;
import com.refinedmods.refinedstorage.forge.support.containermenu.ContainerTransferDestination;
import com.refinedmods.refinedstorage.forge.support.containermenu.MenuOpenerImpl;
import com.refinedmods.refinedstorage.forge.support.energy.EnergyStorageEnergyHandlerAdapter;
import com.refinedmods.refinedstorage.forge.support.energy.StackEnergyItemContext;
import com.refinedmods.refinedstorage.forge.support.render.FluidStackFluidRenderer;
import com.refinedmods.refinedstorage.forge.support.resource.VariantUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jspecify.annotations.Nullable;

import static com.refinedmods.refinedstorage.forge.support.resource.VariantUtil.ofFluidStack;
import static com.refinedmods.refinedstorage.forge.support.resource.VariantUtil.toFluidStack;
import static java.util.Objects.requireNonNull;

public final class PlatformImpl extends AbstractPlatform {
    private final ConfigImpl config = new ConfigImpl();
    @Nullable
    private RecipeProvider clientRecipeProvider;
    private final PatternProviderExternalPatternSinkFactory patternProviderExternalPatternSinkFactory =
        new ResourceHandlerPatternProviderExternalPatternSinkFactory();

    public PlatformImpl(final FMLJavaModLoadingContext context) {
        super(new MenuOpenerImpl(), new FluidStackFluidRenderer(), ItemGridInsertionStrategy::new);
        context.registerConfig(ModConfig.Type.COMMON, config.getSpec());
    }

    @Override
    public long getBucketAmount() {
        return FluidType.BUCKET_VOLUME;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public boolean canEditBoxLoseFocus(final EditBox editBox) {
        return editBox.canLoseFocus;
    }

    @Override
    public boolean isKeyDown(final KeyMapping keyMapping) {
        return !keyMapping.isUnbound() && InputConstants.isKeyDown(
            keyMapping.getKey().getValue()
        );
    }

    @Override
    public Optional<FluidOperationResult> drainContainer(final ItemStack container) {
        if (container.isEmpty()) {
            return Optional.empty();
        }
        final ItemStack copy = container.copy();
        final IFluidHandlerItem handler = FluidUtil.getFluidHandler(copy).orElse(null);
        if (handler == null) {
            return Optional.empty();
        }
        final FluidStack simulated = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (simulated.isEmpty()) {
            return Optional.empty();
        }
        final FluidStack drained = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
        return Optional.of(new FluidOperationResult(
            handler.getContainer(),
            ofFluidStack(drained),
            drained.getAmount()
        ));
    }

    @Override
    public Optional<FluidOperationResult> fillContainer(final ItemStack container,
                                                        final ResourceAmount resourceAmount) {
        if (!(resourceAmount.resource() instanceof FluidResource fluidResource)) {
            return Optional.empty();
        }
        final ItemStack copy = container.copy();
        final IFluidHandlerItem handler = FluidUtil.getFluidHandler(copy).orElse(null);
        if (handler == null) {
            return Optional.empty();
        }
        final int filled = handler.fill(
            toFluidStack(fluidResource, (int) Math.min(resourceAmount.amount(), Integer.MAX_VALUE)),
            IFluidHandler.FluidAction.EXECUTE
        );
        return Optional.of(new FluidOperationResult(handler.getContainer(), fluidResource, filled));
    }

    @Override
    public TransferManager createTransferManager(final AbstractContainerMenu containerMenu) {
        return new TransferManager(containerMenu, ContainerTransferDestination::new);
    }

    @Override
    public long insertIntoContainer(final Container container,
                                    final ItemResource itemResource,
                                    final long amount,
                                    final Action action) {
        final InvWrapper wrapper = new InvWrapper(container);
        final boolean simulate = action == Action.SIMULATE;
        ItemStack toInsert = VariantUtil.toItemStack(itemResource, (int) Math.min(amount, Integer.MAX_VALUE));
        long inserted = 0L;
        for (int i = 0; i < wrapper.getSlots() && !toInsert.isEmpty(); ++i) {
            final ItemStack remainder = wrapper.insertItem(i, toInsert, simulate);
            inserted += toInsert.getCount() - remainder.getCount();
            toInsert = remainder;
        }
        return inserted;
    }

    @Override
    public ItemStack getCloneItemStack(final BlockState state,
                                       final Level level,
                                       final BlockHitResult hitResult,
                                       final Player player) {
        return state.getCloneItemStack(level, hitResult.getBlockPos(), false);
    }

    @Override
    public NonNullList<ItemStack> getRemainingCraftingItems(final Player player,
                                                            final CraftingRecipe craftingRecipe,
                                                            final CraftingInput input) {
        ForgeHooks.setCraftingPlayer(player);
        final NonNullList<ItemStack> remainingItems = craftingRecipe.getRemainingItems(input);
        ForgeHooks.setCraftingPlayer(null);
        return remainingItems;
    }

    @Override
    public void onItemCrafted(final Player player, final ItemStack craftedStack, final CraftingContainer container) {
        ForgeEventFactory.firePlayerCraftingEvent(player, craftedStack, container);
    }

    @Override
    public Player getFakePlayer(final ServerLevel level, @Nullable final UUID playerId) {
        return Optional.ofNullable(playerId)
            .flatMap(id -> level.getServer().services().profileResolver().fetchById(id))
            .map(profile -> FakePlayerFactory.get(level, profile))
            .orElseGet(() -> FakePlayerFactory.getMinecraft(level));
    }

    @Override
    public boolean canBreakBlock(final Level level, final BlockPos pos, final BlockState state, final Player player) {
        final BlockEvent.BreakEvent e = new BlockEvent.BreakEvent(level, pos, state, player, Result.DEFAULT);
        return !BlockEvent.BreakEvent.BUS.post(e);
    }

    @Override
    public boolean placeBlock(
        final Level level,
        final BlockPos pos,
        final Direction direction,
        final Player player,
        final ItemStack stack
    ) {
        final BlockPlaceContext ctx = new CustomBlockPlaceContext(
            level,
            player,
            InteractionHand.MAIN_HAND,
            stack,
            new BlockHitResult(Vec3.ZERO, direction, pos, false)
        );
        final InteractionResult result = ForgeHooks.onPlaceItemIntoWorld(ctx);
        return result.consumesAction();
    }

    @Override
    public boolean placeFluid(
        final Level level,
        final BlockPos pos,
        final Direction direction,
        final Player player,
        final FluidResource fluidResource
    ) {
        if (level.getBlockState(pos).getFluidState().isSource()) {
            return false;
        }
        final FluidStack fluid = toFluidStack(fluidResource, FluidType.BUCKET_VOLUME);
        final IFluidHandler source = new IFluidHandler() {
            @Override
            public int getTanks() {
                return 1;
            }

            @Override
            public FluidStack getFluidInTank(final int tank) {
                return fluid.copy();
            }

            @Override
            public int getTankCapacity(final int tank) {
                return fluid.getAmount();
            }

            @Override
            public boolean isFluidValid(final int tank, final FluidStack stack) {
                return false;
            }

            @Override
            public int fill(final FluidStack resource, final FluidAction action) {
                return 0;
            }

            @Override
            public FluidStack drain(final FluidStack resource, final FluidAction action) {
                if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
                    return FluidStack.EMPTY;
                }
                final int taking = Math.min(resource.getAmount(), fluid.getAmount());
                final FluidStack result = fluid.copy();
                result.setAmount(taking);
                return result;
            }

            @Override
            public FluidStack drain(final int maxDrain, final FluidAction action) {
                final FluidStack result = fluid.copy();
                result.setAmount(Math.min(maxDrain, fluid.getAmount()));
                return result;
            }
        };
        return FluidUtil.tryPlaceFluid(player, level, InteractionHand.MAIN_HAND, pos, source, fluid);
    }

    @Override
    public ItemStack getBlockAsItemStack(final Block block,
                                         final BlockState state,
                                         final Direction direction,
                                         final LevelReader level,
                                         final BlockPos position,
                                         final Player player) {
        return state.getCloneItemStack(level, position, false);
    }

    @Override
    public Optional<SoundEvent> getBucketPickupSound(final LiquidBlock liquidBlock, final BlockState state) {
        return liquidBlock.getPickupSound(state);
    }

    @Override
    public List<ClientTooltipComponent> processTooltipComponents(
        final ItemStack stack,
        final GuiGraphicsExtractor graphics,
        final int mouseX,
        final Optional<TooltipComponent> imageComponent,
        final List<Component> components
    ) {
        return new ArrayList<>(ForgeHooksClient.gatherTooltipComponents(
            stack,
            components,
            imageComponent,
            mouseX,
            graphics.guiWidth(),
            graphics.guiHeight(),
            Minecraft.getInstance().font
        )); // make modifiable
    }

    @Override
    public Optional<EnergyStorage> getEnergyStorage(final ItemStack stack, final EnergyItemContext context) {
        return Optional.ofNullable(stack.getCapability(ForgeCapabilities.ENERGY).orElse(null))
            .filter(EnergyStorageEnergyHandlerAdapter.class::isInstance)
            .map(EnergyStorageEnergyHandlerAdapter.class::cast)
            .map(EnergyStorageEnergyHandlerAdapter::getEnergyStorage);
    }

    @Override
    public <T extends CustomPacketPayload> void sendPacketToServer(final T packet) {
        ModInitializer.CHANNEL.send(packet, net.minecraftforge.network.PacketDistributor.SERVER.with(null));
    }

    @Override
    public <T extends CustomPacketPayload> void sendPacketToClient(final ServerPlayer player, final T packet) {
        ModInitializer.CHANNEL.send(packet, net.minecraftforge.network.PacketDistributor.PLAYER.with(player));
    }

    @Override
    @Nullable
    public NetworkNodeContainerProvider getContainerProvider(final Level level,
                                                             final BlockPos pos,
                                                             @Nullable final Direction direction) {
        return CapabilityUtil.getContainerProvider(level, pos, direction);
    }

    @Override
    @Nullable
    public NetworkNodeContainerProvider getContainerProviderSafely(final Level level,
                                                                   final BlockPos pos,
                                                                   @Nullable final Direction direction) {
        if (!level.isLoaded(pos)) {
            return null;
        }
        // Avoid using EntityCreationType.IMMEDIATE.
        // By default, the block is removed first and then the block entity (see BaseBlock#onRemove).
        // But, when using mods like "Carrier", "Carpet" or "Carry On" that allow for moving block entities,
        // they remove the block entity first and then the block.
        // When removing a block with Carrier for example,
        // this causes a problematic situation that the block entity IS gone,
        // but that the #getBlockEntity() call here with type IMMEDIATE would recreate the block entity because
        // the block is still there.
        // If the block entity is returned here again even if it is removed, the preconditions in NetworkBuilder will
        // fail as the "removed" block entity/connection would still be present.
        final BlockEntity safeBlockEntity = level.getChunkAt(pos).getBlockEntity(
            pos,
            LevelChunk.EntityCreationType.CHECK
        );
        if (safeBlockEntity == null) {
            return null;
        }
        if (safeBlockEntity instanceof AbstractNetworkNodeContainerBlockEntity<?> container) {
            return container.getContainerProvider();
        }
        return safeBlockEntity.getCapability(
            RefinedStorageForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
            direction
        ).orElse(null);
    }

    @Override
    public void setSlotY(final Slot slot, final int y) {
        slot.y = y;
    }

    @Override
    public void requestModelDataUpdateOnClient(final BlockEntity blockEntity, final boolean updateChunk) {
        final Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }
        if (!level.isClientSide()) {
            throw new IllegalArgumentException("Cannot request model data update on server");
        }
        blockEntity.requestModelDataUpdate();
        if (updateChunk) {
            level.sendBlockUpdated(
                blockEntity.getBlockPos(),
                blockEntity.getBlockState(),
                blockEntity.getBlockState(),
                Block.UPDATE_ALL
            );
        }
    }

    @Override
    public void updateImageHeight(final AbstractContainerScreen<?> screen, final int height) {
        screen.imageHeight = height;
    }

    @Override
    public RecipeProvider getClientRecipeProvider(final Level level) {
        if (!level.isClientSide()) {
            final RecipeManager recipeManager = requireNonNull(level.getServer()).getRecipeManager();
            return new RecipeProvider() {
                @Override
                @SuppressWarnings("unchecked")
                public <I extends RecipeInput, T extends Recipe<I>> Stream<RecipeHolder<T>> getRecipesFor(
                    final RecipeType<T> type,
                    final I container,
                    final Level level
                ) {
                    return recipeManager.getRecipes().stream()
                        .filter(holder -> holder.value().getType() == type)
                        .map(holder -> (RecipeHolder<T>) holder)
                        .filter(holder -> holder.value().matches(container, level));
                }
            };
        }
        if (clientRecipeProvider == null) {
            throw new RuntimeException("Recipe provider is not synced yet");
        }
        return clientRecipeProvider;
    }

    @Override
    public void setClientRecipeProvider(final RecipeProvider recipeProvider) {
        this.clientRecipeProvider = recipeProvider;
    }

    @Override
    public void resetSlots(final AbstractContainerMenu containerMenu) {
        containerMenu.slots.clear();
        containerMenu.lastSlots.clear();
        containerMenu.remoteSlots.clear();
    }

    @Override
    public PatternProviderExternalPatternSinkFactory getPatternProviderExternalPatternSinkFactory() {
        return patternProviderExternalPatternSinkFactory;
    }

    @Override
    public GridResourceType getItemGridResourceType() {
        return ForgeItemGridResourceType.INSTANCE;
    }

    @Override
    public GridResourceType getFluidGridResourceType() {
        return ForgeFluidGridResourceType.INSTANCE;
    }

    @Override
    public Path getGameDirectory() {
        return FMLPaths.GAMEDIR.get();
    }
}
