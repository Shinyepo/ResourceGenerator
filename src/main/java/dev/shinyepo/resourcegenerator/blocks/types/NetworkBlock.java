package dev.shinyepo.resourcegenerator.blocks.types;

import com.mojang.serialization.MapCodec;
import dev.shinyepo.resourcegenerator.controllers.DeviceNetworkController;
import dev.shinyepo.resourcegenerator.registries.BlockTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class NetworkBlock extends BasicBlock {
    public NetworkBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull MapCodec<? extends Block> codec() {
        super.codec();
        return BlockTypeRegistry.NETWORK_BLOCK.get();
    }

    @Override
    public void setPlacedBy(@NonNull Level level, @NonNull BlockPos pos, @NonNull BlockState state, @Nullable LivingEntity placer, @NonNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            DeviceNetworkController networkController = DeviceNetworkController.getInstance((ServerLevel) level);
            networkController.handleNetworkOnPlace((ServerLevel) level, pos);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull ItemStack toolStack, boolean willHarvest, @NonNull FluidState fluid) {
        if (level.isClientSide())
            return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);

        DeviceNetworkController networkController = DeviceNetworkController.getInstance((ServerLevel) level);
        networkController.handleNetworkOnDestroy((ServerLevel) level, pos);

        return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
    }
}
