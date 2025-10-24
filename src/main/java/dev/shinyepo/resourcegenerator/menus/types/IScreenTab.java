package dev.shinyepo.resourcegenerator.menus.types;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public interface IScreenTab {
    void display(GuiGraphics graphics);

    String getName();

    int getIndex();

    Font getFont();

    ResourceLocation getActiveTexture();

    ResourceLocation getInactiveTexture();
}
