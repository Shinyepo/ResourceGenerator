package dev.shinyepo.resourcegenerator.blocks.types;

import com.mojang.serialization.MapCodec;
import dev.shinyepo.resourcegenerator.blocks.entities.types.NetworkDeviceEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.function.BiFunction;

public class HorizontalNetworkBlock extends NetworkBlock {
    protected HashMap<Direction, VoxelShape> SHAPES = new HashMap<>();
    protected static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public HorizontalNetworkBlock(BiFunction<BlockPos, BlockState, ? extends NetworkDeviceEntity> blockEntityFactory, Properties properties) {
        super(blockEntityFactory, properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = SHAPES.get(state.getValue(FACING));
        return shape == null ? SHAPES.get(Direction.NORTH) : shape;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = SHAPES.get(state.getValue(FACING));
        return shape == null ? SHAPES.get(Direction.NORTH) : shape;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }


}
