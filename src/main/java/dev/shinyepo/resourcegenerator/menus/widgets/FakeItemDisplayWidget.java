package dev.shinyepo.resourcegenerator.menus.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class FakeItemDisplayWidget extends AbstractMiscWidget {
    private ItemStack itemStack;
    private final Runnable onClick;

    public FakeItemDisplayWidget(Font font, int x, int y, Runnable onClick) {
        super(font, x, y, 18, 18, Component.literal(""));
        this.onClick = onClick;
        this.setSpriteDimensions(18, 18);
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    private void displayItem(GuiGraphicsExtractor graphics) {
        if (itemStack == null) return;
        graphics.fakeItem(itemStack, getX() + 1, getY() + 1);
        graphics.itemDecorations(font, itemStack, getX() + 1, getY() + 1);
    }

    private void displayTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (itemStack == null || !isHovered()) return;
        graphics.setTooltipForNextFrame(font, itemStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (onClick != null) onClick.run();
        return true;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTicks);
        displayItem(graphics);
        displayTooltip(graphics, mouseX, mouseY);
    }
}
