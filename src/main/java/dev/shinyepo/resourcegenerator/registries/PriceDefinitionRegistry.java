package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.pricing.ResourcePriceDefinition;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
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

    public static final Supplier<ResourcePriceDefinition> IRON_ORE = PRICES.register("iron_ore",
            () -> ResourcePriceDefinition.fixed(BuiltInRegistries.BLOCK.getKey(Blocks.IRON_ORE), 20L));
}
