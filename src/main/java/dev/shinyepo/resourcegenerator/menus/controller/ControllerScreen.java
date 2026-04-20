package dev.shinyepo.resourcegenerator.menus.controller;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.Upgrade;
import dev.shinyepo.resourcegenerator.menus.controller.tabs.ControllerDetailsTab;
import dev.shinyepo.resourcegenerator.menus.controller.tabs.ControllerSummaryTab;
import dev.shinyepo.resourcegenerator.menus.types.GuiElement;
import dev.shinyepo.resourcegenerator.menus.types.TabContainerScreen;
import dev.shinyepo.resourcegenerator.menus.widgets.ScrollableUpgradeList;
import dev.shinyepo.resourcegenerator.registries.UpgradeRegistry;
import dev.shinyepo.resourcegenerator.util.GuiMouseUtil;
import dev.shinyepo.resourcegenerator.util.GuiNumericUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class ControllerScreen extends TabContainerScreen<ControllerContainer> {
    private final Identifier GUI = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/shared/blank.png");
    private ScrollableUpgradeList.UpgradeEntry selected;

    public ControllerScreen(ControllerContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 196, 166);
    }

    @Override
    protected void init() {
        super.init();
        createTabs(
                new ControllerSummaryTab(this, this.menu, 0, true),
                new ControllerDetailsTab(this, this.menu, 1)
        );
    }

    public void setSelected(ScrollableUpgradeList.UpgradeEntry selected) {
        this.selected = selected;
    }

    public ScrollableUpgradeList.UpgradeEntry getSelected() {
        return selected;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int i, int i1, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        super.extractBackground(graphics, a, i, i1);
    }

    public <T extends ObjectSelectionList.Entry<T>> void buildList(Consumer<T> modListViewConsumer, Function<Upgrade, T> newEntry) {
        UpgradeRegistry.UPGRADES.getEntries().forEach(entry -> modListViewConsumer.accept(newEntry.apply(entry.get())));
    }

    public void formatAndDisplayValue(GuiGraphicsExtractor graphics, String text, long value, int x, int y, int mouseX, int mouseY) {
        Component formatedBalance = Component.literal(text + GuiNumericUtil.abbreviate(value));
        Component tooltipFullBalance = Component.literal(GuiNumericUtil.format(value));

        displayFormattedWithTooltip(graphics, formatedBalance, tooltipFullBalance, GuiElement.BASIC.getColor(), x, y, mouseX, mouseY);
    }

    public void displayFormattedWithTooltip(GuiGraphicsExtractor graphics, Component text, Component tooltipText, int color, int x, int y, int mouseX, int mouseY) {
        int topPos = getTopPos();
        int leftPos = getLeftPos();
        int balanceWidth = getFont().width(text);

        graphics.text(getFont(), text, x, y, color, false);
        if (GuiMouseUtil.isMouseOver(mouseX, mouseY, leftPos + x, topPos + y, balanceWidth, getFont().lineHeight)) {
            graphics.setTooltipForNextFrame(getFont(), List.of(tooltipText), Optional.empty(), mouseX, mouseY);
        }
    }

    public void formatAndDisplayChange(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY) {
        long balanceChange = getMenu().getValueChange();
        int color = balanceChange > 0 ? GuiElement.GREEN.getColor() : GuiElement.RED.getColor();
        String changeSign = balanceChange < 0 ? "-" : "";
        long absBalanceChange = Math.abs(balanceChange);
        Component formatedChange = Component.literal("Change: " + changeSign + GuiNumericUtil.abbreviate(absBalanceChange));

        Component tooltipFullChange = Component.literal(GuiNumericUtil.format(balanceChange));
        displayFormattedWithTooltip(graphics, formatedChange, tooltipFullChange, color, x, y, mouseX, mouseY);
    }
}
