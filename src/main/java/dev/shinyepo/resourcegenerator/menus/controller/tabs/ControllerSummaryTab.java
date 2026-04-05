package dev.shinyepo.resourcegenerator.menus.controller.tabs;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerScreen;
import dev.shinyepo.resourcegenerator.menus.types.GuiElement;
import dev.shinyepo.resourcegenerator.menus.types.ScreenTab;
import dev.shinyepo.resourcegenerator.util.GuiMouseUtil;
import dev.shinyepo.resourcegenerator.util.GuiNumericUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

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

        formatAndDisplayBalance(graphics, 10, 34, mouseX, mouseY);
        formatAndDisplayChange(graphics, 10, 44, mouseX, mouseY);
    }

    private void formatAndDisplayBalance(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY) {
        long balance = getMenu().getValue();
        Component formatedBalance = Component.literal("Balance: " + GuiNumericUtil.abbreviate(balance));
        Component tooltipFullBalance = Component.literal(GuiNumericUtil.format(balance));

        displayFormattedWithTooltip(graphics, formatedBalance, tooltipFullBalance, GuiElement.BASIC.getColor(), x, y, mouseX, mouseY);

    }

    private void formatAndDisplayChange(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY) {
        long balanceChange = getMenu().getValueChange();
        int color = balanceChange > 0 ? GuiElement.GREEN.getColor() : GuiElement.RED.getColor();
        String changeSign = balanceChange < 0 ? "-" : "";
        long absBalanceChange = Math.abs(balanceChange);
        Component formatedChange = Component.literal("Change: " + changeSign + GuiNumericUtil.abbreviate(absBalanceChange));

        Component tooltipFullChange = Component.literal(GuiNumericUtil.format(balanceChange));
        displayFormattedWithTooltip(graphics, formatedChange, tooltipFullChange, color, x, y, mouseX, mouseY);
    }

    private void displayFormattedWithTooltip(GuiGraphicsExtractor graphics, Component text, Component tooltipText, int color, int x, int y, int mouseX, int mouseY) {
        int topPos = getParent().getGuiTop();
        int leftPos = getParent().getGuiLeft();
        int balanceWidth = getFont().width(text);

        graphics.text(getFont(), text, x, y, color, false);
        if (GuiMouseUtil.isMouseOver(mouseX, mouseY, leftPos + x, topPos + y, balanceWidth, getFont().lineHeight)) {
            graphics.setTooltipForNextFrame(getFont(), List.of(tooltipText), Optional.empty(), mouseX, mouseY);
        }
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
