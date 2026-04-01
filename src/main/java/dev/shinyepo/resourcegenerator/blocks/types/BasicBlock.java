package dev.shinyepo.resourcegenerator.blocks.types;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.MapCodec;
import dev.shinyepo.resourcegenerator.blocks.entities.types.IDataEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.ITickableEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.IVerboseDataEntity;
import dev.shinyepo.resourcegenerator.menus.types.AbstractContainerBase;
import dev.shinyepo.resourcegenerator.registries.BlockTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.function.BiFunction;

public class BasicBlock extends Block implements EntityBlock {
    protected BiFunction<BlockPos, BlockState, BlockEntity> BLOCK_ENTITY;
    private Function4<Integer, Player, BlockPos, ContainerData, ? extends AbstractContainerBase> DATA_CONTAINER;
    private Function3<Integer, Player, BlockPos, ? extends AbstractContainerBase> BASIC_CONTAINER;
    public VoxelShape SHAPE;

    public BasicBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull MapCodec<? extends Block> codec() {
        return BlockTypeRegistry.BASIC_BLOCK.get();
    }

    @Override
    protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPE;
    }

    protected void setBlockEntity(BiFunction<BlockPos, BlockState, BlockEntity> blockEntity) {
        BLOCK_ENTITY = blockEntity;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return BLOCK_ENTITY.apply(blockPos, blockState);
    }

    protected void setDataContainerFactory(Function4<Integer, Player, BlockPos, ContainerData, ? extends AbstractContainerBase> factory) {
        DATA_CONTAINER = factory;
    }

    protected void setBasicContainerFactory(Function3<Integer, Player, BlockPos, ? extends AbstractContainerBase> factory) {
        BASIC_CONTAINER = factory;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return (entityLevel, entityPos, entityState, entityType) -> {
            if (entityType instanceof ITickableEntity tickableEntity) {
                tickableEntity.tick((ServerLevel) entityLevel);
            }
        };
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(@NonNull BlockState state, Level level, @NonNull BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (DATA_CONTAINER != null) {
            if (entity instanceof IDataEntity dataEntity) {
                return new SimpleMenuProvider(
                        (windowId, inv, player) ->
                                DATA_CONTAINER.apply(windowId, player, pos, dataEntity.getDataSlot()), Component.translatable(this.getDescriptionId())
                );
            }
        } else if (BASIC_CONTAINER != null) {
            return new SimpleMenuProvider(
                    (windowId, inv, player) ->
                            BASIC_CONTAINER.apply(windowId, player, pos), Component.translatable(this.getDescriptionId())
            );
        }
        return null;
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            MenuProvider menu = state.getMenuProvider(level, pos);
            if (menu != null) {
                if (level.getBlockEntity(pos) instanceof IVerboseDataEntity verboseDataEntity) {
                    verboseDataEntity.syncDataToClient(serverPlayer);
                }
                serverPlayer.openMenu(state.getMenuProvider(level, pos), buf -> buf.writeBlockPos(pos));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }
}
