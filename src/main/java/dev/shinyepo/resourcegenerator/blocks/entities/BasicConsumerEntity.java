package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Consumer;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import static dev.shinyepo.resourcegenerator.datagen.patterns.CustomPatternProvider.TIER_1_PATTERN;

public class BasicConsumerEntity extends Consumer {
    private boolean initialized = false;

    public BasicConsumerEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.BASIC_CONSUMER_ENTITY.get(), pos, blockState);
        configureSides(Direction.DOWN, Direction.UP);
    }

    @Override
    public void tick(ServerLevel level) {
        if (!initialized) {
            level.registryAccess().get(TIER_1_PATTERN).ifPresent(pattern -> {
                this.pattern = pattern.value();
            });
            initialized = true;
        }
        super.tick(level);
    }
}
