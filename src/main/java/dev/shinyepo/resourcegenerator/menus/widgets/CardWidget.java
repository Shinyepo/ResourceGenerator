package dev.shinyepo.resourcegenerator.menus.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class CardWidget extends AbstractMiscWidget {
    public CardWidget(Font font, int x, int y) {
        super(font, x, y, 18, 18, Component.literal(""));

        this.setUV(162, 0);
        this.setSpriteDimensions(18, 18);
    }

    //Override to make it possible to move items...
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }
}
