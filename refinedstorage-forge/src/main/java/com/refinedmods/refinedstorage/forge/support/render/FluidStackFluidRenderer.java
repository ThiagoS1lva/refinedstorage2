package com.refinedmods.refinedstorage.forge.support.render;

import com.refinedmods.refinedstorage.common.support.render.FluidRenderer;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.util.ClientPlatformUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

public class FluidStackFluidRenderer implements FluidRenderer {
    private final Map<FluidResource, FluidStack> stackCache = new HashMap<>();

    private FluidStack getStack(final FluidResource resource) {
        return stackCache.computeIfAbsent(
            resource,
            r -> new FluidStack(r.fluid(), FluidType.BUCKET_VOLUME)
        );
    }

    private int getTint(final FluidStack stack) {
        return IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor(stack);
    }

    @Override
    public void render(final GuiGraphicsExtractor graphics, final int x, final int y,
                       final FluidResource resource) {
        final Minecraft minecraft = Minecraft.getInstance();
        final FluidStack stack = getStack(resource);
        final ModelManager modelManager = minecraft.getModelManager();
        final FluidStateModelSet fluidStateModelSet = modelManager.getFluidStateModelSet();
        final FluidModel fluidModel = fluidStateModelSet.get(resource.fluid().defaultFluidState());
        final TextureAtlasSprite sprite = ClientPlatformUtil.getFluidSprite(fluidModel);
        final int tint = getTint(stack);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, 16, 16, tint);
    }

    @Override
    public void render(final PoseStack poseStack, final SubmitNodeCollector nodes, final int light, final long seed,
                       final FluidResource resource) {
        final Minecraft minecraft = Minecraft.getInstance();
        final FluidStack stack = getStack(resource);
        final ModelManager modelManager = minecraft.getModelManager();
        final FluidStateModelSet fluidStateModelSet = modelManager.getFluidStateModelSet();
        final FluidModel fluidModel = fluidStateModelSet.get(resource.fluid().defaultFluidState());
        final TextureAtlasSprite sprite = ClientPlatformUtil.getFluidSprite(fluidModel);
        final int tint = getTint(stack);
        nodes.submitCustomGeometry(poseStack, RenderTypes.entitySolid(sprite.atlasLocation()), (pose, buffer) -> {
            final float scale = 0.3F;
            final var x0 = -scale / 2;
            final var y0 = scale / 2;
            final var x1 = scale / 2;
            final var y1 = -scale / 2;
            buffer.addVertex(pose, x0, y1, 0)
                .setColor(tint)
                .setUv(sprite.getU0(), sprite.getV1())
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0, 0, 1);
            buffer.addVertex(pose, x1, y1, 0)
                .setColor(tint)
                .setUv(sprite.getU1(), sprite.getV1())
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0, 0, 1);
            buffer.addVertex(pose, x1, y0, 0)
                .setColor(tint)
                .setUv(sprite.getU1(), sprite.getV0())
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0, 0, 1);
            buffer.addVertex(pose, x0, y0, 0)
                .setColor(tint)
                .setUv(sprite.getU0(), sprite.getV0())
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0, 0, 1);
        });
    }

    @Override
    public List<Component> getTooltip(final FluidResource resource) {
        if (resource.fluid() instanceof FlowingFluid flowing) {
            final Item bucketItem = flowing.getBucket();
            if (bucketItem != null) {
                final Minecraft minecraft = Minecraft.getInstance();
                return new ItemStack(bucketItem).getTooltipLines(
                    Item.TooltipContext.of(ClientPlatformUtil.getClientLevel()),
                    ClientPlatformUtil.getClientPlayer(),
                    minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL
                );
            }
        }
        return List.of(getDisplayName(resource));
    }

    @Override
    public Component getDisplayName(final FluidResource resource) {
        return getStack(resource).getDisplayName();
    }
}
