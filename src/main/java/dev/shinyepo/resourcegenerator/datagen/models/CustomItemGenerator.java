package dev.shinyepo.resourcegenerator.datagen.models;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.registries.ItemRegistry;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.BiConsumer;

public class CustomItemGenerator extends ItemModelGenerators {


    public CustomItemGenerator(ItemModelOutput itemModelOutput, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        super(itemModelOutput, modelOutput);
    }

    @Override
    public void run() {
        generateItemWithExistingParent(ItemRegistry.SOLAR_ITEM.get(), "solar_panel");
        generateItemWithExistingParent(ItemRegistry.CONTROLLER_ITEM.get(), "controller");
        generateItemWithExistingParent(ItemRegistry.PIPE_ITEM.get(), "pipe");
        generateFlatItem(ItemRegistry.ID_CARD.get(), ModelTemplates.FLAT_ITEM);
    }

    private void generateItemWithExistingParent(Item item, String parent) {
        generateItemWithExistingModel(item, ResourceLocation.fromNamespaceAndPath(ResourceGenerator.MODID, "block/" + parent));
    }

    private void generateItemWithExistingModel(Item item, ResourceLocation modelLocation) {
        itemModelOutput.accept(
                item,
                ItemModelUtils.plainModel(modelLocation)
        );
    }
}
