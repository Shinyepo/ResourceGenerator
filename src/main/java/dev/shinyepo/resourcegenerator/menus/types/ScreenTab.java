package dev.shinyepo.resourcegenerator.menus.types;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public abstract class ScreenTab<T extends ContainerBase, P extends TabContainerScreen<T>> extends GuiElement implements IScreenTab {
    private final String name;
    private final T menu;
    private final P parent;
    private final int index;
    private boolean isInventoryTab = false;

    public ScreenTab(String name, P parent, T menu, int index) {
        this.name = name;
        this.menu = menu;
        this.index = index;
        this.parent = parent;
    }

    public ScreenTab(String name, P parent, T menu, int index, boolean isInventoryTab) {
        this(name, parent, menu, index);
        this.isInventoryTab = isInventoryTab;
    }


    @Override
    public abstract void display(GuiGraphics graphics, int mouseX, int mouseY);

    @Override
    public abstract boolean handleScroll(double mouseX, double mouseY, double scrollX, double scrollY);

    @Override
    public abstract void renderTabTooltips(GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY);

    @Override
    public abstract ResourceLocation getActiveTexture();

    @Override
    public abstract ResourceLocation getInactiveTexture();

    public boolean isInventoryTab() {
        return isInventoryTab;
    }

    public void setInventoryTab(boolean inventoryTab) {
        isInventoryTab = inventoryTab;
    }

    public String getName() {
        return this.name;
    }

    protected T getMenu() {
        return this.menu;
    }

    public int getIndex() {
        return this.index;
    }

    protected P getParent() {
        return this.parent;
    }

    public Font getFont() {
        return this.parent.getFont();
    }
}
