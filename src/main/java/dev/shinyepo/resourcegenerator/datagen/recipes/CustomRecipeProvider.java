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
                .pattern("RCR")
                .pattern("IRI")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COMPARATOR)
                .define('R', Items.REPEATER)
                .define('D', ItemRegistry.ID_CARD)
                .unlockedBy("has_item", has(ItemRegistry.ID_CARD))
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
