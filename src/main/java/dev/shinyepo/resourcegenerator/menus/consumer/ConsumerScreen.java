package dev.shinyepo.resourcegenerator.menus.consumer;

import dev.shinyepo.resourcegenerator.menus.types.AbstractScreenBase;
import dev.shinyepo.resourcegenerator.menus.types.GuiElement;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ConsumerScreen extends AbstractScreenBase<ConsumerContainer> {
    private static final WidgetSprites PAGE_FORWARD_SPRITES = new WidgetSprites(
            Identifier.withDefaultNamespace("recipe_book/page_forward"), Identifier.withDefaultNamespace("recipe_book/page_forward_highlighted")
    );

    public ConsumerScreen(ConsumerContainer menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        createInventoryWidget();
        createSlotWidget(80, 35);

        addRenderableWidget(new ImageButton(leftPos + 8, topPos + 20, 8, 8, PAGE_FORWARD_SPRITES, btn ->
                this.menu.syncPatternTier()
        ));
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);
        graphics.text(this.font, "Tier: " + menu.getPatternTier(), 16, 20, GuiElement.BASIC.getColor(), false);
        boolean validState = this.menu.getValidState();
        String validStateString = validState ? "Valid" : "Invalid";
        int color = validState ? GuiElement.GREEN.getColor() : GuiElement.RED.getColor();
        graphics.text(this.font, validStateString, 16, 30, color, false);

        graphics.item(this.menu.getProduct(), 16, 40);
    }


}
