package dev.shinyepo.resourcegenerator.menus.controller.tabs;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
import dev.shinyepo.resourcegenerator.menus.types.ScreenTab;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class ControllerSummaryTab extends ScreenTab<ControllerContainer> {
    private static final ResourceLocation ACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/summary_on.png");
    private static final ResourceLocation INACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/summary_off.png");

    public ControllerSummaryTab(Font font, ControllerContainer menu, int index) {
        super("Summary", font, menu, index);
    }

    @Override
    public void display(GuiGraphics graphics) {
        String owner = getMenu().getOwnerName();
        if (owner.isEmpty()) {
            displayNotAssigned(graphics);
            return;
        }
        graphics.drawString(getFont(), Component.literal("Owner: " + owner), 10, 24, BASIC, false);
        graphics.drawString(getFont(), Component.literal("Value: " + getMenu().getValue()), 10, 34, BASIC, false);
    }

    private void displayNotAssigned(GuiGraphics graphics) {
        graphics.drawString(getFont(), Component.literal("Owner not assigned!"), 8, 20, RED, false);
    }

    @Override
    public ResourceLocation getInactiveTexture() {
        return INACTIVE_TAB;
    }

    @Override
    public ResourceLocation getActiveTexture() {
        return ACTIVE_TAB;
    }
}
