package com.refinedmods.refinedstorage.forge.grid.view;

import com.refinedmods.refinedstorage.common.grid.view.AbstractItemGridResourceType;

import java.util.Optional;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class ForgeItemGridResourceType extends AbstractItemGridResourceType {
    public static final ForgeItemGridResourceType INSTANCE = new ForgeItemGridResourceType();

    private ForgeItemGridResourceType() {
    }

    @Override
    public Optional<String> getModName(final String modId) {
        return ModList.getModContainerById(modId)
            .map(container -> container.getModInfo().getDisplayName());
    }

    @Override
    public String getModId(final ItemStack itemStack) {
        final String creatorModId = itemStack.getItem().getCreatorModId(itemStack);
        if (creatorModId == null) {
            return "";
        }
        return creatorModId;
    }
}
