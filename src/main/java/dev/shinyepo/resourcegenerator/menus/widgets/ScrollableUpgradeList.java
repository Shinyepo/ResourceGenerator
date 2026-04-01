package dev.shinyepo.resourcegenerator.menus.widgets;

import dev.shinyepo.resourcegenerator.data.Upgrade;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

public class ScrollableUpgradeList extends ObjectSelectionList<ScrollableUpgradeList.UpgradeEntry> {
    private final int listWidth;
    private final ControllerScreen parent;

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
        return listWidth;
    }

    @Override
    protected int scrollBarX() {
        return getRight();
    }

    public class UpgradeEntry extends ObjectSelectionList.Entry<UpgradeEntry> {
        private final ControllerScreen parent;
        private final Upgrade upgrade;

        public UpgradeEntry(Upgrade upgrade, ControllerScreen parent) {
            this.upgrade = upgrade;
            this.parent = parent;
        }

        @Override
        public @NonNull Component getNarration() {
            return Component.empty();
        }

        public Upgrade getUpgrade() {
            return this.upgrade;
        }


        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            MutableComponent mainText = Component.translatable("gui." + upgrade.id().toLanguageKey());
            Font font = parent.getFont();
            int left = getContentX();
            int top = getContentY();

            graphics.text(font, mainText, left + 3, top + 2, 0xFF800000, false);
        }

        @Override
        public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
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
