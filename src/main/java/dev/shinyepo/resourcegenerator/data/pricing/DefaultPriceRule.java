package dev.shinyepo.resourcegenerator.data.pricing;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.function.Supplier;

import static dev.shinyepo.resourcegenerator.registries.PriceDefinitionRegistry.*;

public record DefaultPriceRule(TagKey<Item> tag, Supplier<ResourcePriceDefinition> supplier) {
    public static final List<DefaultPriceRule> DEFAULT_RULES = List.of(
            new DefaultPriceRule(Tags.Items.ORES, DEFAULT_ORE),
            new DefaultPriceRule(Tags.Items.RAW_MATERIALS, DEFAULT_RAW),
            new DefaultPriceRule(Tags.Items.INGOTS, DEFAULT_INGOT),
            new DefaultPriceRule(Tags.Items.NUGGETS, DEFAULT_NUGGET),
            new DefaultPriceRule(Tags.Items.STORAGE_BLOCKS, DEFAULT_STORAGE_BLOCK),
            new DefaultPriceRule(Tags.Items.GEMS, DEFAULT_GEM),
            new DefaultPriceRule(Tags.Items.DUSTS, DEFAULT_DUST),
            new DefaultPriceRule(Tags.Items.GRAVELS, DEFAULT_GRAVEL),
            new DefaultPriceRule(Tags.Items.SANDS, DEFAULT_SAND),
            new DefaultPriceRule(Tags.Items.STONES, DEFAULT_STONE),
            new DefaultPriceRule(Tags.Items.COBBLESTONES, DEFAULT_COBBLESTONE),
            new DefaultPriceRule(Tags.Items.SANDSTONE_BLOCKS, DEFAULT_SANDSTONE)
    );
}
