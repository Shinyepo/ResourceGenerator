package dev.shinyepo.resourcegenerator.blocks.types;

import dev.shinyepo.resourcegenerator.controllers.DeviceNetworkController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class NetworkBlock extends BasicBlock {
    public NetworkBlock(BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory, Properties properties) {
        super(blockEntityFactory, properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            DeviceNetworkController networkController = DeviceNetworkController.getInstance((ServerLevel) level);
            networkController.handleNetworkOnPlace((ServerLevel) level, pos);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest, FluidState fluid) {
        if (level.isClientSide())
            return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);

        DeviceNetworkController networkController = DeviceNetworkController.getInstance((ServerLevel) level);
        networkController.handleNetworkOnDestroy((ServerLevel) level, pos);

        return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
    }
}
