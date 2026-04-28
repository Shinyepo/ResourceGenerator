package dev.shinyepo.resourcegenerator.menus.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class SlotWidget extends AbstractMiscWidget {
    public SlotWidget(Font font, int x, int y) {
        super(font, x, y, 18, 18, Component.literal(""));

        this.setSpriteDimensions(18, 18);
    }
}
