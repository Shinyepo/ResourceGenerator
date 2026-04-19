package dev.shinyepo.resourcegenerator.menus.market;

import dev.shinyepo.resourcegenerator.menus.types.AbstractScreenBase;
import dev.shinyepo.resourcegenerator.menus.types.GuiElement;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MarketScreen extends AbstractScreenBase<MarketContainer> {
    public MarketScreen(MarketContainer menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        createInventoryWidget();
        createCardSlotWidget();
        createSlotWidget(85, 35);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);
        graphics.text(this.font, this.menu.getOwnerName(), 16, 30, GuiElement.BASIC.getColor(), false);
    }
}
