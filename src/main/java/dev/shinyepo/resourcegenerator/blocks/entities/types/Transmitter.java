package dev.shinyepo.resourcegenerator.blocks.entities.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class Transmitter extends NetworkDeviceEntity {
    public Transmitter(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void tick() {

    }
}
