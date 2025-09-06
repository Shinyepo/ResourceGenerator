package dev.shinyepo.resourcegenerator.datagen.models;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;

public class CustomModelProvider extends ModelProvider {
    public CustomModelProvider(PackOutput output) {
        super(output, ResourceGenerator.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        new CustomBlockGenerator(blockModels.blockStateOutput, blockModels.itemModelOutput, blockModels.modelOutput).run();
        new CustomItemGenerator(itemModels.itemModelOutput, blockModels.modelOutput).run();
    }
}
