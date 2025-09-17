package dev.shinyepo.resourcegenerator.blocks.types;

import com.mojang.datafixers.util.Function4;
import dev.shinyepo.resourcegenerator.blocks.entities.types.IDataEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.INetworkDevice;
import dev.shinyepo.resourcegenerator.blocks.entities.types.NetworkDeviceEntity;
import dev.shinyepo.resourcegenerator.capabilities.INetworkCapability;
import dev.shinyepo.resourcegenerator.configs.SideConfig;
import dev.shinyepo.resourcegenerator.controllers.DeviceNetworkController;
import dev.shinyepo.resourcegenerator.menus.types.ContainerBase;
import dev.shinyepo.resourcegenerator.registries.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

public class NetworkBlock extends Block implements EntityBlock {
    private final BiFunction<BlockPos, BlockState, ? extends NetworkDeviceEntity> BLOCK_ENTITY;
    private Function4<Integer, Player, BlockPos, DataSlot, ? extends ContainerBase> DATA_CONTAINER;
    public VoxelShape SHAPE;

    public NetworkBlock(BiFunction<BlockPos, BlockState, ? extends NetworkDeviceEntity> blockEntityFactory, Properties properties) {
        super(properties);
        BLOCK_ENTITY = blockEntityFactory;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return BLOCK_ENTITY.apply(blockPos, blockState);
    }

    protected void setDataContainerFactory(Function4<Integer, Player, BlockPos, DataSlot, ? extends ContainerBase> factory) {
        DATA_CONTAINER = factory;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return (entityLevel, entityPos, entityState, entityType) -> {
            if (entityType instanceof NetworkDeviceEntity networkDevice) {
                networkDevice.tick((ServerLevel) entityLevel);
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            MenuProvider menu = state.getMenuProvider(level, pos);
            if (menu != null) {
                serverPlayer.openMenu(state.getMenuProvider(level, pos), buf -> buf.writeBlockPos(pos));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return InteractionResult.PASS;
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

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            INetworkCapability networkCapability = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, pos, null);
            INetworkDevice device = (INetworkDevice) level.getBlockEntity(pos);
            if (networkCapability != null) {
                SideConfig[] sides = networkCapability.getSides();
                List<UUID> networkIds = new ArrayList<>();
                for (int i = 0; i < sides.length; i++) {
                    if (sides[i] == SideConfig.NETWORK) {
                        Direction direction = Direction.values()[i];
                        BlockPos relPos = pos.relative(direction);
                        INetworkCapability relCapability = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, relPos, direction.getOpposite());
                        if (relCapability != null) {
                            networkIds.add(relCapability.getNetworkId());
                        }
                    }
                }
                DeviceNetworkController networkController = DeviceNetworkController.getInstance((ServerLevel) level);
                UUID networkId = null;
                if (networkIds.isEmpty()) {
                    networkId = networkController.createNewNetwork(level.dimension(), device, pos);
                } else if (networkIds.size() == 1) {
                    networkId = networkController.addDeviceToNetwork(networkIds.getFirst(), device, pos);
                } else {
                    //TODO: implement merging networks
                    networkId = networkController.mergeNetworks(networkIds, device, pos, (ServerLevel) level);
                }

                networkCapability.setNetworkId(networkId);
            }
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level.isClientSide()) return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        INetworkDevice device = (INetworkDevice) level.getBlockEntity(pos);
        INetworkCapability networkCapability = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, pos, null);
        if (networkCapability != null) {
            DeviceNetworkController networkController = DeviceNetworkController.getInstance((ServerLevel) level);
            networkController.removeDeviceFromNetwork(networkCapability.getNetworkId(), device, pos);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
