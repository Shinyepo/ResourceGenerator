package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Transmitter;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import dev.shinyepo.resourcegenerator.util.ItemStacksHandlerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.Nullable;

public class ItemPipeEntity extends Transmitter {
    private final ItemStacksResourceHandler itemHandler = ItemStacksHandlerUtil.createOutputOnlyHandler(1, this::setChanged);

    public ItemPipeEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ITEM_PIPE_ENTITY.get(), pos, blockState);

        configureSides(Direction.values());
    }

    public ResourceHandler<ItemResource> getItemCapability(@Nullable Direction direction) {
        return itemHandler;
    }
}
