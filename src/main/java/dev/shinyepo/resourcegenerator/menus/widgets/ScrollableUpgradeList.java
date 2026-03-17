package dev.shinyepo.resourcegenerator.menus.widgets;

import dev.shinyepo.resourcegenerator.data.Upgrade;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.NeoForgeMod;

public class ScrollableUpgradeList extends ObjectSelectionList<ScrollableUpgradeList.UpgradeEntry> {
    private final int listWidth;
    private ControllerScreen parent;

    private static final Identifier VERSION_CHECK_ICONS = Identifier.fromNamespaceAndPath(NeoForgeMod.MOD_ID, "textures/gui/version_check_icons.png");

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
        return getRowLeft() + listWidth - 2;
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
        public void renderContent(GuiGraphics graphics, int top, int left, boolean isMouseOver, float partialTick) {
            MutableComponent mainText = Component.translatable("gui." + upgrade.id().toLanguageKey());
            Font font = parent.getFont();

//            var pose = graphics.pose();
//            pose.pushMatrix();
//            pose.translate(left, top + 3);
//            pose.scale(0.65F);

            graphics.drawString(font, Component.literal("Owner: "), 0, 0, 0xFF800000, false);


            graphics.drawString(font, mainText, 0, 0, 0xFF800000, false);
//            pose.popMatrix();
            graphics.blit(RenderPipelines.GUI_TEXTURED, VERSION_CHECK_ICONS, getX() + width - 12, top + 14 / 4, 8, 8, 0, 8, 8, 64, 16);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
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
