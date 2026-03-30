package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.pricing.ResourcePriceDefinition;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class PriceDefinitionRegistry {
    public static final ResourceKey<Registry<ResourcePriceDefinition>> PRICE_REGISTRY_KEY = ResourceKey.createRegistryKey(fromNamespaceAndPath(ResourceGenerator.MODID, "price_definitions"));
    public static final Registry<ResourcePriceDefinition> PRICE_REGISTRY = new RegistryBuilder<>(PRICE_REGISTRY_KEY)
            .sync(true)
            .create();

    public static final DeferredRegister<ResourcePriceDefinition> PRICES = DeferredRegister.create(PRICE_REGISTRY, ResourceGenerator.MODID);

    public static final Supplier<ResourcePriceDefinition> DEFAULT_ORE = registerDefault("ore", 5L);
    public static final Supplier<ResourcePriceDefinition> DEFAULT_COMPRESSED_BLOCK = registerDefault("compressed_block", "ore", 9);

    public static final Supplier<ResourcePriceDefinition> IRON_ORE = register(Blocks.IRON_ORE, 10L);

    public static final Supplier<ResourcePriceDefinition> COPPER_ORE = register(Blocks.COPPER_ORE, Blocks.IRON_ORE, 5f);

    public static final Supplier<ResourcePriceDefinition> GOLD_ORE = register(Blocks.GOLD_ORE, 400L);


    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> registerDefault(String defaultId, long price) {
        return PRICES.register(defaultId,
                () -> ResourcePriceDefinition.fixed(fromNamespaceAndPath(ResourceGenerator.MODID, defaultId), price));
    }

    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> registerDefault(String defaultId, String referenceId, float multiplier) {
        return PRICES.register(defaultId,
                () -> ResourcePriceDefinition.reference(fromNamespaceAndPath(ResourceGenerator.MODID, defaultId), fromNamespaceAndPath(ResourceGenerator.MODID, referenceId), multiplier));
    }

    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> register(Block resource, long price) {
        Identifier resourceId = getBlockIdentifier(resource);
        return PRICES.register(resourceId.getPath(),
                () -> ResourcePriceDefinition.fixed(resourceId, price));
    }

    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> register(Block resource, Block reference, float multiplier) {
        Identifier resourceId = getBlockIdentifier(resource);
        Identifier referenceId = getBlockIdentifier(reference);
        Identifier registryReferenceId = fromNamespaceAndPath(ResourceGenerator.MODID, referenceId.getPath());

        return PRICES.register(resourceId.getPath(),
                () -> ResourcePriceDefinition.reference(resourceId, registryReferenceId, multiplier));
    }

    private static Identifier getBlockIdentifier(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static ResourcePriceDefinition getPriceData(BlockState blockState) {
        Identifier key = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());
        Identifier id = fromNamespaceAndPath(ResourceGenerator.MODID, key.getPath());
        if (getPriceData(id) == null) {
            return getDefaultPriceData(blockState);
        }
        return getPriceData(id);
    }

    public static ResourcePriceDefinition getDefaultPriceData(BlockState blockState) {
        var isOre = blockState.is(Tags.Blocks.ORES);
        if (isOre) {
            return DEFAULT_ORE.get();
        } else {
            return DEFAULT_COMPRESSED_BLOCK.get();
        }
    }

    public static ResourcePriceDefinition getPriceData(Identifier id) {
        return PRICE_REGISTRY.getValue(id);
    }
}
