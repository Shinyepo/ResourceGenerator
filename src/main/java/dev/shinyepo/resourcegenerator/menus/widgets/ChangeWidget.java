package dev.shinyepo.resourcegenerator.menus.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class ChangeWidget extends AbstractAccountWidget {
    private final int v = 124;
    private final int gainU = 0;
    private final int lossU = 16;
    private final int noChangeU = 64;

    private long cachedChange;

    public ChangeWidget(Font font, int x, int y, Component message) {
        super(font, x, y, font.width(message) + 16 + 4, 16, message, "Change: ");

        this.setUV(0, 124);
        this.setSpriteDimensions(16, 16);
    }

    @Override
    public void setMessage(@NonNull Long value) {
        if (value == cachedChange) return;
        cachedChange = value;
        super.setMessage(value);
        if (value > 0) {
            setUV(gainU, v);
        } else if (value < 0) {
            setUV(lossU, v);
        } else {
            setUV(noChangeU, v);
        }
    }
}
