package dev.shinyepo.resourcegenerator.menus.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class InventoryWidget extends AbstractMiscWidget {
    public InventoryWidget(Font font, int x, int y) {
        super(font, x, y, 162, 76, Component.literal(""));

        this.setSpriteDimensions(162, 76);
    }

    //Override to make it possible to move items...
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }
}
