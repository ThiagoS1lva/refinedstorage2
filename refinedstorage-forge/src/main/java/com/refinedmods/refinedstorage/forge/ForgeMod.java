package com.refinedmods.refinedstorage.forge;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// Skeleton entrypoint for the unofficial Forge port (MVP).
// Real content (ported from refinedstorage-neoforge) lands here file by file.
@Mod(ForgeMod.MOD_ID)
public final class ForgeMod {
    public static final String MOD_ID = "refinedstorage";

    private static final Logger LOGGER = LogUtils.getLogger();

    public ForgeMod() {
        LOGGER.info("Refined Storage (unofficial Forge port skeleton) loading");
    }
}
