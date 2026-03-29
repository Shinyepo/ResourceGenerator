package dev.shinyepo.resourcegenerator.datagen.tags;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class CustomBlockTagProvider extends BlockTagsProvider {
    public CustomBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ResourceGenerator.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(TagRegistry.CONSUMER_RESOURCES)
                .addTag(Tags.Blocks.ORES)
                .replace(false);

        tag(TagRegistry.UPGRADE_BLOCKS)
                .addTag(Tags.Blocks.STORAGE_BLOCKS_GOLD)
                .replace(false);

    }
}
