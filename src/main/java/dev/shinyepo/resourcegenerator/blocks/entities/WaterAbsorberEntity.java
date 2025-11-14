package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Producer;
import dev.shinyepo.resourcegenerator.configs.ProducerConfig;
import dev.shinyepo.resourcegenerator.properties.CustomProperties;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class WaterAbsorberEntity extends Producer {
    public WaterAbsorberEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.WATER_ABSORBER_ENTITY.get(), ProducerConfig.WATER_ABSORBER, pos, blockState);

        configureSides(Direction.DOWN, Direction.UP);
    }

    @Override
    public void tick(ServerLevel level) {
        if (getBlockState().getValue(CustomProperties.OPERATIONAL)) {
            super.tick(level);
        }
    }
}
