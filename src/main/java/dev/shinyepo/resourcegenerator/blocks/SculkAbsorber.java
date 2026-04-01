package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.SculkAbsorberEntity;
import dev.shinyepo.resourcegenerator.blocks.types.NetworkBlock;
import dev.shinyepo.resourcegenerator.properties.CustomProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SculkAbsorber extends NetworkBlock {
    private static final BooleanProperty OPERATIONAL = CustomProperties.OPERATIONAL;

    public SculkAbsorber(Properties properties) {
        super(properties);

        SHAPE = Block.box(0, 0, 0, 16, 16, 16);

        setBlockEntity(SculkAbsorberEntity::new);


        registerDefaultState(getStateDefinition().any()
                .setValue(OPERATIONAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPERATIONAL);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NonNull BlockPlaceContext context) {
        return Objects.requireNonNull(super.getStateForPlacement(context))
                .setValue(OPERATIONAL, isNextToSculkBlock(context.getLevel(), context.getClickedPos()));
    }

    @Override
    public void neighborChanged(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NotNull Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide()) return;
        boolean isNextToSculk = isNextToSculkBlock(level, pos);

        if (state.getValue(OPERATIONAL) != isNextToSculk) {
            level.setBlock(pos, state.setValue(OPERATIONAL, isNextToSculk), Block.UPDATE_ALL);
        }
    }

    private boolean isNextToSculkBlock(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        Set<BlockPos> sculkPositions = new HashSet<>();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState blockState = level.getBlockState(neighborPos);

            if (blockState.is(Blocks.SCULK)) {
                sculkPositions.add(neighborPos);
            }
        }
        if (blockEntity instanceof SculkAbsorberEntity sculkAbsorber)
            sculkAbsorber.setSculkPositions(sculkPositions);
        return !sculkPositions.isEmpty();
    }
}
