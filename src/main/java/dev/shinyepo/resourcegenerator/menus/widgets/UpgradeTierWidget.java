package dev.shinyepo.resourcegenerator.menus.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class UpgradeTierWidget extends AbstractMiscWidget {
    private final int v = 124;

    public UpgradeTierWidget(Font font, int x, int y, Component message) {
        super(font, x, y, font.width(message) + 16 + 4, 16, message, "Tier: ");

        this.setUV(TierType.NORMAL.getU(), v);
        this.setSpriteDimensions(16, 16);
    }

    @Override
    public void setMessage(String value) {
        String current = value.split("/")[0];
        String maxTier = value.split("/")[1];
        if (current.equals(maxTier)) {
            setUV(TierType.MAX_TIER.getU(), v);
        } else {
            setUV(TierType.NORMAL.getU(), v);
        }
        super.setMessage(value);
    }

    public enum TierType {
        NORMAL(80),
        MAX_TIER(96);

        private final int u;

        TierType(int u) {
            this.u = u;
        }

        public int getU() {
            return u;
        }
    }
}
