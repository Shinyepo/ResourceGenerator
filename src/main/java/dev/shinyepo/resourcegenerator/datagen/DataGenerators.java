package dev.shinyepo.resourcegenerator.datagen;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.datagen.models.CustomModelProvider;
import dev.shinyepo.resourcegenerator.datagen.patterns.CustomPatternProvider;
import dev.shinyepo.resourcegenerator.datagen.tags.CustomTagProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

@EventBusSubscriber(modid = ResourceGenerator.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(CustomModelProvider::new);
        event.createProvider(CustomTagProvider::new);

        event.createDatapackRegistryObjects(
                CustomPatternProvider.register(),
                _ -> {
                },
                Set.of(ResourceGenerator.MODID)
        );
    }
}
