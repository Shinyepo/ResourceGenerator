package dev.shinyepo.resourcegenerator.menus.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class PatternTierWidget extends AbstractMiscWidget {
    private final int v = 140;
    private Runnable onClick;

    public PatternTierWidget(Font font, int x, int y) {
        super(font, x, y, 16, 16, Component.literal(""), "Tier: ");

        this.setUV(TierType.ONE.getU(), v);
        this.setSpriteDimensions(16, 16);
    }

    public PatternTierWidget(Font font, int x, int y, Runnable onClick) {
        this(font, x, y);
        this.onClick = onClick;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (onClick == null) return super.mouseClicked(event, doubleClick);
        onClick.run();
        return true;
    }

    @Override
    public void setMessage(Long value) {
        var u = 48;
        if (value - 1 > TierType.values().length || value - 1 < 0) {
            setUV(u, v);
            super.updateTooltip(value);
            return;
        }

        int tier = value.intValue();
        u = TierType.values()[tier - 1].getU();
        setUV(u, v);
        super.updateTooltip(value);
    }

    public enum TierType {
        ONE(48),
        TWO(64),
        THREE(80);

        private final int u;

        TierType(int u) {
            this.u = u;
        }

        public int getU() {
            return u;
        }
    }
}
