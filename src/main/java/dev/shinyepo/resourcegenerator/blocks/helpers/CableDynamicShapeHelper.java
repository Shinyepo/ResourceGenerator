package dev.shinyepo.resourcegenerator.blocks.helpers;

import dev.shinyepo.resourcegenerator.blocks.Cable;
import dev.shinyepo.resourcegenerator.blocks.entities.ItemPipeEntity;
import dev.shinyepo.resourcegenerator.capabilities.INetworkCapability;
import dev.shinyepo.resourcegenerator.pipes.helpers.ItemPipeConnection;
import dev.shinyepo.resourcegenerator.registries.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
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

    public VoxelShape[] shapeCache;

    public CableDynamicShapeHelper() {
        init();
    }

    @Override
    public void init() {
        shapeCache = makeShapes();
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
        int north = state.getValue(NORTH) ? 1 : 0;
        int south = state.getValue(SOUTH) ? 1 : 0;
        int west = state.getValue(WEST) ? 1 : 0;
        int east = state.getValue(EAST) ? 1 : 0;
        int up = state.getValue(UP) ? 1 : 0;
        int down = state.getValue(DOWN) ? 1 : 0;

        int index = calculateShapeIndex(north, south, west, east, up, down);

        return shapeCache[index];
    }

    private VoxelShape[] makeShapes() {
        int length = ItemPipeConnection.values().length;
        VoxelShape[] shapeCache = new VoxelShape[length * length * length * length * length * length];

        for (int up = 0; up < 2; up++) {
            for (int down = 0; down < 2; down++) {
                for (int north = 0; north < 2; north++) {
                    for (int south = 0; south < 2; south++) {
                        for (int east = 0; east < 2; east++) {
                            for (int west = 0; west < 2; west++) {
                                int idx = calculateShapeIndex(north, south, west, east, up, down);
                                shapeCache[idx] = makeShape(north, south, west, east, up, down);
                            }
                        }
                    }
                }
            }
        }
        return shapeCache;
    }

    private VoxelShape makeShape(int north, int south, int west, int east, int up, int down) {
        VoxelShape shape = CENTER;
        shape = combineShape(shape, north, NORTH_EXT);
        shape = combineShape(shape, south, SOUTH_EXT);
        shape = combineShape(shape, west, WEST_EXT);
        shape = combineShape(shape, east, EAST_EXT);
        shape = combineShape(shape, up, UP_EXT);
        shape = combineShape(shape, down, DOWN_EXT);
        return shape;
    }

    private VoxelShape combineShape(VoxelShape shape, int cableConnection, VoxelShape cableShape) {
        if (cableConnection == 1) {
            return Shapes.join(shape, cableShape, BooleanOp.OR);
        }
        return shape;
    }

    private int calculateShapeIndex(int north, int south, int west, int east, int up, int down) {
        int l = 2; //Possible shapes per side
        return ((((south * l + north) * l + west) * l + east) * l + up) * l + down;
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

        BlockEntity be = level.getBlockEntity(relativePos);
        if (be == null || be instanceof ItemPipeEntity)
            return false;

        assert be.getLevel() != null;
        INetworkCapability cap = be.getLevel().getCapability(CapabilityRegistry.NETWORK_CAPABILITY, relativePos, direction.getOpposite());
        return cap != null;
    }
}
