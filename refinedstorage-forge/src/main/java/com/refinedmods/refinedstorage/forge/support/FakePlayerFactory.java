package com.refinedmods.refinedstorage.forge.support;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Util;

public final class FakePlayerFactory {
    private FakePlayerFactory() {
    }

    public static Player get(final ServerLevel level, final GameProfile profile) {
        return new ForgeFakePlayer(level, profile);
    }

    public static Player getMinecraft(final ServerLevel level) {
        return get(level, new GameProfile(Util.NIL_UUID, "[Minecraft]"));
    }

    private static final class ForgeFakePlayer extends ServerPlayer {
        private ForgeFakePlayer(final ServerLevel level, final GameProfile profile) {
            super(level.getServer(), level, profile, ClientInformation.createDefault());
        }
    }
}
