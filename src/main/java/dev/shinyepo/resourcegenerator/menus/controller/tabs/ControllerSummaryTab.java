package dev.shinyepo.resourcegenerator.menus.controller.tabs;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerScreen;
import dev.shinyepo.resourcegenerator.menus.types.ScreenTab;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class ControllerSummaryTab extends ScreenTab<ControllerContainer, ControllerScreen> {
    private static final ResourceLocation ACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/summary_on.png");
    private static final ResourceLocation INACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/summary_off.png");
    private static final ResourceLocation CARD_SLOT = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/card_slot.png");
    private static final ResourceLocation INVENTORY_SLOTS = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/inventory_slots.png");

    public ControllerSummaryTab(ControllerScreen parent, ControllerContainer menu, int index, boolean isInventoryTab) {
        super("Summary", parent, menu, index, isInventoryTab);
    }

    @Override
    public void display(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, CARD_SLOT, 151, 7, 0, 0, 18, 18, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_SLOTS, 7, 83, 0, 0, 162, 76, 256, 256);
        String owner = getMenu().getOwnerName();
        if (owner.isEmpty()) {
            displayNotAssigned(graphics);
            return;
        }
        graphics.drawString(getFont(), Component.literal("Owner: " + owner), 10, 24, BASIC, false);
        graphics.drawString(getFont(), Component.literal("Value: " + getMenu().getValue()), 10, 34, BASIC, false);
    }

    @Override
    public boolean handleScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
        return false;
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void renderTabTooltips(GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY) {

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
