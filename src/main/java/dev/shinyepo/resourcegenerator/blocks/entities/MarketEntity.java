package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import dev.shinyepo.resourcegenerator.util.ItemStacksHandlerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;

public class MarketEntity extends BlockEntity {
    private final ResourceHandler<ItemResource> outputHandler = ItemStacksHandlerUtil.createOutputOnlyHandler(1, this::setChanged);

    public MarketEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.MARKET_ENTITY.get(), worldPosition, blockState);
    }

    public ResourceHandler<ItemResource> getItemCapability(@Nullable Direction direction) {
        return direction == Direction.DOWN ? outputHandler : null;
    }
}
