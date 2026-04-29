package dev.shinyepo.resourcegenerator.menus.consumer;

import dev.shinyepo.resourcegenerator.menus.types.AbstractScreenBase;
import dev.shinyepo.resourcegenerator.menus.widgets.AbstractMiscWidget;
import dev.shinyepo.resourcegenerator.menus.widgets.ChangeWidget;
import dev.shinyepo.resourcegenerator.menus.widgets.PatternStateWidget;
import dev.shinyepo.resourcegenerator.menus.widgets.PatternTierWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ConsumerScreen extends AbstractScreenBase<ConsumerContainer> {
    private AbstractMiscWidget patternTierWidget;
    private AbstractMiscWidget patternStateWidget;
    private AbstractMiscWidget priceWidget;

    public ConsumerScreen(ConsumerContainer menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        createInventoryWidget();
        createSlotWidget(80, 35);
        int leftPos = this.getLeftPos();
        int topPos = this.getTopPos();

        patternStateWidget = new PatternStateWidget(getFont(), leftPos + 152, topPos + 47);
        addRenderableWidget(patternStateWidget);

        patternTierWidget = new PatternTierWidget(getFont(), leftPos + 152, topPos + 27, this::syncPatternTier);
        patternTierWidget.setMessage((long) getMenu().getPatternTier());
        addRenderableWidget(patternTierWidget);

        priceWidget = new ChangeWidget(getFont(), leftPos + 8, topPos + 36, Component.literal(""), "Price: ", ChangeWidget.ChangeType.LOSS);
        addRenderableWidget(priceWidget);
    }

    private void syncPatternTier() {
        getMenu().syncPatternTier();
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);
        boolean validState = this.menu.getPatternValidState();
        if (validState) {
            boolean productValidState = this.menu.getProductValidState();
            if (productValidState) {
                patternStateWidget.setMessage("VALID");
            } else {
                patternStateWidget.setMessage("INVALID_ITEM");
            }
        } else {
            patternStateWidget.setMessage("INVALID");
        }

        priceWidget.setMessage(-getMenu().getPrice());
        patternTierWidget.setMessage((long) getMenu().getPatternTier());
    }
}
