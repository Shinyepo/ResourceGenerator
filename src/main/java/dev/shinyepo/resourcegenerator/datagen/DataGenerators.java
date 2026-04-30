package dev.shinyepo.resourcegenerator.datagen;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.datagen.models.CustomModelProvider;
import dev.shinyepo.resourcegenerator.datagen.patterns.CustomPatternProvider;
import dev.shinyepo.resourcegenerator.datagen.recipes.CustomRecipeProvider;
import dev.shinyepo.resourcegenerator.datagen.tags.CustomBlockTagProvider;
import dev.shinyepo.resourcegenerator.datagen.tags.CustomItemTagProvider;
import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

import static dev.shinyepo.resourcegenerator.registries.DataPackRegistry.PATTERN_REGISTRY_KEY;

@EventBusSubscriber(modid = ResourceGenerator.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(CustomModelProvider::new);
        event.createProvider(CustomItemTagProvider::new);
        event.createProvider(CustomBlockTagProvider::new);
        event.createProvider(CustomRecipeProvider.Runner::new);

        event.createDatapackRegistryObjects(
                new RegistrySetBuilder()
                        .add(PATTERN_REGISTRY_KEY, CustomPatternProvider::register),
                _ -> {
                },
                Set.of(ResourceGenerator.MODID)
        );
    }
}
