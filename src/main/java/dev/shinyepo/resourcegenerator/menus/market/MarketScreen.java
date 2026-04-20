package dev.shinyepo.resourcegenerator.menus.market;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.pricing.ResourcePriceDefinition;
import dev.shinyepo.resourcegenerator.menus.types.AbstractScreenBase;
import dev.shinyepo.resourcegenerator.registries.PriceDefinitionRegistry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class MarketScreen extends AbstractScreenBase<MarketContainer> {
    private static final Identifier OUT_OF_STOCK_SPRITE = Identifier.withDefaultNamespace("container/villager/out_of_stock");
    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/villager/scroller");
    private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/villager/scroller_disabled");
    private static final Identifier TRADE_ARROW_OUT_OF_STOCK_SPRITE = Identifier.withDefaultNamespace("container/villager/trade_arrow_out_of_stock");
    private static final Identifier TRADE_ARROW_SPRITE = Identifier.withDefaultNamespace("container/villager/trade_arrow");
    private static final Identifier DISCOUNT_STRIKETHRUOGH_SPRITE = Identifier.withDefaultNamespace("container/villager/discount_strikethrough");
    private static final Identifier BACKGROUND = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/market/background.png");

    private final MarketOfferButton[] buttons = new MarketOfferButton[7];
    private int selectedIndex;
    private int scrollOff;
    private boolean isDragging;


    private final List<ResourcePriceDefinition> entries;

    public MarketScreen(MarketContainer menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 276, 166);
        this.inventoryLabelX = 107;
        this.entries = PriceDefinitionRegistry.getMarketEntries();
    }

    public @NonNull Font getFont() {
        return this.font;
    }

    @Override
    protected void init() {
        super.init();
        this.createInventoryWidget(107, 83);
        this.createCardSlotWidget(251, 7);


        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        int buttonY = yo + 16 + 2;

        for (int i = 0; i < 7; i++) {
            this.buttons[i] = (MarketOfferButton) this.addRenderableWidget(
                    new MarketOfferButton(xo + 5, buttonY, i, button -> {
                        this.selectedIndex = ((MarketOfferButton) button).getIndex() + this.scrollOff;
                    }));
            buttonY += 20;
        }

//        createInventoryWidget();
//        createCardSlotWidget();
//        createSlotWidget(85, 35);

//        this.listWidget = new MarketListWidget(this, 64, 16, getTopPos() + 70);
//        this.listWidget.setX(6);
//
//        this.addRenderableWidget(this.listWidget);
//        listWidget.refreshList();
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);
//        graphics.text(this.font, this.menu.getOwnerName(), 16, 30, GuiElement.BASIC.getColor(), false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);

    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);

        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        int offerY = yo + 16 + 1;
        int sellItem1X = xo + 5 + 5;
        this.extractScroller(graphics, xo, yo, mouseX, mouseY, entries);
        int currentOfferIndex = 0;

        for (ResourcePriceDefinition entry : entries) {
            if (!this.canScroll(entries.size()) || currentOfferIndex >= this.scrollOff && currentOfferIndex < 7 + this.scrollOff) {
                ItemStack baseCost = getItemStackFromEntry(entry);
                int decorH = offerY + 2;
                this.extractAndDecorateCost(graphics, baseCost, sellItem1X, decorH);

                this.extractButtonArrows(graphics, entry, xo, decorH);
                graphics.fakeItem(baseCost, xo + 5 + 68, decorH);
                graphics.itemDecorations(this.font, baseCost, xo + 5 + 68, decorH);
                offerY += 20;
                ++currentOfferIndex;
            } else {
                ++currentOfferIndex;
            }
        }

        for (MarketOfferButton button : buttons) {
            if (button.isHoveredOrFocused()) {
                button.extractToolTip(graphics, mouseX, mouseY);
            }

            button.visible = button.index < entries.size();
        }
    }

    private void extractButtonArrows(GuiGraphicsExtractor graphics, ResourcePriceDefinition offer, int xo, int decorHeight) {
//        if (offer.isOutOfStock()) {
//            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TRADE_ARROW_OUT_OF_STOCK_SPRITE, xo + 5 + 35 + 20, decorHeight + 3, 10, 9);
//        } else {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TRADE_ARROW_SPRITE, xo + 5 + 35 + 20, decorHeight + 3, 10, 9);
//        }

    }

    private void extractAndDecorateCost(GuiGraphicsExtractor graphics, ItemStack costA, int sellItem1X, int decorHeight) {
        graphics.fakeItem(costA, sellItem1X, decorHeight);
        graphics.itemDecorations(this.font, costA, sellItem1X, decorHeight);
    }

    private ItemStack getItemStackFromEntry(ResourcePriceDefinition entry) {
        var isItem = BuiltInRegistries.ITEM.get(entry.resource());
        if (isItem.isPresent()) {
            return new ItemStack(isItem.get());
        } else {
            var isBlock = BuiltInRegistries.BLOCK.get(entry.resource());
            if (isBlock.isPresent()) {
                return new ItemStack(isBlock.get().value());
            }
        }
        return ItemStack.EMPTY;
    }

    private void extractScroller(GuiGraphicsExtractor graphics, int xo, int yo, int mouseX, int mouseY, List<ResourcePriceDefinition> offers) {
        int steps = offers.size() + 1 - 7;
        if (steps > 1) {
            int leftOver = 139 - (27 + (steps - 1) * 139 / steps);
            int stepHeight = 1 + leftOver / steps + 139 / steps;
            int maxScrollerOff = 113;
            int scrollerYOff = Math.min(113, this.scrollOff * stepHeight);
            if (this.scrollOff == steps - 1) {
                scrollerYOff = 113;
            }

            int scrollerX = xo + 94;
            int scrollerY = yo + 18 + scrollerYOff;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_SPRITE, scrollerX, scrollerY, 6, 27);
            if (mouseX >= scrollerX && mouseX < xo + 94 + 6 && mouseY >= scrollerY && mouseY <= scrollerY + 27) {
                graphics.requestCursor(this.isDragging ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
            }
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_DISABLED_SPRITE, xo + 94, yo + 18, 6, 27);
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (super.mouseScrolled(x, y, scrollX, scrollY)) {
            return true;
        } else {
            int numberOfOffers = entries.size();
            if (this.canScroll(numberOfOffers)) {
                int maxScrollOff = numberOfOffers - 7;
                this.scrollOff = Mth.clamp((int) ((double) this.scrollOff - scrollY), 0, maxScrollOff);
            }

            return true;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        if (this.canScroll(entries.size()) && event.x() > (double) (xo + 94) && event.x() < (double) (xo + 94 + 6) && event.y() > (double) (yo + 18) && event.y() <= (double) (yo + 18 + 139 + 1)) {
            this.isDragging = true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        int numberOfOffers = entries.size();
        if (this.isDragging) {
            int fullScrollTopPos = this.topPos + 18;
            int fullScrollBottomPos = fullScrollTopPos + 139;
            int maxScrollOff = numberOfOffers - 7;
            float scrolling = ((float) event.y() - (float) fullScrollTopPos - 13.5F) / ((float) (fullScrollBottomPos - fullScrollTopPos) - 27.0F);
            scrolling = scrolling * (float) maxScrollOff + 0.5F;
            this.scrollOff = Mth.clamp((int) scrolling, 0, maxScrollOff);
            return true;
        } else {
            return super.mouseDragged(event, dx, dy);
        }
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.isDragging = false;
        return super.mouseReleased(event);
    }

    private boolean canScroll(int numberOfOffers) {
        return numberOfOffers > 7;
    }

    public class MarketOfferButton extends Button.Plain {
        final int index;

        protected MarketOfferButton(int x, int y, int index, OnPress onPress) {
            super(x, y, 88, 20, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.index = index;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        public void extractToolTip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
            if (this.isHovered && entries.size() > this.index + MarketScreen.this.scrollOff) {
                ItemStack item = getItemStackFromEntry(entries.get(this.index + MarketScreen.this.scrollOff));
                if (mouseX < this.getX() + 20) {
                    graphics.setTooltipForNextFrame(MarketScreen.this.font, item, mouseX, mouseY);
                } else if (mouseX > this.getX() + 65) {
                    graphics.setTooltipForNextFrame(MarketScreen.this.font, item, mouseX, mouseY);
                }
            }
        }
    }
}
