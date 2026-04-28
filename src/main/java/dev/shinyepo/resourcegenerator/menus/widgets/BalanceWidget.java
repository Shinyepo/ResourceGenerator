package dev.shinyepo.resourcegenerator.menus.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class BalanceWidget extends AbstractMiscWidget {
    public BalanceWidget(Font font, int x, int y, Component message) {
        super(font, x, y, font.width(message) + 16 + 4, 16, message, "Balance: ");

        this.setUV(32, 124);
        this.setSpriteDimensions(16, 16);
    }

    @Override
    public void setMessage(@NonNull Long value) {
        super.setMessage(value);
    }
}
