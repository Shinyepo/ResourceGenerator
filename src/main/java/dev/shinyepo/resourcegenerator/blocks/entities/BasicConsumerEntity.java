package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Consumer;
import dev.shinyepo.resourcegenerator.configs.ConsumerConfig;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import dev.shinyepo.resourcegenerator.util.ItemStacksHandlerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.List;

public class BasicConsumerEntity extends Consumer {
    private final ItemStacksResourceHandler cardHandler;
    private final ItemStacksResourceHandler outputHandler;

    public BasicConsumerEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.BASIC_CONSUMER_ENTITY.get(), ConsumerConfig.BASIC_CONSUMER, pos, blockState);

        cardHandler = ItemStacksHandlerUtil.createInputItemHandler(1, this::setChanged, List.of(TagRegistry.ID_CARDS));
        outputHandler = ItemStacksHandlerUtil.createOutputOnlyHandler(1, this::setChanged);

        configureSides(Direction.DOWN);
    }

    @Override
    public void tick(ServerLevel level) {
    }

    public ItemStacksResourceHandler getCardHandler() {
        return cardHandler;
    }

    public ItemStacksResourceHandler getOutputHandler() {
        return outputHandler;
    }
}
