package dev.shinyepo.resourcegenerator.menus.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class OwnerWidget extends AbstractMiscWidget {
    public OwnerWidget(Font font, int x, int y, Component message) {
        super(font, x, y, font.width(message) + 16 + 4, 16, message, "Owner: ");

        this.setUV(48, 124);
        this.setSpriteDimensions(16, 16);
    }
}
