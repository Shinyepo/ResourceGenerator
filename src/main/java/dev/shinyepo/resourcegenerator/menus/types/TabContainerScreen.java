package dev.shinyepo.resourcegenerator.menus.types;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public abstract class TabContainerScreen<T extends ContainerBase> extends AbstractContainerScreen<T> {
    protected TabManager tabManager;

    public TabContainerScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        tabManager = new TabManager();
    }

    protected void createTabs(IScreenTab @NotNull ... tabs) {
        tabManager.createTabs(tabs);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float v, int i, int i1) {
        tabManager.renderTabs(graphics, leftPos, topPos);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        tabManager.renderTooltips(graphics, leftPos, topPos, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = tabManager.handleClick(leftPos, topPos, mouseX, mouseY);
        if (result) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        tabManager.displayTab(graphics);
    }
}
