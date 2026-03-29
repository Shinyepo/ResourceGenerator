package dev.shinyepo.resourcegenerator.datagen.patterns;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.patterns.Pattern;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import static dev.shinyepo.resourcegenerator.registries.DataPackRegistry.PATTERN_REGISTRY_KEY;

public class CustomPatternProvider {
    public static final ResourceKey<Pattern> TIER_1_PATTERN = ResourceKey.create(PATTERN_REGISTRY_KEY, Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "tier_1_pattern"));
    public static final ResourceKey<Pattern> TIER_2_PATTERN = ResourceKey.create(PATTERN_REGISTRY_KEY, Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "tier_2_pattern"));
    public static final ResourceKey<Pattern> TIER_3_PATTERN = ResourceKey.create(PATTERN_REGISTRY_KEY, Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "tier_3_pattern"));

    public static void register(BootstrapContext<Pattern> bootstrap) {
        bootstrap.register(TIER_1_PATTERN,
                new Pattern.BasePatternBuilder()
                        .withTier(1)
                        .build());
        bootstrap.register(TIER_2_PATTERN,
                new Pattern.BasePatternBuilder()
                        .withTier(2)
                        .build());
        bootstrap.register(TIER_3_PATTERN,
                new Pattern.BasePatternBuilder()
                        .withTier(3)
                        .build());
    }
}
