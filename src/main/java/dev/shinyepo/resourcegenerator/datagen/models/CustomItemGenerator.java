package dev.shinyepo.resourcegenerator.datagen.models;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.registries.ItemRegistry;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.function.BiConsumer;

public class CustomItemGenerator extends ItemModelGenerators {


    public CustomItemGenerator(ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput) {
        super(itemModelOutput, modelOutput);
    }

    @Override
    public void run() {
        generateItemWithExistingParent(ItemRegistry.SOLAR_ITEM.get(), "solar_panel");
        generateItemWithExistingParent(ItemRegistry.WATER_ABSORBER_ITEM.get(), "water_absorber");
        generateItemWithExistingParent(ItemRegistry.CONTROLLER_ITEM.get(), "controller");
        generateItemWithExistingParent(ItemRegistry.CABLE_ITEM.get(), "cable");
        generateItemWithExistingParent(ItemRegistry.BASIC_CONSUMER_ITEM.get(), "basic_consumer");
        generateItemWithExistingParent(ItemRegistry.OUTPUT_UPGRADE_ITEM.get(), "output_upgrade");
        generateItemWithExistingParent(ItemRegistry.RESOURCE_IMITATOR_ITEM.get(), "resource_imitator");
        generateItemWithExistingParent(ItemRegistry.SCULK_ABSORBER_ITEM.get(), "sculk_absorber");
        generateItemWithExistingParent(ItemRegistry.CONSUMER_OUTPUT_ITEM.get(), "consumer_output");
        generateItemWithExistingParent(ItemRegistry.CONDUIT_ABSORBER_ITEM.get(), "conduit_absorber");
        generateItemWithExistingModel(ItemRegistry.ITEM_PIPE_ITEM.get(), Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "item/item_pipe"));
        generateFlatItem(ItemRegistry.ID_CARD.get(), ModelTemplates.FLAT_ITEM);
        generateFlatItem(ItemRegistry.INSPECTOR.get(), ModelTemplates.FLAT_ITEM);
        generateFlatItem(ItemRegistry.PIPE_WRENCH.get(), ModelTemplates.FLAT_ITEM);
    }

    private void generateItemWithExistingParent(Item item, String parent) {
        generateItemWithExistingModel(item, Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "block/" + parent));
    }

    private void generateItemWithExistingModel(Item item, Identifier modelLocation) {
        itemModelOutput.accept(
                item,
                ItemModelUtils.plainModel(modelLocation)
        );
    }
}
