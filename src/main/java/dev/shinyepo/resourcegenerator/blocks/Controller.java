package dev.shinyepo.resourcegenerator.blocks;

import com.mojang.serialization.MapCodec;
import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.blocks.entities.ControllerEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.DummyExtensionEntity;
import dev.shinyepo.resourcegenerator.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Controller extends HorizontalDirectionalBlock implements EntityBlock {
    private static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape[] SHAPE = new VoxelShape[]{Shapes.or(
            Block.box(-15.5, 0.5, 0.75, 31.5, 15.5, 16),
            Block.box(-15.99, 0, 0.01, -13.99, 15.99, 2.01),
            Block.box(-15.99, 0, 14.01, -13.99, 15.99, 16.01),
            Block.box(29.99, 0, 0.01, 31.99, 15.99, 2.01),
            Block.box(29.99, 0, 14.01, 31.99, 15.99, 16.01),
            Block.box(-16, 15, 0, 32, 16, 16.02)
    ), Shapes.or(
            Block.box(0.01, 0, -15.99, 2.01, 16, -13.99),
            Block.box(13.99, 0, -15.99, 15.99, 16, -13.99),
            Block.box(13.99, 0, 29.99, 15.99, 16, 31.99),
            Block.box(0.01, 0, 29.99, 2.01, 16, 31.99),
            Block.box(0.75, 0.5, -15.5, 15.98, 15, 31.5),
            Block.box(0, 15, -16, 16, 16.01, 32)
    )};

    public Controller(Properties p_54120_) {
        super(p_54120_);

        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        if (facing.getAxis() == Direction.Axis.X) {
            return SHAPE[1];
        }
        return SHAPE[0];
    }


    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos leftPos = blockpos.relative(direction.getCounterClockWise());
        BlockPos rightPos = blockpos.relative(direction.getClockWise());
        Level level = context.getLevel();
        return level.getBlockState(rightPos).canBeReplaced(context) &&
                level.getBlockState(leftPos).canBeReplaced() &&
                level.getWorldBorder().isWithinBounds(rightPos) && level.getWorldBorder().isWithinBounds(leftPos) ?
                this.defaultBlockState().setValue(FACING, direction.getOpposite()) :
                null;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        Direction facing = state.getValue(FACING);
        BlockPos leftPos = pos.relative(facing.getCounterClockWise());
        BlockPos rightPos = pos.relative(facing.getClockWise());

        if (!level.getBlockState(leftPos).isAir()) {
            level.removeBlock(leftPos, false);
        }

        if (!level.getBlockState(rightPos).isAir()) {
            level.removeBlock(rightPos, false);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        Block dummyExtension = BlockRegistry.DUMMY_EXTENSION.get();

        BlockPos leftPos = pos.relative(state.getValue(FACING).getCounterClockWise());
        BlockPos rightPos = pos.relative(state.getValue(FACING).getClockWise());

        level.setBlock(leftPos, dummyExtension.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(rightPos, dummyExtension.defaultBlockState(), Block.UPDATE_ALL);

        if (!level.isClientSide) {
            DummyExtensionEntity leftEntity = (DummyExtensionEntity) level.getBlockEntity(leftPos);
            DummyExtensionEntity rightEntity = (DummyExtensionEntity) level.getBlockEntity(rightPos);
            if (leftEntity != null && rightEntity != null) {
                leftEntity.setMainPos(pos);
                rightEntity.setMainPos(pos);
            } else {
                ResourceGenerator.LOGGER.error("Could not find block entity for Controller at {} AND {}", leftPos, rightPos);
            }
        }
    }


    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        if (facing.getAxis() == Direction.Axis.X) {
            return SHAPE[1];
        }
        return SHAPE[0];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        System.out.println("i've been hit");
        return InteractionResult.SUCCESS;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ControllerEntity(blockPos, blockState);
    }
}
