package dev.shinyepo.resourcegenerator.datagen.patterns;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.patterns.Pattern;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

import static dev.shinyepo.resourcegenerator.registries.DataPackRegistry.PATTERN_REGISTRY_KEY;

public class CustomPatternProvider {
    public static final ResourceKey<Pattern> BASE_PATTERN = ResourceKey.create(PATTERN_REGISTRY_KEY, Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "base_pattern"));

    public static RegistrySetBuilder register() {
        RegistrySetBuilder builder = new RegistrySetBuilder();

        builder.add(PATTERN_REGISTRY_KEY, bootstrap -> {
            bootstrap.register(BASE_PATTERN,
                    new Pattern.BasePatternBuilder()
                            .withTier(0)
                            .withDepth(1)
                            .withSize(3)
                            .pattern(List.of(BlockTags.IRON_ORES, Tags.Blocks.STORAGE_BLOCKS_GOLD, BlockTags.IRON_ORES))
                            .pattern(List.of(Tags.Blocks.STORAGE_BLOCKS_GOLD, BlockTags.AIR, Tags.Blocks.STORAGE_BLOCKS_GOLD))
                            .pattern(List.of(BlockTags.IRON_ORES, Tags.Blocks.STORAGE_BLOCKS_GOLD, BlockTags.IRON_ORES))
                            .build());
        });

        return builder;
    }
}
