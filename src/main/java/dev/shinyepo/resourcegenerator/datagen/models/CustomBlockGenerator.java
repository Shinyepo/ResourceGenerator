package dev.shinyepo.resourcegenerator.datagen.models;

import dev.shinyepo.resourcegenerator.registries.BlockRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CustomBlockGenerator extends BlockModelGenerators {
    public CustomBlockGenerator(Consumer<BlockModelDefinitionGenerator> blockStateOutput, ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput) {
        super(blockStateOutput, itemModelOutput, modelOutput);
    }

    @Override
    public void run() {
        generateHorizontalBlockWithExistingModel(BlockRegistry.CONTROLLER);
        generateBlockWithExistingModel(BlockRegistry.PIPE);
        generateBlockWithExistingModel(BlockRegistry.BASIC_CONSUMER);
        generateBlockWithExistingModel(BlockRegistry.SOLAR_PANEL);
        generateBlockWithExistingModel(BlockRegistry.WATER_ABSORBER);
        generateAirLikeBlock(BlockRegistry.DUMMY_EXTENSION);
        generateBlockWithExistingModel(BlockRegistry.OUTPUT_UPGRADE);

    }

    private void generateBlockWithExistingModel(Block block, Identifier modelLocation) {
        blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block,
                        new MultiVariant(
                                WeightedList.of(
                                        new Variant(modelLocation)
                                ))
                ));
    }

    private void generateHorizontalBlockWithExistingModel(Block block, Identifier modelLocation) {
        blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block,
                        new MultiVariant(
                                WeightedList.of(
                                        new Variant(modelLocation)
                                ))
                ).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
    }

    private void generateHorizontalBlockWithExistingModel(Supplier<Block> block) {
        generateHorizontalBlockWithExistingModel(block.get(), ModelLocationUtils.getModelLocation(block.get()));
    }

    private void generateBlockWithExistingModel(Supplier<Block> block) {
        generateBlockWithExistingModel(block.get(), ModelLocationUtils.getModelLocation(block.get()));
    }

    private void generateAirLikeBlock(Supplier<Block> block) {
        blockStateOutput.accept(createSimpleBlock(block.get(), plainVariant(Identifier.parse("block/air"))));
    }
}
