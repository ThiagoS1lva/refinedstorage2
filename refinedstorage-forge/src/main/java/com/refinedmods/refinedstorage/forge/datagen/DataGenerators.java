package com.refinedmods.refinedstorage.forge.datagen;

import com.refinedmods.refinedstorage.forge.datagen.loot.LootTableProviderImpl;
import com.refinedmods.refinedstorage.forge.datagen.model.ModelProviders;
import com.refinedmods.refinedstorage.forge.datagen.recipe.MainRecipeProvider;
import com.refinedmods.refinedstorage.forge.datagen.recipe.RecoloringRecipeProvider;
import com.refinedmods.refinedstorage.forge.datagen.tag.BlockTagsProvider;
import com.refinedmods.refinedstorage.forge.datagen.tag.ItemTagsProvider;

import java.util.List;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;

public class DataGenerators {
    private DataGenerators() {
    }

    public static void onGatherData(final GatherDataEvent e) {
        final DataGenerator generator = e.getGenerator();
        final DataGenerator.PackGenerator pack = generator.getVanillaPack(true);
        pack.addProvider(ModelProviders::new);
        pack.addProvider(output -> new LootTableProviderImpl(output, e.getLookupProvider()));
        pack.addProvider(output -> new RecoloringRecipeProvider.Runner(output, e.getLookupProvider()));
        pack.addProvider(output -> new MainRecipeProvider.Runner(output, e.getLookupProvider()));
        final BlockTagsProvider blockTagsProvider = pack.addProvider(output ->
            new BlockTagsProvider(output, e.getLookupProvider(), e.getExistingFileHelper()));
        pack.addProvider(output ->
            new ItemTagsProvider(output, e.getLookupProvider(), e.getExistingFileHelper()));
        pack.addProvider(output -> new AdvancementProvider(
            output,
            e.getLookupProvider(),
            List.of(new com.refinedmods.refinedstorage.forge.datagen.advancement.AdvancementProvider())
        ));
    }
}
