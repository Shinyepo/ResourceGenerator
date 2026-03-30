package dev.shinyepo.resourcegenerator.blocks.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;

public class UpgradeBlock extends BasicBlock {

    public UpgradeBlock(BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory, Properties properties) {
        super(blockEntityFactory, properties);
    }
}
