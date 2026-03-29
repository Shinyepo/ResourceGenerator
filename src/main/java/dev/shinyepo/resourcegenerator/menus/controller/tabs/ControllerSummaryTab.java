package dev.shinyepo.resourcegenerator.menus.controller.tabs;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerScreen;
import dev.shinyepo.resourcegenerator.menus.types.GuiElement;
import dev.shinyepo.resourcegenerator.menus.types.ScreenTab;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class ControllerSummaryTab extends ScreenTab<ControllerContainer, ControllerScreen> {
    private static final Identifier ACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/summary_on.png");
    private static final Identifier INACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/summary_off.png");
    private static final Identifier CARD_SLOT = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/card_slot.png");
    private static final Identifier INVENTORY_SLOTS = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/inventory_slots.png");

    public ControllerSummaryTab(ControllerScreen parent, ControllerContainer menu, int index, boolean isInventoryTab) {
        super("Summary", parent, menu, index, isInventoryTab);
    }

    @Override
    public void display(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, CARD_SLOT, 151, 7, 0, 0, 18, 18, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_SLOTS, 7, 83, 0, 0, 162, 76, 256, 256);
        String owner = getMenu().getOwnerName();
        if (owner.isEmpty()) {
            displayNotAssigned(graphics);
            return;
        }
        graphics.text(getFont(), Component.literal("Owner: " + owner), 10, 24, GuiElement.BASIC.getColor(), false);
        graphics.text(getFont(), Component.literal("Value: " + getMenu().getValue()), 10, 34, GuiElement.BASIC.getColor(), false);

        long valueChange = getMenu().getValueChange();
        graphics.text(getFont(), Component.literal("Change: " + valueChange), 10, 44, valueChange > 0 ? GuiElement.GREEN.getColor() : GuiElement.RED.getColor(), false);
    }

    @Override
    public boolean handleScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
        return false;
    }

    @Override
    public void onTabSwitch() {
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void renderTabTooltips(GuiGraphicsExtractor graphics, int leftPos, int topPos, int mouseX, int mouseY) {

    }

    private void displayNotAssigned(GuiGraphicsExtractor graphics) {
        graphics.text(getFont(), Component.literal("Owner not assigned!"), 8, 20, GuiElement.RED.getColor(), false);
    }

    @Override
    public Identifier getInactiveTexture() {
        return INACTIVE_TAB;
    }

    @Override
    public Identifier getActiveTexture() {
        return ACTIVE_TAB;
    }
}
