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

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ItemRegistry.FRAME)
                .pattern("III")
                .pattern("I I")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(this.output);

        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, ItemRegistry.MARKET_ITEM)
                .pattern(" E ")
                .pattern("CFL")
                .pattern(" H ")
                .define('E', Items.EMERALD)
                .define('C', Items.CHEST)
                .define('F', ItemRegistry.FRAME)
                .define('L', Items.LECTERN)
                .define('H', Items.HOPPER)
                .unlockedBy("has_item", has(ItemRegistry.FRAME))
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
