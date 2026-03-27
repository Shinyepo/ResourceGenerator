package dev.shinyepo.resourcegenerator.menus.consumer;

import dev.shinyepo.resourcegenerator.menus.types.AbstractScreenBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ConsumerScreen extends AbstractScreenBase<ConsumerContainer> {
    public ConsumerScreen(ConsumerContainer menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        createInventoryWidget();
        createCardSlotWidget();
        createSlotWidget(80, 35);
    }
}
