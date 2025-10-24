package dev.shinyepo.resourcegenerator.menus.types;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public abstract class ScreenTab<T extends ContainerBase> extends GuiElement implements IScreenTab {
    private final String name;
    private final T menu;
    private final int index;
    private final Font font;

    public ScreenTab(String name, Font font, T menu, int index) {
        this.name = name;
        this.menu = menu;
        this.index = index;
        this.font = font;
    }

    @Override
    public abstract void display(GuiGraphics graphics);

    @Override
    public abstract ResourceLocation getActiveTexture();

    @Override
    public abstract ResourceLocation getInactiveTexture();

    public String getName() {
        return this.name;
    }

    protected T getMenu() {
        return this.menu;
    }

    public int getIndex() {
        return this.index;
    }

    public Font getFont() {
        return font;
    }
}
