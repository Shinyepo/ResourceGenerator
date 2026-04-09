package dev.shinyepo.resourcegenerator.pipes.builders;

import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.world.level.block.Block;

public class CustomModelDefinition implements BlockModelDefinitionGenerator {
    private final Block block;
    private final CustomBlockModelBuilder builder;

    private CustomModelDefinition(Block block, CustomBlockModelBuilder builder) {
        this.block = block;
        this.builder = builder;
    }

    public static CustomModelDefinition dispatch(Block block, CustomBlockModelBuilder builder) {
        return new CustomModelDefinition(block, builder);
    }

    @Override
    public Block block() {
        // Returns the block you are generating the definition file for
        return this.block;
    }

    @Override
    public BlockStateModelDispatcher create() {
        return new BlockStateModelDispatcher(new CustomBlockDefinition(this.builder.toUnbaked()));
    }
}
