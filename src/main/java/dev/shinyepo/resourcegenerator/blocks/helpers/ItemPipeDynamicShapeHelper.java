package dev.shinyepo.resourcegenerator.blocks.helpers;

import dev.shinyepo.resourcegenerator.blocks.ItemPipe;
import dev.shinyepo.resourcegenerator.pipes.helpers.ItemPipeConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;

import static dev.shinyepo.resourcegenerator.pipes.helpers.ItemPipeConnection.BLOCK;
import static dev.shinyepo.resourcegenerator.pipes.helpers.ItemPipeConnection.CABLE;
import static dev.shinyepo.resourcegenerator.properties.CustomProperties.*;

public class ItemPipeDynamicShapeHelper implements IDynamicShapeHelper {
    VoxelShape SHAPE_CABLE_NORTH = Shapes.box(.3, .3, 0, .7, .7, .5);
    VoxelShape SHAPE_CABLE_SOUTH = Shapes.box(.3, .3, .5, .7, .7, 1);
    VoxelShape SHAPE_CABLE_WEST = Shapes.box(0, .3, .3, .5, .7, .7);
    VoxelShape SHAPE_CABLE_EAST = Shapes.box(.5, .3, .3, 1, .7, .7);
    VoxelShape SHAPE_CABLE_UP = Shapes.box(.3, .5, .3, .7, 1, .7);
    VoxelShape SHAPE_CABLE_DOWN = Shapes.box(.3, 0, .3, .7, .5, .7);

    VoxelShape SHAPE_BLOCK_NORTH = Shapes.box(.3, .3, 0, .7, .7, .1);
    VoxelShape SHAPE_BLOCK_SOUTH = Shapes.box(.3, .3, .9, .7, .7, 1);
    VoxelShape SHAPE_BLOCK_WEST = Shapes.box(0, .3, .3, .1, .7, .7);
    VoxelShape SHAPE_BLOCK_EAST = Shapes.box(.9, .3, .3, 1, .7, .7);
    VoxelShape SHAPE_BLOCK_UP = Shapes.box(.3, .9, .3, .7, 1, .7);
    VoxelShape SHAPE_BLOCK_DOWN = Shapes.box(.3, 0, .3, .7, .1, .7);

    public VoxelShape[] shapeCache;

    public ItemPipeDynamicShapeHelper() {
        init();
    }

    @Override
    public void init() {
        shapeCache = makeShapes();
    }

    @Override
    public BlockState calculateStateForPlacement(Level level, BlockPos pos, BlockState state) {
        return calculateState(level, pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        ItemPipeConnection north = state.getValue(NORTH);
        ItemPipeConnection south = state.getValue(SOUTH);
        ItemPipeConnection west = state.getValue(WEST);
        ItemPipeConnection east = state.getValue(EAST);
        ItemPipeConnection up = state.getValue(UP);
        ItemPipeConnection down = state.getValue(DOWN);
        int index = calculateShapeIndex(north, south, west, east, up, down);
        return shapeCache[index];
    }

    @Override
    public BlockState updateShape(LevelReader level, BlockPos pos, BlockState state) {
        return calculateState(level, pos, state);
    }

    public BlockState calculateState(LevelReader world, BlockPos pos, BlockState state) {
        ItemPipeConnection north = getConnectorType(world, pos, Direction.NORTH);
        ItemPipeConnection south = getConnectorType(world, pos, Direction.SOUTH);
        ItemPipeConnection west = getConnectorType(world, pos, Direction.WEST);
        ItemPipeConnection east = getConnectorType(world, pos, Direction.EAST);
        ItemPipeConnection up = getConnectorType(world, pos, Direction.UP);
        ItemPipeConnection down = getConnectorType(world, pos, Direction.DOWN);

        return state
                .setValue(NORTH, north)
                .setValue(SOUTH, south)
                .setValue(WEST, west)
                .setValue(EAST, east)
                .setValue(UP, up)
                .setValue(DOWN, down);
    }

    private ItemPipeConnection getConnectorType(LevelReader world, BlockPos connectorPos, Direction facing) {
        BlockPos pos = connectorPos.relative(facing);
        BlockState fromState = world.getBlockState(connectorPos);
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof ItemPipe && fromState.getBlock().equals(state.getBlock())) {
            return CABLE;
        } else if (isConnectable(world, connectorPos, facing)) {
            if (fromState.is(Blocks.AIR)) return BLOCK;
            var prop = getProp(facing);
            assert prop != null;
            return fromState.getValue(prop) != ItemPipeConnection.NONE ? fromState.getValue(prop) : BLOCK;
        } else {
            return ItemPipeConnection.NONE;
        }
    }

