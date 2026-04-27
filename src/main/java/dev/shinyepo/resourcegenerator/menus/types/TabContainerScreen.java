package dev.shinyepo.resourcegenerator.menus.types;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class TabContainerScreen<T extends AbstractContainerBase> extends AbstractContainerScreen<T> {
    protected TabManager tabManager;

    public TabContainerScreen(T menu, Inventory playerInventory, Component title, int imageWidth, int imageHeight) {
        super(menu, playerInventory, title, imageWidth, imageHeight);
        tabManager = new TabManager();
    }

    public void registerWidget(AbstractWidget widget) {
        this.addRenderableWidget(widget);
    }

    protected void createTabs(IScreenTab @NotNull ... tabs) {
        tabManager.createTabs(tabs);
    }

    protected void extractBackground(GuiGraphicsExtractor graphics, float v, int i, int i1) {
        tabManager.renderTabs(graphics, leftPos, topPos);
    }

    protected void configureFirstTab() {
        tabManager.init();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        List<Slot> backup = null;
        if (!tabManager.tabShouldRenderInventory()) {
            backup = new ArrayList<>(this.menu.slots);
            this.menu.slots.clear();
        }
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        this.extractTooltip(graphics, mouseX, mouseY);
        tabManager.renderTooltips(graphics, leftPos, topPos, mouseX, mouseY);

        if (backup != null) {
            this.menu.slots.clear();
            this.menu.slots.addAll(backup);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        var clickResult = tabManager.handleClick(leftPos, topPos, event.x(), event.y());

        return clickResult || super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
        tabManager.displayTab(graphics, mouseX, mouseY);
    }

    @Override
    protected void extractSlot(GuiGraphicsExtractor guiGraphics, Slot slot, int mouseX, int mouseY) {
        if (tabManager.tabShouldRenderInventory())
            super.extractSlot(guiGraphics, slot, mouseX, mouseY);
    }

    @Override
    protected void extractSlots(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if (tabManager.tabShouldRenderInventory())
            super.extractSlots(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
        if (tabManager.tabShouldRenderInventory())
            super.extractTooltip(guiGraphics, x, y);
    }

    @Override
    public @Nullable Slot getHoveredSlot() {
        if (tabManager.tabShouldRenderInventory())
            return super.getHoveredSlot();
        return null;
    }

    @Override
    protected void renderSlotContents(GuiGraphicsExtractor guiGraphics, ItemStack itemstack, Slot slot, @Nullable String countString) {
        if (tabManager.tabShouldRenderInventory())
            super.renderSlotContents(guiGraphics, itemstack, slot, countString);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ContainerInput containerInput) {
        if (tabManager.tabShouldRenderInventory())
            super.slotClicked(slot, slotId, mouseButton, containerInput);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean handled = tabManager.handleScroll(mouseX, mouseY, scrollX, scrollY);
        if (handled) return true;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
