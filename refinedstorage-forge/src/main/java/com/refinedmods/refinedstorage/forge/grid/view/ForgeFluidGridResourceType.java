package com.refinedmods.refinedstorage.forge.grid.view;

import com.refinedmods.refinedstorage.common.grid.view.AbstractFluidGridResourceType;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.util.ClientPlatformUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForgeFluidGridResourceType extends AbstractFluidGridResourceType {
    public static final ForgeFluidGridResourceType INSTANCE = new ForgeFluidGridResourceType();

    private static final Logger LOGGER = LoggerFactory.getLogger(ForgeFluidGridResourceType.class);

    private final Map<FluidResource, FluidStack> stackCache = new HashMap<>();

    private ForgeFluidGridResourceType() {
    }

    @Override
    protected String getTooltip(final FluidResource resource) {
        try {
            if (resource.fluid() instanceof net.minecraft.world.level.material.FlowingFluid flowing) {
                final Item bucketItem = flowing.getBucket();
                if (bucketItem != null) {
                    final ItemStack bucketStack = new ItemStack(bucketItem);
                    return bucketStack.getTooltipLines(
                            Item.TooltipContext.of(ClientPlatformUtil.getClientLevel()),
                            ClientPlatformUtil.getClientPlayer(),
                            TooltipFlag.ADVANCED
                        )
                        .stream()
                        .map(Component::getString)
                        .collect(Collectors.joining("\n"));
                }
            }
            return toStack(resource).getDisplayName().getString();
        } catch (final Throwable t) {
            LOGGER.warn("Failed to get tooltip for fluid {}", resource, t);
            return "";
        }
    }

    @Override
    protected String getModName(final String modId) {
        return ModList.getModContainerById(modId)
            .map(container -> container.getModInfo().getDisplayName())
            .orElse("");
    }

    @Override
    protected String getName(final FluidResource fluidResource) {
        return toStack(fluidResource).getDisplayName().getString();
    }

    private FluidStack toStack(final FluidResource fluidResource) {
        return stackCache.computeIfAbsent(
            fluidResource,
            r -> new FluidStack(r.fluid(), FluidType.BUCKET_VOLUME)
        );
    }
}
