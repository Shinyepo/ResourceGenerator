package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.blocks.entities.ControllerEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.DummyExtensionEntity;
import dev.shinyepo.resourcegenerator.blocks.types.HorizontalNetworkBlock;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
import dev.shinyepo.resourcegenerator.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

@ParametersAreNonnullByDefault
public class Controller extends HorizontalNetworkBlock {
    private static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final HashMap<Direction, VoxelShape> SHAPE = new HashMap<>();

    static {
        SHAPE.put(Direction.NORTH, Shapes.or(
                Block.box(-15.5, 0.5, 0.75, 31.5, 15.5, 16),
                Block.box(-15.99, 0, 0.01, -13.99, 15.99, 2.01),
                Block.box(-15.99, 0, 14.01, -13.99, 15.99, 16.01),
                Block.box(29.99, 0, 0.01, 31.99, 15.99, 2.01),
                Block.box(29.99, 0, 14.01, 31.99, 15.99, 16.01),
                Block.box(-16, 15, 0, 32, 16, 16.02)
        ));
        SHAPE.put(Direction.WEST, Shapes.or(
                Block.box(0.01, 0, -15.99, 2.01, 16, -13.99),
                Block.box(13.99, 0, -15.99, 15.99, 16, -13.99),
                Block.box(13.99, 0, 29.99, 15.99, 16, 31.99),
                Block.box(0.01, 0, 29.99, 2.01, 16, 31.99),
                Block.box(0.75, 0.5, -15.5, 15.98, 15, 31.5),
                Block.box(0, 15, -16, 16, 16.01, 32)
        ));
    }

    public Controller(Properties properties) {
        super(ControllerEntity::new, properties);
        setDataContainerFactory(ControllerContainer::new);
        setShape(SHAPE);
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
}
