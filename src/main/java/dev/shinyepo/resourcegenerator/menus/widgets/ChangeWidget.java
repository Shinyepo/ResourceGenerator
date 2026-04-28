package dev.shinyepo.resourcegenerator.menus.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class ChangeWidget extends AbstractMiscWidget {
    public ChangeWidget(Font font, int x, int y, Component message) {
        this(font, x, y, message, "Change: ", ChangeType.NO_CHANGE);
    }

    public ChangeWidget(Font font, int x, int y, Component message, ChangeType type) {
        this(font, x, y, message, "Change: ", type);
    }

    public ChangeWidget(Font font, int x, int y, Component message, String tooltipPrefix) {
        this(font, x, y, message, tooltipPrefix, ChangeType.NO_CHANGE);
    }

    public ChangeWidget(Font font, int x, int y, Component message, String tooltipPrefix, ChangeType type) {
        super(font, x, y, font.width(message) + 16 + 4, 16, message, tooltipPrefix);

        this.setUV(type.getU(), type.getV());
        this.setSpriteDimensions(16, 16);
    }

    @Override
    public void setMessage(@NonNull Long value) {
        super.setMessage(value);
        if (value > 0) {
            setUV(ChangeType.GAIN.getU(), ChangeType.GAIN.getV());
        } else if (value < 0) {
            setUV(ChangeType.LOSS.getU(), ChangeType.GAIN.getV());
        } else {
            setUV(ChangeType.NO_CHANGE.getU(), ChangeType.GAIN.getV());
        }
    }

    public enum ChangeType {
        GAIN(0, 124),
        LOSS(16, 124),
        NO_CHANGE(64, 124);

        private final int u;
        private final int v;

        ChangeType(int u, int v) {
            this.u = u;
            this.v = v;
        }

        public int getU() {
            return u;
        }

        public int getV() {
            return v;
        }
    }
}
