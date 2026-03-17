package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Consumer;
import dev.shinyepo.resourcegenerator.configs.ConsumerConfig;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class BasicConsumerEntity extends Consumer {
    public BasicConsumerEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.BASIC_CONSUMER_ENTITY.get(), ConsumerConfig.BASIC_CONSUMER, pos, blockState);

        configureSides(Direction.DOWN);
    }

    @Override
    public void tick(ServerLevel level) {
        
    }
}
