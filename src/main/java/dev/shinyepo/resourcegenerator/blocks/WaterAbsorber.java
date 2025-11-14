package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.WaterAbsorberEntity;
import dev.shinyepo.resourcegenerator.blocks.types.NetworkBlock;
import dev.shinyepo.resourcegenerator.properties.CustomProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class WaterAbsorber extends NetworkBlock {
    private static final BooleanProperty OPERATIONAL = CustomProperties.OPERATIONAL;

    public WaterAbsorber(Properties properties) {
        super(WaterAbsorberEntity::new, properties);

        SHAPE = Block.box(0, 0, 0, 16, 16, 16);

        registerDefaultState(getStateDefinition().any()
                .setValue(OPERATIONAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPERATIONAL);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return Objects.requireNonNull(super.getStateForPlacement(context))
                .setValue(OPERATIONAL, isSurroundedByFlowingWater(context.getLevel(), context.getClickedPos()));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, @NotNull Block neighborBlock, @NotNull Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide()) return;
        boolean foundFlowingWater = isSurroundedByFlowingWater(level, pos);

        if (state.getValue(OPERATIONAL) != foundFlowingWater) {
            level.setBlock(pos, state.setValue(OPERATIONAL, foundFlowingWater), Block.UPDATE_ALL);
        }
    }

    private boolean isSurroundedByFlowingWater(Level level, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(dir);
            FluidState fluid = level.getFluidState(neighborPos);

            if (fluid.getType() == Fluids.FLOWING_WATER) {
                return true;
            }
        }
        return false;
    }
}
