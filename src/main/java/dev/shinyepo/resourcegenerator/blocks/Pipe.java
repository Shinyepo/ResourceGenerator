package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.PipeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Pipe extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(Block.box(7, 7, 0, 9, 9, 16),
            Block.box(7, 0, 7, 9, 7, 9),
            Block.box(7, 9, 7, 9, 16, 9),
            Block.box(9, 7, 7, 16, 9, 9),
            Block.box(0, 7, 7, 7, 9, 9));

    public Pipe(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PipeEntity(blockPos, blockState);
    }
}
