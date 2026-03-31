package dev.shinyepo.resourcegenerator.datagen.tags;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.registries.ItemRegistry;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class CustomItemTagProvider extends ItemTagsProvider {
    public CustomItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ResourceGenerator.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(TagRegistry.ID_CARDS)
                .add(ItemRegistry.ID_CARD.get())
                .replace(false);

        tag(TagRegistry.CONSUMER_RESOURCES)
                .addTag(Tags.Items.ORES)
                .addTag(Tags.Items.INGOTS)
                .replace(false);
    }
}
