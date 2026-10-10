package com.refinedmods.refinedstorage.forge.storage.diskdrive;

import com.refinedmods.refinedstorage.common.storage.Disk;
import com.refinedmods.refinedstorage.common.storage.diskdrive.AbstractDiskDriveBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class ForgeDiskDriveBlockEntity extends AbstractDiskDriveBlockEntity {
    public static final ModelProperty<Disk[]> DISKS_PROPERTY = new ModelProperty<>();

    public ForgeDiskDriveBlockEntity(final BlockPos pos, final BlockState state) {
        super(pos, state);
    }

    @Override
    public ModelData getModelData() {
        if (disks == null) {
            return ModelData.EMPTY;
        }
        return ModelData.builder().with(DISKS_PROPERTY, disks).build();
    }
}
