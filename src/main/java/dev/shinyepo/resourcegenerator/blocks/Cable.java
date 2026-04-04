package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.CableEntity;
import dev.shinyepo.resourcegenerator.blocks.types.NetworkBlock;
import dev.shinyepo.resourcegenerator.capabilities.INetworkCapability;
import dev.shinyepo.resourcegenerator.registries.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Cable extends NetworkBlock implements SimpleWaterloggedBlock {
    private static final BooleanProperty NORTH = BooleanProperty.create("north");
    private static final BooleanProperty SOUTH = BooleanProperty.create("south");
    private static final BooleanProperty WEST = BooleanProperty.create("west");
    private static final BooleanProperty EAST = BooleanProperty.create("east");
    private static final BooleanProperty UP = BooleanProperty.create("up");
    private static final BooleanProperty DOWN = BooleanProperty.create("down");

    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape CENTER = Block.box(7, 7, 7, 9, 9, 9);

    private static final VoxelShape NORTH_EXT = Block.box(7, 7, 0, 9, 9, 7);
    private static final VoxelShape SOUTH_EXT = Block.box(7, 7, 9, 9, 9, 16);
    private static final VoxelShape EAST_EXT = Block.box(9, 7, 7, 16, 9, 9);
    private static final VoxelShape WEST_EXT = Block.box(0, 7, 7, 7, 9, 9);
    private static final VoxelShape UP_EXT = Block.box(7, 9, 7, 9, 16, 9);
    private static final VoxelShape DOWN_EXT = Block.box(7, 0, 7, 9, 7, 9);

    public Cable(Properties properties) {
        super(properties);

        setBlockEntity(CableEntity::new);

        registerDefaultState(getStateDefinition().any()
                .setValue(WATERLOGGED, true)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    @Override
    protected @NonNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.isFull())
                .setValue(NORTH, canConnect(context.getLevel(), context.getClickedPos(), Direction.NORTH))
                .setValue(SOUTH, canConnect(context.getLevel(), context.getClickedPos(), Direction.SOUTH))
                .setValue(WEST, canConnect(context.getLevel(), context.getClickedPos(), Direction.WEST))
                .setValue(EAST, canConnect(context.getLevel(), context.getClickedPos(), Direction.EAST))
                .setValue(UP, canConnect(context.getLevel(), context.getClickedPos(), Direction.UP))
                .setValue(DOWN, canConnect(context.getLevel(), context.getClickedPos(), Direction.DOWN));
    }

    private boolean canConnect(Level level, BlockPos pos, Direction direction) {
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

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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
    protected @NonNull BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state.setValue(getPropertyFor(direction),
                canConnect((Level) level, pos, direction));
    }

    private static BooleanProperty getPropertyFor(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }
}
