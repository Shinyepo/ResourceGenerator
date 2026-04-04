package dev.shinyepo.resourcegenerator.datagen.models;

import com.mojang.math.Quadrant;
import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.registries.BlockRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

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
        generateCable();
        generateBlockWithExistingModel(BlockRegistry.BASIC_CONSUMER);
        generateBlockWithExistingModel(BlockRegistry.SOLAR_PANEL);
        generateBlockWithExistingModel(BlockRegistry.WATER_ABSORBER);
        generateAirLikeBlock(BlockRegistry.DUMMY_EXTENSION);
        generateBlockWithExistingModel(BlockRegistry.OUTPUT_UPGRADE);
        generateBlockWithExistingModel(BlockRegistry.RESOURCE_IMITATOR);
        generateBlockWithExistingModel(BlockRegistry.SCULK_ABSORBER);
        generateBlockWithExistingModel(BlockRegistry.CONSUMER_OUTPUT);
        generateBlockWithExistingModel(BlockRegistry.CONDUIT_ABSORBER);
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

    private void generateCable() {
        Block block = BlockRegistry.CABLE.get();

        Variant core = new Variant(ModelLocationUtils.getModelLocation(block));
        Variant extension = new Variant(Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "block/cable_extension"));

        blockStateOutput.accept(
                MultiPartGenerator.multiPart(block)
                        .with(BlockModelGenerators.variant(core))
                        .with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true),
                                BlockModelGenerators.variant(extension))
                        .with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true),
                                BlockModelGenerators.variant(extension.withYRot(Quadrant.R180)))
                        .with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, true),
                                BlockModelGenerators.variant(extension.withYRot(Quadrant.R90)))
                        .with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, true),
                                BlockModelGenerators.variant(extension.withYRot(Quadrant.R270)))
                        .with(BlockModelGenerators.condition().term(BlockStateProperties.UP, true),
                                BlockModelGenerators.variant(extension.withXRot(Quadrant.R270)))
                        .with(BlockModelGenerators.condition().term(BlockStateProperties.DOWN, true),
                                BlockModelGenerators.variant(extension.withXRot(Quadrant.R90)))
        );
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
