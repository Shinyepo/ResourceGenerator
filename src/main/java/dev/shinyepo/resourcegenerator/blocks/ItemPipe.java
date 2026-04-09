package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.ItemPipeEntity;
import dev.shinyepo.resourcegenerator.blocks.helpers.IDynamicShapeHelper;
import dev.shinyepo.resourcegenerator.blocks.helpers.ItemPipeDynamicShapeHelper;
import dev.shinyepo.resourcegenerator.blocks.types.BasicBlock;
import dev.shinyepo.resourcegenerator.pipes.helpers.ItemPipeConnection;
import dev.shinyepo.resourcegenerator.properties.CustomProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ItemPipe extends BasicBlock implements SimpleWaterloggedBlock {
    private final IDynamicShapeHelper dynamicShape;
    private static final EnumProperty<ItemPipeConnection> NORTH = CustomProperties.NORTH;
    private static final EnumProperty<ItemPipeConnection> SOUTH = CustomProperties.SOUTH;
    private static final EnumProperty<ItemPipeConnection> WEST = CustomProperties.WEST;
    private static final EnumProperty<ItemPipeConnection> EAST = CustomProperties.EAST;
    private static final EnumProperty<ItemPipeConnection> UP = CustomProperties.UP;
    private static final EnumProperty<ItemPipeConnection> DOWN = CustomProperties.DOWN;

    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;


    public ItemPipe(Properties properties) {
        super(properties);

        setBlockEntity(ItemPipeEntity::new);

        dynamicShape = new ItemPipeDynamicShapeHelper();
        dynamicShape.init();

        registerDefaultState(getStateDefinition().any()
                .setValue(WATERLOGGED, false));
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
        Level world = context.getLevel();
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        BlockPos pos = context.getClickedPos();
        return dynamicShape.calculateStateForPlacement(world, pos, defaultBlockState())
                .setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.isFull());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, by, itemStack);
        BlockState blockState = dynamicShape.calculateStateForPlacement(level, pos, state);
        if (state != blockState) {
            level.setBlockAndUpdate(pos, blockState);
        }
    }

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return dynamicShape.getShape(state);
    }

    @Override
    protected @NonNull BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return dynamicShape.updateShape(level, pos, state);
    }
}
