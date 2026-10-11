package com.refinedmods.refinedstorage.forge.support.containermenu;

import com.refinedmods.refinedstorage.common.support.containermenu.ExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.containermenu.MenuOpener;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public class MenuOpenerImpl implements MenuOpener {
    @Override
    public void openMenu(final ServerPlayer player, final MenuProvider menuProvider) {
        if (menuProvider instanceof ExtendedMenuProvider<?> extendedMenuProvider) {
            openExtendedMenu(player, extendedMenuProvider);
        } else {
            player.openMenu(menuProvider);
        }
    }

    private static <T> void openExtendedMenu(final ServerPlayer player,
                                              final ExtendedMenuProvider<T> extendedMenuProvider) {
        player.openMenu(
            extendedMenuProvider,
            buf -> {
                final RegistryFriendlyByteBuf regBuf = buf instanceof RegistryFriendlyByteBuf r
                    ? r
                    : new RegistryFriendlyByteBuf(buf, player.registryAccess());
                extendedMenuProvider.getMenuCodec().encode(
                    regBuf,
                    extendedMenuProvider.getMenuData()
                );
            }
        );
    }
}
