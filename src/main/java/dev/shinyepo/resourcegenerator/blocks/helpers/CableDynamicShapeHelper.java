package dev.shinyepo.resourcegenerator.blocks.helpers;

import dev.shinyepo.resourcegenerator.blocks.Cable;
import dev.shinyepo.resourcegenerator.blocks.types.NetworkBlock;
import dev.shinyepo.resourcegenerator.capabilities.INetworkCapability;
import dev.shinyepo.resourcegenerator.registries.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static dev.shinyepo.resourcegenerator.blocks.Cable.*;

public class CableDynamicShapeHelper implements IDynamicShapeHelper {
    private static final VoxelShape CENTER = Block.box(7, 7, 7, 9, 9, 9);

    private static final VoxelShape NORTH_EXT = Block.box(7, 7, 0, 9, 9, 7);
    private static final VoxelShape SOUTH_EXT = Block.box(7, 7, 9, 9, 9, 16);
    private static final VoxelShape EAST_EXT = Block.box(9, 7, 7, 16, 9, 9);
    private static final VoxelShape WEST_EXT = Block.box(0, 7, 7, 7, 9, 9);
    private static final VoxelShape UP_EXT = Block.box(7, 9, 7, 9, 16, 9);
    private static final VoxelShape DOWN_EXT = Block.box(7, 0, 7, 9, 7, 9);

    @Override
    public void init() {

    }

    @Override
    public BlockState calculateStateForPlacement(Level level, BlockPos pos, BlockState state) {
        return state
                .setValue(NORTH, canConnect(level, pos, Direction.NORTH))
                .setValue(SOUTH, canConnect(level, pos, Direction.SOUTH))
                .setValue(WEST, canConnect(level, pos, Direction.WEST))
                .setValue(EAST, canConnect(level, pos, Direction.EAST))
                .setValue(UP, canConnect(level, pos, Direction.UP))
                .setValue(DOWN, canConnect(level, pos, Direction.DOWN));
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        VoxelShape shape = CENTER;

        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_EXT);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_EXT);
        if (state.getValue(EAST)) shape = Shapes.or(shape, EAST_EXT);
        if (state.getValue(WEST)) shape = Shapes.or(shape, WEST_EXT);
        if (state.getValue(UP)) shape = Shapes.or(shape, UP_EXT);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, DOWN_EXT);

        return shape;
    }

    @Override
    public BlockState updateShape(LevelReader level, BlockPos pos, BlockState state) {
        return calculateState(level, pos, state);
    }

    private BlockState calculateState(LevelReader world, BlockPos pos, BlockState state) {
        boolean north = canConnect(world, pos, Direction.NORTH);
        boolean south = canConnect(world, pos, Direction.SOUTH);
        boolean east = canConnect(world, pos, Direction.EAST);
        boolean west = canConnect(world, pos, Direction.WEST);
        boolean up = canConnect(world, pos, Direction.UP);
        boolean down = canConnect(world, pos, Direction.DOWN);

        return state.setValue(NORTH, north)
                .setValue(SOUTH, south)
                .setValue(EAST, east)
                .setValue(WEST, west)
                .setValue(UP, up)
                .setValue(DOWN, down);
    }

    private boolean canConnect(LevelReader level, BlockPos pos, Direction direction) {
        BlockPos relativePos = pos.relative(direction);
        BlockState relativeState = level.getBlockState(relativePos);
        if (relativeState.getBlock() instanceof Cable)
            return true;

        if (relativeState.getBlock() instanceof NetworkBlock) {
            if (level instanceof ServerLevel serverLevel) {
                INetworkCapability cap = serverLevel.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, relativePos, direction.getOpposite());
                return cap != null;
            }
        }

        return false;
    }
}
