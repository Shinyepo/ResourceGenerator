package dev.shinyepo.resourcegenerator.blocks.entities.types;

import dev.shinyepo.resourcegenerator.configs.ProducerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class Producer extends NetworkDeviceEntity {
    private final ProducerConfig config;

    public Producer(BlockEntityType<?> type, ProducerConfig config, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.config = config;
    }

    @Override
    protected void tick() {

    }
}
