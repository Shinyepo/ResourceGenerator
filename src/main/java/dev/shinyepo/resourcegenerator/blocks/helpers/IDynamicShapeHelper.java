package dev.shinyepo.resourcegenerator.blocks.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface IDynamicShapeHelper {
    void init();

    BlockState calculateStateForPlacement(Level level, BlockPos pos, BlockState state);

    VoxelShape getShape(BlockState state);

    BlockState updateShape(LevelReader level, BlockPos pos, BlockState state);
}
