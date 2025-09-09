package dev.shinyepo.resourcegenerator.blocks.types;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.MapCodec;
import dev.shinyepo.resourcegenerator.blocks.entities.types.IDataEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.NetworkDeviceEntity;
import dev.shinyepo.resourcegenerator.menus.types.ContainerBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.BiFunction;

public class HorizontalNetworkBlock extends Block implements EntityBlock {
    private final BiFunction<BlockPos, BlockState, ? extends NetworkDeviceEntity> BLOCK_ENTITY;
    private HashMap<Direction, VoxelShape> SHAPES = new HashMap<>();
    private static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private Function4<Integer, Player, BlockPos, DataSlot, ? extends ContainerBase> DATA_CONTAINER;

    public HorizontalNetworkBlock(BiFunction<BlockPos, BlockState, ? extends NetworkDeviceEntity> blockEntityFactory, Properties properties) {
        super(properties);
        BLOCK_ENTITY = blockEntityFactory;

        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.NORTH));
    }

    protected void setShape(HashMap<Direction, VoxelShape> shape) {
        SHAPES = shape;
    }

    protected void setShape(VoxelShape shape) {
        SHAPES.put(Direction.NORTH, shape);
    }

    protected void setDataContainerFactory(Function4<Integer, Player, BlockPos, DataSlot, ? extends ContainerBase> factory) {
        DATA_CONTAINER = factory;
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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(state.getMenuProvider(level, pos), buf -> buf.writeBlockPos(pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return BLOCK_ENTITY.apply(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return (entityLevel, entityPos, entityState, entityType) -> {
            if (entityType instanceof NetworkDeviceEntity networkDevice) {
                networkDevice.tick();
            }
        };
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (DATA_CONTAINER != null) {
            if (entity instanceof IDataEntity dataEntity) {
                return new SimpleMenuProvider(
                        (windowId, inv, player) ->
                                DATA_CONTAINER.apply(windowId, player, pos, dataEntity.getDataSlot()), Component.translatable(this.getDescriptionId())
                );
            }
        }
        return null;
    }
}
