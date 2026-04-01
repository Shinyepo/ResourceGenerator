package dev.shinyepo.resourcegenerator.blocks.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;

public class HorizontalNetworkBlock extends NetworkBlock {
    protected HashMap<Direction, VoxelShape> SHAPES = new HashMap<>();
    protected static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public HorizontalNetworkBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        VoxelShape shape = SHAPES.get(state.getValue(FACING));
        return shape == null ? SHAPES.get(Direction.NORTH) : shape;
    }

    @Override
    protected @NonNull VoxelShape getCollisionShape(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        VoxelShape shape = SHAPES.get(state.getValue(FACING));
        return shape == null ? SHAPES.get(Direction.NORTH) : shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }


}
