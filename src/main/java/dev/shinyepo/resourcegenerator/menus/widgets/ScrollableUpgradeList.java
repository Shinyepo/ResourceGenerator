package dev.shinyepo.resourcegenerator.menus.widgets;

import dev.shinyepo.resourcegenerator.data.Upgrade;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ScrollableUpgradeList extends ObjectSelectionList<ScrollableUpgradeList.UpgradeEntry> {
    private final int listWidth;
    private ControllerScreen parent;

    public ScrollableUpgradeList(ControllerScreen parent, int listWidth, int top, int bottom) {
        super(Minecraft.getInstance(), listWidth, bottom - top, top, 14);
        this.listWidth = listWidth;
        this.parent = parent;
        refreshList();
    }

    public void refreshList() {
        this.clearEntries();
        this.parent.buildList(this::addEntry, (upgrade) -> new UpgradeEntry(upgrade, this.parent));
    }

    @Override
    public int getRowWidth() {
        return listWidth - 8;
    }

    @Override
    protected int scrollBarX() {
        return getRowLeft() + listWidth - 6;
    }

    public class UpgradeEntry extends ObjectSelectionList.Entry<UpgradeEntry> {
        private ControllerScreen parent;
        private Upgrade upgrade;

        public UpgradeEntry(Upgrade upgrade, ControllerScreen parent) {
            this.upgrade = upgrade;
            this.parent = parent;
        }

        @Override
        public Component getNarration() {
            return Component.empty();
        }

        public Upgrade getUpgrade() {
            return this.upgrade;
        }

        @Override
        public void render(GuiGraphics graphics, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            MutableComponent mainText = Component.translatable("gui." + upgrade.id().toLanguageKey());
            Font font = parent.getFont();

            var pose = graphics.pose();
            pose.pushMatrix();
            pose.translate(left, top + 3);
            pose.scale(0.65F);

            graphics.drawString(font, mainText, 0, 0, 0xFF404040, false);
            pose.popMatrix();
        }

        @Override
        public boolean mouseClicked(double p_331676_, double p_330254_, int p_331536_) {
            this.parent.setSelected(this.isFocused() ? null : this);
            ScrollableUpgradeList.this.setSelected(this.isFocused() ? null : this);
            return false;
        }

        @Override
        public void setFocused(boolean focused) {
            if (focused) {
                this.parent.setSelected(this);
                ScrollableUpgradeList.this.setSelected(this);
            }
        }

        @Override
        public boolean isFocused() {
            return ScrollableUpgradeList.this.getSelected() == this;
        }
    }
}
