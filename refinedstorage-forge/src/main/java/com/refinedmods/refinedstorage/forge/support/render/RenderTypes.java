package com.refinedmods.refinedstorage.forge.support.render;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.rendertype.RenderType;

public final class RenderTypes {
    // TODO: port the custom disk LED render pipeline to the 26.3 renderpearl API.
    // Temporary vanilla stub so the game runs; LEDs render opaque instead of with custom depth state.
    public static final RenderType DISK_LEDS =
        net.minecraft.client.renderer.rendertype.RenderTypes.entitySolid(TextureAtlas.LOCATION_BLOCKS);

    private RenderTypes() {
    }
}
