package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.ConsumerStructureEntity;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import dev.shinyepo.resourcegenerator.util.ItemStacksHandlerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;

public class ConsumerOutputEntity extends ConsumerStructureEntity {
    private BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction> consumerItemCache;
    private final ItemStacksResourceHandler output;

    private final HashMap<BlockPos, BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction>> adjacentItemCaches = new HashMap<>();
    private boolean isAdjacentCacheValid = false;

    public ConsumerOutputEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.CONSUMER_OUTPUT_ENTITY.get(), blockPos, blockState);

        output = ItemStacksHandlerUtil.createOutputOnlyHandler(1, this::setChanged);
    }

    @Override
    public void assignConsumer(BlockPos consumerPos) {
        super.assignConsumer(consumerPos);
        assert this.level != null;

        consumerItemCache = BlockCapabilityCache.create(
                Capabilities.Item.BLOCK,
                (ServerLevel) level,
                consumerPos,
                Direction.DOWN);
    }

    private void calculateAdjacentCaches() {
        if (this.level == null || this.level.isClientSide()) return;
        adjacentItemCaches.clear();

        for (Direction dir : Direction.values()) {
            BlockPos relativePos = worldPosition.relative(dir);
            ResourceHandler<ItemResource> relativeCap = this.level.getCapability(Capabilities.Item.BLOCK, relativePos, dir.getOpposite());
            if (relativeCap != null) {
                adjacentItemCaches.put(relativePos, BlockCapabilityCache.create(
                        Capabilities.Item.BLOCK,
                        (ServerLevel) level,
                        relativePos,
                        dir.getOpposite()));
            }
        }
        isAdjacentCacheValid = true;
    }

    public void invalidateAdjacentCache() {
        isAdjacentCacheValid = false;
    }

    @Override
    public void tick(ServerLevel level) {
        if (!isAdjacentCacheValid) calculateAdjacentCaches();
        if (level.getGameTime() % 20 != 0) return;
        if (consumerItemCache != null) {
            pushItemsToAdjacent();
        }
    }

    private void pushItemsToAdjacent() {
        if (adjacentItemCaches.isEmpty()) return;
        ResourceHandler<ItemResource> consumerCap = consumerItemCache.getCapability();
        if (consumerCap == null) return;
        adjacentItemCaches.forEach((pos, cache) -> {
            ResourceHandler<ItemResource> cap = cache.getCapability();
            if (cap == null) {
                invalidateAdjacentCache();
                return;
            }

            try (Transaction tx = Transaction.openRoot()) {
                var result = ResourceHandlerUtil.move(consumerCap, cap, _ -> true, 1, tx);
                if (result == 0) {
                    tx.close();
                    return;
                }
                tx.commit();
            }

        });
    }

    public ResourceHandler<ItemResource> getItemCapability(@Nullable Direction direction) {
        if (this.level.isClientSide()) {
            return output;
        }
        if (this.consumerItemCache == null) return null;
        return this.consumerItemCache.getCapability();
    }
}
