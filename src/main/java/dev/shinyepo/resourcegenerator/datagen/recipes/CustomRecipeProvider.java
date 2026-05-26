package dev.shinyepo.resourcegenerator.datagen.recipes;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.registries.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class CustomRecipeProvider extends RecipeProvider {
    protected CustomRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.TOOLS, ItemRegistry.ID_CARD)
                .pattern(" D ")
                .pattern(" I ")
                .define('D', Items.BLACK_DYE)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_item", has(Items.BLACK_DYE))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.TOOLS, ItemRegistry.PIPE_WRENCH)
                .pattern(" C ")
                .pattern(" IC")
                .pattern("I  ")
                .define('C', Items.COPPER_INGOT)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_item", has(Items.COPPER_INGOT))
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, ItemRegistry.ITEM_PIPE_ITEM)
                .pattern("III")
                .pattern("HRH")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('H', Items.HOPPER)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_item", has(Items.HOPPER))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, ItemRegistry.CABLE_ITEM)
                .pattern("CCC")
                .pattern("PRP")
                .pattern("CCC")
                .define('C', Items.COPPER_INGOT)
                .define('R', Items.REDSTONE)
                .define('P', Items.REPEATER)
                .unlockedBy("has_item", has(Items.REDSTONE))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, ItemRegistry.MACHINE_CORE)
                .pattern("IGI")
                .pattern("GCG")
                .pattern("IGI")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COMPARATOR)
                .define('G', Items.GOLD_NUGGET)
                .unlockedBy("has_item", has(Items.GOLD_INGOT))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, ItemRegistry.MARKET_ITEM)
                .pattern("IEI")
                .pattern("CML")
                .pattern("IHI")
                .define('E', Items.EMERALD)
                .define('C', Items.CHEST)
                .define('I', Items.IRON_INGOT)
                .define('L', Items.LECTERN)
                .define('H', Items.HOPPER)
                .define('M', ItemRegistry.MACHINE_CORE)
                .unlockedBy("has_item", has(Items.EMERALD))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, ItemRegistry.SPAWNER_ABSORBER_ITEM)
                .pattern("III")
                .pattern("ZMS")
                .pattern("ICI")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.CREEPER_HEAD)
                .define('S', Items.SKELETON_SKULL)
                .define('Z', Items.ZOMBIE_HEAD)
                .define('M', ItemRegistry.MACHINE_CORE)
                .unlockedBy("has_item", has(ItemRegistry.MACHINE_CORE))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, ItemRegistry.CONTROLLER_ITEM)
                .pattern("IDI")
                .pattern("RMR")
                .pattern("IRI")
                .define('I', Items.IRON_INGOT)
                .define('M', ItemRegistry.MACHINE_CORE)
                .define('R', Items.REPEATER)
                .define('D', ItemRegistry.ID_CARD)
                .unlockedBy("has_item", has(ItemRegistry.MACHINE_CORE))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, ItemRegistry.WATER_ABSORBER_ITEM)
                .pattern("III")
                .pattern("BMB")
                .pattern("IBI")
                .define('I', Items.IRON_INGOT)
                .define('M', ItemRegistry.MACHINE_CORE)
                .define('B', Items.BUCKET)
                .unlockedBy("has_item", has(Items.BUCKET))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, ItemRegistry.SCULK_ABSORBER_ITEM)
                .pattern("ISI")
                .pattern("BMB")
                .pattern("IBI")
                .define('I', Items.IRON_INGOT)
                .define('M', ItemRegistry.MACHINE_CORE)
                .define('B', Items.SCULK)
                .define('S', Items.SCULK_SENSOR)
                .unlockedBy("has_item", has(Items.SCULK))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, ItemRegistry.CONDUIT_ABSORBER_ITEM)
                .pattern("ICI")
                .pattern("PMP")
                .pattern("IPI")
                .define('I', Items.IRON_INGOT)
                .define('M', ItemRegistry.MACHINE_CORE)
                .define('P', Items.PRISMARINE)
                .define('C', Items.CONDUIT)
                .unlockedBy("has_item", has(Items.PRISMARINE))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, ItemRegistry.RESOURCE_IMITATOR_ITEM)
                .pattern("IGI")
                .pattern("GCG")
                .pattern("IMI")
                .define('I', Items.IRON_INGOT)
                .define('G', Tags.Items.GLASS_BLOCKS)
                .define('M', ItemRegistry.MACHINE_CORE)
                .define('C', Tags.Items.CHESTS)
                .unlockedBy("has_item", has(ItemRegistry.MACHINE_CORE))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, ItemRegistry.CONSUMER_OUTPUT_ITEM)
                .pattern("IHI")
                .pattern("HMH")
                .pattern("IHI")
                .define('I', Items.IRON_INGOT)
                .define('H', Items.HOPPER)
                .define('M', ItemRegistry.MACHINE_CORE)
                .unlockedBy("has_item", has(Items.HOPPER))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, ItemRegistry.SOLAR_ITEM)
                .pattern("GGG")
                .pattern("IMI")
                .define('I', Items.IRON_INGOT)
                .define('G', Tags.Items.GLASS_PANES)
                .define('M', ItemRegistry.MACHINE_CORE)
                .unlockedBy("has_item", has(ItemRegistry.MACHINE_CORE))
                .save(this.output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new CustomRecipeProvider(provider, output);
        }

        @Override
        public @NonNull String getName() {
            return ResourceGenerator.MODID + " custom recipes";
        }
    }
}
