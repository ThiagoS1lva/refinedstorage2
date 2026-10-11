package com.refinedmods.refinedstorage.forge.datagen;

import com.refinedmods.refinedstorage.forge.datagen.model.ModelProviders;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public class DataGenerators {
    private DataGenerators() {
    }

    public static void onGatherData(final GatherDataEvent e) {
        if (!e.includeClient()) {
            return;
        }
        final DataGenerator generator = e.getGenerator();
        final DataGenerator.PackGenerator pack = generator.getVanillaPack(true);
        pack.addProvider(ModelProviders::new);
    }
}
