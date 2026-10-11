package com.refinedmods.refinedstorage.forge.datagen;

import com.refinedmods.refinedstorage.forge.datagen.loot.LootTableProviderImpl;
import com.refinedmods.refinedstorage.forge.datagen.model.ModelProviders;
import com.refinedmods.refinedstorage.forge.datagen.recipe.MainRecipeProvider;
import com.refinedmods.refinedstorage.forge.datagen.recipe.RecoloringRecipeProvider;
import com.refinedmods.refinedstorage.forge.datagen.tag.BlockTagsProvider;
import com.refinedmods.refinedstorage.forge.datagen.tag.ItemTagsProvider;

import java.util.List;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraftforge.common.data.RegistryDataBuilder;
import net.minecraftforge.data.event.GatherDataEvent;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.MOD_ID;

public class DataGenerators {
    private DataGenerators() {
    }

    public static void onGatherData(final GatherDataEvent e) {
        final DataGenerator generator = e.getGenerator();
        final DataGenerator.PackGenerator pack = generator.getVanillaPack(true);
        pack.addProvider(ModelProviders::new);
        final BlockTagsProvider blockTagsProvider = pack.addProvider(output ->
            new BlockTagsProvider(output, e.getLookupProvider(), e.getExistingFileHelper()));
        pack.addProvider(output ->
            new ItemTagsProvider(output, e.getLookupProvider(), e.getExistingFileHelper()));

        final RegistryDataBuilder registryDataBuilder = RegistryDataBuilder.of()
            .modid(MOD_ID)
            .reloadable(reloadable -> {
                reloadable.add(Registries.LOOT_TABLE, new LootTableProviderImpl());
                reloadable.add(Registries.ADVANCEMENT, new AdvancementProvider(List.of(
                    com.refinedmods.refinedstorage.forge.datagen.advancement.AdvancementProvider::new
                )));
                reloadable.add(MainRecipeProvider.create());
                reloadable.add(RecoloringRecipeProvider.create());
            });
        pack.addProvider(registryDataBuilder::reloadableGenerator);
    }
}
