package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.pricing.DefaultPriceRule;
import dev.shinyepo.resourcegenerator.data.pricing.ResourcePriceDefinition;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

import static dev.shinyepo.resourcegenerator.data.pricing.DefaultPriceRule.DEFAULT_RULES;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class PriceDefinitionRegistry {
    public static final ResourceKey<Registry<ResourcePriceDefinition>> PRICE_REGISTRY_KEY = ResourceKey.createRegistryKey(fromNamespaceAndPath(ResourceGenerator.MODID, "price_definitions"));
    public static final Registry<ResourcePriceDefinition> PRICE_REGISTRY = new RegistryBuilder<>(PRICE_REGISTRY_KEY)
            .sync(true)
            .create();

    public static final DeferredRegister<ResourcePriceDefinition> PRICES = DeferredRegister.create(PRICE_REGISTRY, ResourceGenerator.MODID);

    public static final Supplier<ResourcePriceDefinition> DEFAULT_ORE = registerDefault("ore", 5L);
    public static final Supplier<ResourcePriceDefinition> DEFAULT_RAW = registerDefault("raw", "ore", 1F);
    public static final Supplier<ResourcePriceDefinition> DEFAULT_INGOT = registerDefault("ingot", "ore", 1F);
    public static final Supplier<ResourcePriceDefinition> DEFAULT_NUGGET = registerDefault("nugget", "ingot", 0.11F);
    public static final Supplier<ResourcePriceDefinition> DEFAULT_STORAGE_BLOCK = registerDefault("storage_block", "ore", 9);
    public static final Supplier<ResourcePriceDefinition> DEFAULT_GEM = registerDefault("gem", 1000L);
    public static final Supplier<ResourcePriceDefinition> DEFAULT_DUST = registerDefault("dust", 75L);
    public static final Supplier<ResourcePriceDefinition> DEFAULT_GRAVEL = registerDefault("gravel", 15L);
    public static final Supplier<ResourcePriceDefinition> DEFAULT_SAND = registerDefault("sand", 30L);
    public static final Supplier<ResourcePriceDefinition> DEFAULT_STONE = registerDefault("stone", 60L);
    public static final Supplier<ResourcePriceDefinition> DEFAULT_COBBLESTONE = registerDefault("cobblestone", 30L);
    public static final Supplier<ResourcePriceDefinition> DEFAULT_SANDSTONE = registerDefault("sandstone", 120L);


    public static final Supplier<ResourcePriceDefinition> IRON_ORE = registerSetOfResources(Blocks.IRON_ORE, 45L);
    public static final Supplier<ResourcePriceDefinition> COPPER_ORE = registerSetOfResources(Blocks.COPPER_ORE, 35L);
    public static final Supplier<ResourcePriceDefinition> GOLD_ORE = registerSetOfResources(Blocks.GOLD_ORE, 400L);

    public static final Supplier<ResourcePriceDefinition> COAL_ORE = registerOre(Blocks.COAL_ORE, 60L);
    public static final Supplier<ResourcePriceDefinition> COAL = register(Items.COAL, 30L);
    public static final Supplier<ResourcePriceDefinition> CHARCOAL = register(Items.CHARCOAL, 30L);
    public static final Supplier<ResourcePriceDefinition> COAL_BLOCK = register(Blocks.COAL_BLOCK, Items.COAL, 9);


    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> registerDefault(String defaultId, long price) {
        return PRICES.register(defaultId,
                () -> ResourcePriceDefinition.fixed(fromNamespaceAndPath(ResourceGenerator.MODID, defaultId), price));
    }

    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> registerDefault(String defaultId, String referenceId, float multiplier) {
        return PRICES.register(defaultId,
                () -> ResourcePriceDefinition.reference(fromNamespaceAndPath(ResourceGenerator.MODID, defaultId), fromNamespaceAndPath(ResourceGenerator.MODID, referenceId), multiplier));
    }

    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> registerOre(Block ore, long price) {
        Identifier oreId = getBlockIdentifier(ore);
        Identifier oreReferenceId = fromNamespaceAndPath(ResourceGenerator.MODID, oreId.getPath());
        var result = register(oreId, price);

        Identifier netherOreId = fromNamespaceAndPath(oreId.getNamespace(), "nether_" + oreId.getPath());
        register(netherOreId, oreReferenceId, 1F);

        Identifier deepslateOreId = fromNamespaceAndPath(oreId.getNamespace(), "deepslate_" + oreId.getPath());
        register(deepslateOreId, oreReferenceId, 1F);

        return result;
    }

    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> register(Block resource, long price) {
        Identifier resourceId = getBlockIdentifier(resource);
        return register(resourceId, price);
    }

    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> register(Item resource, long price) {
        Identifier resourceId = getItemIdentifier(resource);
        return register(resourceId, price);
    }

    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> register(Identifier resourceId, long price) {
        return PRICES.register(resourceId.getPath(),
                () -> ResourcePriceDefinition.fixed(resourceId, price));
    }

    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> register(Block resource, Block reference, float multiplier) {
        Identifier resourceId = getBlockIdentifier(resource);
        Identifier referenceId = getBlockIdentifier(reference);
        Identifier registryReferenceId = fromNamespaceAndPath(ResourceGenerator.MODID, referenceId.getPath());

        return register(resourceId, registryReferenceId, multiplier);
    }

    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> register(Block resource, Item reference, float multiplier) {
        Identifier resourceId = getBlockIdentifier(resource);
        Identifier referenceId = getItemIdentifier(reference);
        Identifier registryReferenceId = fromNamespaceAndPath(ResourceGenerator.MODID, referenceId.getPath());

        return register(resourceId, registryReferenceId, multiplier);
    }

    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> register(Identifier resourceId, Identifier referenceId, float multiplier) {
        return PRICES.register(resourceId.getPath(),
                () -> ResourcePriceDefinition.reference(resourceId, referenceId, multiplier));
    }

    private static DeferredHolder<ResourcePriceDefinition, ResourcePriceDefinition> registerSetOfResources(Block resource, long price) {
        Identifier oreId = getBlockIdentifier(resource);
        Identifier oreReferenceId = fromNamespaceAndPath(ResourceGenerator.MODID, oreId.getPath());
        var result = registerOre(resource, price);

        String ingotPath = oreId.getPath().replace("ore", "ingot");
        Identifier ingotId = fromNamespaceAndPath(oreId.getNamespace(), ingotPath);
        Identifier ingotReferenceId = fromNamespaceAndPath(ResourceGenerator.MODID, ingotId.getPath());
        register(ingotId, oreReferenceId, 1F);

        String nuggetPath = oreId.getPath().replace("ore", "nugget");
        Identifier nuggetId = fromNamespaceAndPath(oreId.getNamespace(), nuggetPath);
        register(nuggetId, ingotReferenceId, 0.11F);

        String rawPath = "raw_" + oreId.getPath().replace("_ore", "");
        Identifier rawId = fromNamespaceAndPath(oreId.getNamespace(), rawPath);
        register(rawId, oreReferenceId, 1F);

        String blockPath = oreId.getPath().replace("ore", "block");
        Identifier blockId = fromNamespaceAndPath(oreId.getNamespace(), blockPath);
        register(blockId, ingotReferenceId, 9F);

        return result;
    }

    private static Identifier getBlockIdentifier(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private static Identifier getItemIdentifier(Item resource) {
        return BuiltInRegistries.ITEM.getKey(resource);
    }

    public static ResourcePriceDefinition getPriceData(Item item) {

        Identifier key = BuiltInRegistries.ITEM.getKey(item);
        Identifier id = fromNamespaceAndPath(ResourceGenerator.MODID, key.getPath());
        if (getPriceData(id) == null) {
            return getDefaultPriceData(item);
        }
        return getPriceData(id);
    }

    public static ResourcePriceDefinition getDefaultPriceData(Item item) {
        ItemStack itemStack = new ItemStack(item);

        for (DefaultPriceRule rule : DEFAULT_RULES) {
            if (itemStack.is(rule.tag())) {
                return rule.supplier().get();
            }
        }

        return null;
    }

    public static ResourcePriceDefinition getPriceData(Identifier id) {
        return PRICE_REGISTRY.getValue(id);
    }
}
