package dev.shinyepo.resourcegenerator.menus.consumer;

import dev.shinyepo.resourcegenerator.blocks.entities.BasicConsumerEntity;
import dev.shinyepo.resourcegenerator.menus.types.AbstractContainerBase;
import dev.shinyepo.resourcegenerator.networking.CustomMessages;
import dev.shinyepo.resourcegenerator.networking.packets.RequestPatternTierSyncC2S;
import dev.shinyepo.resourcegenerator.registries.BlockRegistry;
import dev.shinyepo.resourcegenerator.registries.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class ConsumerContainer extends AbstractContainerBase {
    private BasicConsumerEntity consumerEntity;
    private final ContainerData data;

    public ConsumerContainer(int windowId, Player player, BlockPos pos) {
        this(windowId, player, pos, new SimpleContainerData(2));
    }

    public ConsumerContainer(int windowId, Player player, BlockPos pos, ContainerData data) {
        super(MenuRegistry.CONSUMER_MENU.get(), windowId, pos, 1, 0, BlockRegistry.BASIC_CONSUMER.get());
        this.data = data;
        if (player.level().getBlockEntity(pos) instanceof BasicConsumerEntity isConsumerEntity) {
            this.consumerEntity = isConsumerEntity;

            addSlot(consumerEntity.getOutputHandler(), 0, 81, 36);
            addDataSlots(data);
            layoutPlayerInventorySlots(player.getInventory());
        }
    }

    public void syncPatternTier() {
        CustomMessages.sendToServer(new RequestPatternTierSyncC2S(consumerEntity.getBlockPos()));
    }

    public int getTier() {
        return data.get(0);
    }

    public int getValidState() {
        return data.get(1);
    }

    public void setTier(int tier) {
        data.set(0, tier);
    }
}