    private static EnumProperty<ItemPipeConnection> getProp(Direction facing) {
        switch (facing) {
            case Direction.NORTH -> {
                return NORTH;
            }
            case Direction.SOUTH -> {
                return SOUTH;
            }
            case Direction.WEST -> {
                return WEST;
            }
            case Direction.EAST -> {
                return EAST;
            }
            case Direction.DOWN -> {
                return DOWN;
            }
            case Direction.UP -> {
                return UP;
            }
        }
        return null;
    }

    private boolean isConnectable(LevelReader world, BlockPos connectorPos, Direction facing) {
        BlockPos pos = connectorPos.relative(facing);
        BlockState state = world.getBlockState(pos);
        if (state.isAir()) {
            return false;
        }
        BlockEntity te = world.getBlockEntity(pos);
        if (te == null) {
            return false;
        }
        assert te.getLevel() != null;
        return te.getLevel().getCapability(Capabilities.Item.BLOCK, pos, facing.getOpposite()) != null;
    }


    private VoxelShape[] makeShapes() {
        int length = ItemPipeConnection.values().length;
        VoxelShape[] shapeCache = new VoxelShape[length * length * length * length * length * length];

        for (ItemPipeConnection up : ItemPipeConnection.VALUES) {
            for (ItemPipeConnection down : ItemPipeConnection.VALUES) {
                for (ItemPipeConnection north : ItemPipeConnection.VALUES) {
                    for (ItemPipeConnection south : ItemPipeConnection.VALUES) {
                        for (ItemPipeConnection east : ItemPipeConnection.VALUES) {
                            for (ItemPipeConnection west : ItemPipeConnection.VALUES) {
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

    private VoxelShape makeShape(ItemPipeConnection north, ItemPipeConnection south, ItemPipeConnection west, ItemPipeConnection east, ItemPipeConnection up, ItemPipeConnection down) {
        VoxelShape shape = Shapes.box(.3, .3, .3, .7, .7, .7);
        shape = combineShape(shape, north, SHAPE_CABLE_NORTH, SHAPE_BLOCK_NORTH);
        shape = combineShape(shape, south, SHAPE_CABLE_SOUTH, SHAPE_BLOCK_SOUTH);
        shape = combineShape(shape, west, SHAPE_CABLE_WEST, SHAPE_BLOCK_WEST);
        shape = combineShape(shape, east, SHAPE_CABLE_EAST, SHAPE_BLOCK_EAST);
        shape = combineShape(shape, up, SHAPE_CABLE_UP, SHAPE_BLOCK_UP);
        shape = combineShape(shape, down, SHAPE_CABLE_DOWN, SHAPE_BLOCK_DOWN);
        return shape;
    }

    private VoxelShape combineShape(VoxelShape shape, ItemPipeConnection ItemPipeConnection, VoxelShape cableShape, VoxelShape blockShape) {
        if (ItemPipeConnection == CABLE) {
            return Shapes.join(shape, cableShape, BooleanOp.OR);
        } else if (ItemPipeConnection == BLOCK) {
            return Shapes.join(shape, Shapes.join(blockShape, cableShape, BooleanOp.OR), BooleanOp.OR);
        } else {
            return shape;
        }
    }

    private int calculateShapeIndex(ItemPipeConnection north, ItemPipeConnection south, ItemPipeConnection west, ItemPipeConnection east, ItemPipeConnection up, ItemPipeConnection down) {
        int l = ItemPipeConnection.values().length;
        return ((((south.ordinal() * l + north.ordinal()) * l + west.ordinal()) * l + east.ordinal()) * l + up.ordinal()) * l + down.ordinal();
    }
}
