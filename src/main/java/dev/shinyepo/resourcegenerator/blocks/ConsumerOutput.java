package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.ConsumerOutputEntity;
import dev.shinyepo.resourcegenerator.blocks.types.ConsumerStructureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ConsumerOutput extends ConsumerStructureBlock {

    public ConsumerOutput(Properties properties) {
        super(properties);

        SHAPE = Block.box(0, 0, 0, 16, 16, 16);

        setBlockEntity(ConsumerOutputEntity::new);
        registerDefaultState(getStateDefinition().any());
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
        if (level instanceof ServerLevel serverLevel) {
            ConsumerOutputEntity entity = (ConsumerOutputEntity) serverLevel.getBlockEntity(pos);
            if (entity == null) return;
            entity.invalidateAdjacentCache();
        }
    }
}
