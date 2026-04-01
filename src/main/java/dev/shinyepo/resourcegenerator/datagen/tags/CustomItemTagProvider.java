package dev.shinyepo.resourcegenerator.datagen.tags;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.registries.ItemRegistry;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
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
                .addTag(Tags.Items.RAW_MATERIALS)
                .addTag(Tags.Items.INGOTS)
                .addTag(Tags.Items.NUGGETS)
                .addTag(Tags.Items.STORAGE_BLOCKS)

                .addTag(Tags.Items.GEMS)
                .addTag(Tags.Items.DUSTS)

                .addTag(Tags.Items.GRAVELS)
                .addTag(Tags.Items.SANDS)
                .addTag(Tags.Items.STONES)
                .addTag(Tags.Items.COBBLESTONES)
                .addTag(Tags.Items.SANDSTONE_BLOCKS)

                .add(Items.COAL)
                .add(Items.CHARCOAL)
                .replace(false);
    }
}
