package dev.shinyepo.resourcegenerator.menus.types;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public interface IScreenTab {
    void display(GuiGraphics graphics, int mouseX, int mouseY);

    void renderTabTooltips(GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY);

    void cleanup();

    String getName();

    int getIndex();

    Font getFont();

    boolean isInventoryTab();

    ResourceLocation getActiveTexture();

    ResourceLocation getInactiveTexture();

    boolean handleScroll(double mouseX, double mouseY, double scrollX, double scrollY);

    void onTabSwitch();

}
