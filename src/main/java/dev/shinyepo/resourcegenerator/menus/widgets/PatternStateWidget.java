package dev.shinyepo.resourcegenerator.menus.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public class PatternStateWidget extends AbstractMiscWidget {
    private final int v = 140;

    public PatternStateWidget(Font font, int x, int y) {
        super(font, x, y, 16, 16, Component.literal(""), "Pattern: ");

        this.setUV(State.INVALID.getU(), v);
        this.setSpriteDimensions(16, 16);
    }

    @Override
    public void setMessage(String message) {
        if (Objects.equals(message, "INVALID")) {
            setUV(State.INVALID.getU(), v);
        } else if (Objects.equals(message, "VALID")) {
            setUV(State.VALID.getU(), v);
        } else if (Objects.equals(message, "INVALID_ITEM")) {
            setUV(State.INVALID_ITEM.getU(), v);
        }
        super.updateTooltip(message);
    }

    public enum State {
        INVALID(0),
        INVALID_ITEM(16),
        VALID(32);

        private final int u;

        State(int u) {
            this.u = u;
        }

        public int getU() {
            return u;
        }
    }
}
