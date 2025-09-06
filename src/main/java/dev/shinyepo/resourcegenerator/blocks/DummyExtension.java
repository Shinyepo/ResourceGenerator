package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.DummyExtensionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DummyExtension extends Block implements EntityBlock {
    public DummyExtension(ResourceLocation registry) {
        super(BlockBehaviour.Properties.of().noOcclusion().pushReaction(PushReaction.BLOCK).setId(ResourceKey.create(Registries.BLOCK, registry)));

        registerDefaultState(getStateDefinition().any());
    }

    private BlockPos getMainBlockPos(BlockGetter level, BlockPos pos) {
        DummyExtensionEntity blockEntity = (DummyExtensionEntity) level.getBlockEntity(pos);
        if (blockEntity != null) {
            return blockEntity.getMainPos();
        }
        return null;
    }


    @Override
    protected boolean canBeReplaced(BlockState state, Fluid fluid) {
        return false;
    }

    private VoxelShape proxyShape(BlockGetter level, BlockPos pos, CollisionContext context, ShapeProxy proxy) {
        BlockPos mainPos = getMainBlockPos(level, pos);
        if (mainPos == null) return Shapes.empty();
        BlockState mainState = level.getBlockState(mainPos);
        VoxelShape mainShape = proxy.getShape(mainState, level, pos, context);
        BlockPos offset = pos.subtract(mainPos);

        return mainShape.move(-offset.getX(), -offset.getY(), -offset.getZ());
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return proxyShape(level, pos, context, BlockStateBase::getShape);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return proxyShape(level, pos, context, BlockStateBase::getCollisionShape);
    }

    private interface ShapeProxy {

        VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos mainPos = getMainBlockPos(level, pos);
        if (mainPos != null) {
            BlockState mainBlockState = level.getBlockState(mainPos);

            return mainBlockState.useWithoutItem(level, player, hitResult.withPosition(mainPos));
        }
        return InteractionResult.FAIL;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (willHarvest) {
            return true;
        }
        BlockPos mainPos = getMainBlockPos(level, pos);
        if (mainPos != null) {
            BlockState mainState = level.getBlockState(mainPos);
            if (!mainState.isAir()) {
                //Set the main block to air, which will invalidate the rest of the bounding blocks
                mainState.onDestroyedByPlayer(level, mainPos, player, false, mainState.getFluidState());
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, false, fluid);
    }

    @NotNull
    @Override
    public BlockState playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        BlockPos mainPos = getMainBlockPos(level, pos);
        if (mainPos != null) {
            BlockState mainState = level.getBlockState(mainPos);
            if (!mainState.isAir()) {
                mainState.getBlock().playerWillDestroy(level, mainPos, mainState, player);
                return state;
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        BlockPos mainPos = getMainBlockPos(level, pos);
        if (mainPos != null) {
            BlockState mainState = level.getBlockState(mainPos);
            mainState.getBlock().playerDestroy(level, player, mainPos, mainState, level.getBlockEntity(mainPos), tool);
        } else {
            super.playerDestroy(level, player, pos, state, blockEntity, tool);
        }
        level.removeBlock(pos, false);
    }


    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return getStateDefinition().any();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DummyExtensionEntity(blockPos, blockState);
    }
}
