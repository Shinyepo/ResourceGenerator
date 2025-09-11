package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Transmitter;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class PipeEntity extends Transmitter {
    public PipeEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.PIPE_ENTITY.get(), pos, blockState);

        configureSides(Direction.values());
    }
}
