package dev.shinyepo.resourcegenerator.menus.types;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.menus.widgets.AbstractMiscWidget;
import dev.shinyepo.resourcegenerator.menus.widgets.CardWidget;
import dev.shinyepo.resourcegenerator.menus.widgets.InventoryWidget;
import dev.shinyepo.resourcegenerator.menus.widgets.SlotWidget;
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

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class AbstractScreenBase<T extends AbstractContainerBase> extends AbstractContainerScreen<T> {
    private final Identifier GUI = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/shared/blank.png");
    protected AbstractMiscWidget inventoryWidget;
    protected AbstractMiscWidget cardSlotWidget;
    protected AbstractMiscWidget slotWidget;

    public AbstractScreenBase(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 196, 166);
    }

    public AbstractScreenBase(T menu, Inventory inventory, Component title, int width, int height) {
        super(menu, inventory, title, width, height);
    }

    @Override
    protected void init() {
        super.init();
    }

    protected void createInventoryWidget() {
        createInventoryWidget(7, 83);
    }

    protected void createInventoryWidget(int x, int y) {
        inventoryWidget = new InventoryWidget(getFont(), getLeftPos() + x, getTopPos() + y);
        addRenderableWidget(inventoryWidget);
    }

    protected void createCardSlotWidget() {
        createCardSlotWidget(151, 7);
    }

    protected void createCardSlotWidget(int x, int y) {
        cardSlotWidget = new CardWidget(getFont(), getLeftPos() + x, getTopPos() + y);
        addRenderableWidget(cardSlotWidget);
    }

    protected void createSlotWidget(int x, int y) {
        slotWidget = new SlotWidget(getFont(), getLeftPos() + x, getTopPos() + y);
        addRenderableWidget(slotWidget);
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
    public @Nullable Slot getHoveredSlot() {
        if (inventoryWidget != null)
            return super.getHoveredSlot();
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
