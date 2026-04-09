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
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.Nullable;

public class ConsumerOutputEntity extends ConsumerStructureEntity {
    private BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction> consumerItemCache;
    private ItemStacksResourceHandler output;

    public ConsumerOutputEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.CONSUMER_OUTPUT_ENTITY.get(), blockPos, blockState);

        output = ItemStacksHandlerUtil.createOutputOnlyHandler(1, this::setChanged);
    }

    @Override
    public void assignConsumer(BlockPos consumerPos) {
        super.assignConsumer(consumerPos);
        assert this.level != null;
//        if (level.isClientSide()) return;

        consumerItemCache = BlockCapabilityCache.create(
                Capabilities.Item.BLOCK,
                (ServerLevel) level,
                consumerPos,
                Direction.DOWN);
    }

    public ResourceHandler<ItemResource> getItemCapability(@Nullable Direction direction) {
        return output;
//        return this.consumerItemCache.getCapability();
    }
}
