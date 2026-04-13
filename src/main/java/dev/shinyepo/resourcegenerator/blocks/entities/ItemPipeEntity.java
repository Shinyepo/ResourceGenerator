package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Transmitter;
import dev.shinyepo.resourcegenerator.blocks.helpers.ItemPipeModeHelper;
import dev.shinyepo.resourcegenerator.controllers.ItemTransferNetworkController;
import dev.shinyepo.resourcegenerator.pipes.helpers.ItemPipeConnection;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import dev.shinyepo.resourcegenerator.util.ItemStacksHandlerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ItemPipeEntity extends Transmitter {
    private final ItemStacksResourceHandler itemHandler = ItemStacksHandlerUtil.createItemTransferHandler(this::onGettingItem, this::networkCanOutputItems);
    private final Set<BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction>> outputCache = new HashSet<>();
    private boolean isOutputCacheValid = false;

    public ItemPipeEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ITEM_PIPE_ENTITY.get(), pos, blockState);

        configureSides(Direction.values());
        calculateOutputCache();
    }

    private void onGettingItem() {
        if (this.level == null || this.level.isClientSide()) return;
        ServerLevel level = (ServerLevel) this.level;
        ItemTransferNetworkController controller = ItemTransferNetworkController.getInstance(level);

        UUID networkId = networkCapability.getNetworkId();
        try (Transaction tx = Transaction.openRoot()) {
            ResourceHandler<ItemResource> targetHandler = controller.getClosestOutput(networkId, worldPosition);
            if (targetHandler != null) {
                var result = ResourceHandlerUtil.move(itemHandler, targetHandler, _ -> true, 1, tx);
                if (result != 0) tx.commit();
                else tx.close();
            }
        }
    }

    private void calculateOutputCache() {
        if (this.level == null || this.level.isClientSide())
            return;
        outputCache.clear();
        this.invalidateCapabilities();
        BlockState currState = level.getBlockState(worldPosition);

        for (Direction direction : Direction.values()) {
            EnumProperty<ItemPipeConnection> propForDirection = ItemPipeModeHelper.getProp(direction);
            assert propForDirection != null;
            ItemPipeConnection connection = currState.getValue(propForDirection);
            if (connection == ItemPipeConnection.EXTRACT) {
                BlockPos relativePos = worldPosition.relative(direction);
                outputCache.add(
                        BlockCapabilityCache.create(
                                Capabilities.Item.BLOCK,
                                (ServerLevel) level,
                                relativePos,
                                direction.getOpposite(),
                                () -> !this.isRemoved(),
                                this::onCachedOutputChange
                        ));
            }
        }
        updateNetworkCapabilityCache();
        isOutputCacheValid = true;
    }

    public void onCachedOutputChange() {
        isOutputCacheValid = false;
    }

    @Override
    public void tick(ServerLevel level) {
        if (!isOutputCacheValid) calculateOutputCache();
        if (level.getGameTime() % 20 != 0) return;
    }

    public ResourceHandler<ItemResource> getItemCapability(@Nullable Direction direction) {
        if (direction == null) return null;
        assert level != null;
        BlockState state = level.getBlockState(worldPosition);

        var property = ItemPipeModeHelper.getProp(direction);
        assert property != null;

        if (state.getValue(property) == ItemPipeConnection.INSERT && networkCanOutputItems()) return itemHandler;

        return null;
    }

    private void updateNetworkCapabilityCache() {
        if (level == null || level.isClientSide()) return;
        ServerLevel level = (ServerLevel) this.level;
        UUID networkId = networkCapability.getNetworkId();
        if (networkId == null) return;

        ItemTransferNetworkController networkController = ItemTransferNetworkController.getInstance(level);
        networkController.addCapabilityCacheToNetwork(networkId, worldPosition, outputCache);
    }

    private boolean networkCanOutputItems() {
        if (level == null || level.isClientSide()) return false;
        ServerLevel level = (ServerLevel) this.level;

        ItemTransferNetworkController networkController = ItemTransferNetworkController.getInstance(level);
        UUID networkId = networkCapability.getNetworkId();

        if (networkId == null) return false;
        return networkController.canOutputItems(networkId);
    }
}
