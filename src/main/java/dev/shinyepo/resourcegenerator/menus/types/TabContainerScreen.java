package dev.shinyepo.resourcegenerator.menus.types;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class TabContainerScreen<T extends ContainerBase> extends AbstractContainerScreen<T> {
    protected TabManager tabManager;

    public TabContainerScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        tabManager = new TabManager();
    }

    public void registerWidget(AbstractWidget widget) {
        this.addRenderableWidget(widget);
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
        List<Slot> backup = null;
        if (!tabManager.tabShouldRenderInventory()) {
            backup = new ArrayList<>(this.menu.slots);
            this.menu.slots.clear();
        }
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        tabManager.renderTooltips(graphics, leftPos, topPos, mouseX, mouseY);

        if (backup != null) {
            this.menu.slots.clear();
            this.menu.slots.addAll(backup);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = tabManager.handleClick(leftPos, topPos, mouseX, mouseY);
        if (result) {
            clearWidgets();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
        tabManager.displayTab(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        if (tabManager.tabShouldRenderInventory())
            super.renderSlot(guiGraphics, slot);
    }

    @Override
    protected void renderSlots(GuiGraphics guiGraphics) {
        if (tabManager.tabShouldRenderInventory())
            super.renderSlots(guiGraphics);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        if (tabManager.tabShouldRenderInventory())
            super.renderTooltip(guiGraphics, x, y);
    }

    @Override
    public @Nullable Slot getSlotUnderMouse() {
        if (tabManager.tabShouldRenderInventory())
            return super.getSlotUnderMouse();
        return null;
    }

    @Override
    protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, @Nullable String countString) {
        if (tabManager.tabShouldRenderInventory())
            super.renderSlotContents(guiGraphics, itemstack, slot, countString);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (tabManager.tabShouldRenderInventory())
            super.slotClicked(slot, slotId, mouseButton, type);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean handled = tabManager.handleScroll(mouseX, mouseY, scrollX, scrollY);
        if (handled) return true;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
