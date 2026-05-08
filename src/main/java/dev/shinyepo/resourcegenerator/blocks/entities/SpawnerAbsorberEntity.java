package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Producer;
import dev.shinyepo.resourcegenerator.configs.ProducerConfig;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerAbsorberEntity extends Producer {
    public SpawnerAbsorberEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.SPAWNER_ABSORBER_ENTITY.get(), ProducerConfig.SPAWNER_ABSORBER, pos, blockState);

        configureSides(Direction.DOWN);
    }

    @Override
    public void tick(ServerLevel level) {

    }
}
