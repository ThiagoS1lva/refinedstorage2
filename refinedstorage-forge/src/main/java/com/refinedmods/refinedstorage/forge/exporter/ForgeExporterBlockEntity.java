package com.refinedmods.refinedstorage.forge.exporter;

import com.refinedmods.refinedstorage.common.exporter.AbstractExporterBlockEntity;
import com.refinedmods.refinedstorage.forge.support.render.ModelProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class ForgeExporterBlockEntity extends AbstractExporterBlockEntity {
    public ForgeExporterBlockEntity(final BlockPos pos, final BlockState state) {
        super(pos, state);
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(ModelProperties.CABLE_CONNECTIONS, connections).build();
    }
}
