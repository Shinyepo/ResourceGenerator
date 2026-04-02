package dev.shinyepo.resourcegenerator.datagen.tags;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.registries.BlockRegistry;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class CustomBlockTagProvider extends BlockTagsProvider {
    public CustomBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ResourceGenerator.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(TagRegistry.CONSUMER_STRUCTURE_BLOCKS)
                .add(BlockRegistry.CONSUMER_OUTPUT.get())
                .add(BlockRegistry.OUTPUT_UPGRADE.get())
                .replace(false);

        tag(TagRegistry.UPGRADE_BLOCKS)
                .add(BlockRegistry.OUTPUT_UPGRADE.get())
                .replace(false);

        tag(TagRegistry.RESOURCE_BLOCKS)
                .add(BlockRegistry.RESOURCE_IMITATOR.get())
                .replace(false);

    }
}
