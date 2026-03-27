package dev.shinyepo.resourcegenerator.menus.consumer;

import dev.shinyepo.resourcegenerator.blocks.entities.BasicConsumerEntity;
import dev.shinyepo.resourcegenerator.menus.types.AbstractContainerBase;
import dev.shinyepo.resourcegenerator.registries.BlockRegistry;
import dev.shinyepo.resourcegenerator.registries.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class ConsumerContainer extends AbstractContainerBase {
    private BasicConsumerEntity consumerEntity;

    public ConsumerContainer(int windowId, Player player, BlockPos pos) {
        super(MenuRegistry.CONSUMER_MENU.get(), windowId, pos, 1, 0, BlockRegistry.BASIC_CONSUMER.get());

        if (player.level().getBlockEntity(pos) instanceof BasicConsumerEntity isConsumerEntity) {
            this.consumerEntity = isConsumerEntity;

            addSlot(consumerEntity.getOutputHandler(), 0, 81, 36);
            layoutPlayerInventorySlots(player.getInventory());
        }
    }
}
