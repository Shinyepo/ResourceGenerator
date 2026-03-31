package dev.shinyepo.resourcegenerator.menus.consumer;

import dev.shinyepo.resourcegenerator.blocks.entities.BasicConsumerEntity;
import dev.shinyepo.resourcegenerator.menus.types.AbstractContainerBase;
import dev.shinyepo.resourcegenerator.networking.CustomMessages;
import dev.shinyepo.resourcegenerator.networking.packets.RequestPatternTierSyncC2S;
import dev.shinyepo.resourcegenerator.registries.BlockRegistry;
import dev.shinyepo.resourcegenerator.registries.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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

    public void syncPatternTier() {
        CustomMessages.sendToServer(new RequestPatternTierSyncC2S(consumerEntity.getBlockPos()));
    }

    public ItemStack getProduct() {
        return consumerEntity.getSyncData().getProduct();
    }

    public int getPatternTier() {
        return consumerEntity.getSyncData().getPatternTier();
    }

    public boolean getPatternValidState() {
        return consumerEntity.getSyncData().isPatternValid();
    }

    public boolean getProductValidState() {
        return consumerEntity.getSyncData().isProductValid();
    }

    public long getPrice() {
        return consumerEntity.getSyncData().getPrice();
    }
}
