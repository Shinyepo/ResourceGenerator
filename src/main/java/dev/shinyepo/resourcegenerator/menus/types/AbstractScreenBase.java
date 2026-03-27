package dev.shinyepo.resourcegenerator.menus.types;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.menus.widgets.BasicWidget;
import dev.shinyepo.resourcegenerator.menus.widgets.CardSlotWidget;
import dev.shinyepo.resourcegenerator.menus.widgets.PlayerInventoryWidget;
import dev.shinyepo.resourcegenerator.menus.widgets.SingleSlotWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class AbstractScreenBase<T extends AbstractContainerBase> extends AbstractContainerScreen<T> {
    private final Identifier GUI = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/shared/blank.png");
    private BasicWidget inventoryWidget;
    private BasicWidget cardSlotWidget;
    private final List<BasicWidget> slotWidgets = new ArrayList<>();

    public AbstractScreenBase(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 196, 166);
    }

    @Override
    protected void init() {
        super.init();
    }

    protected void createInventoryWidget() {
        inventoryWidget = PlayerInventoryWidget.create();
    }

    protected void createCardSlotWidget() {
        cardSlotWidget = CardSlotWidget.create();
    }

    protected void createSlotWidget(int x, int y) {
        slotWidgets.add(SingleSlotWidget.create(x, y));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        this.extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);
        if (inventoryWidget != null)
            inventoryWidget.render(graphics);

        if (cardSlotWidget != null)
            cardSlotWidget.render(graphics);

        if (!slotWidgets.isEmpty())
            slotWidgets.forEach(slotWidget -> slotWidget.render(graphics));
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY) {
        if (inventoryWidget != null)
            super.extractSlot(graphics, slot, mouseX, mouseY);
    }

    @Override
    protected void extractSlots(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (inventoryWidget != null)
            super.extractSlots(graphics, mouseX, mouseY);
    }

    @Override
    public @Nullable Slot getSlotUnderMouse() {
        if (inventoryWidget != null)
            return super.getSlotUnderMouse();
        return null;
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int buttonNum, ContainerInput containerInput) {
        if (inventoryWidget != null)
            super.slotClicked(slot, slotId, buttonNum, containerInput);
    }

    @Override
    protected void renderSlotContents(GuiGraphicsExtractor graphics, ItemStack itemStack, Slot slot, @Nullable String itemCount) {
        if (inventoryWidget != null)
            super.renderSlotContents(graphics, itemStack, slot, itemCount);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
