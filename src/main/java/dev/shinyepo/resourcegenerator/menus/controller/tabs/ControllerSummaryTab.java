package dev.shinyepo.resourcegenerator.menus.controller.tabs;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerScreen;
import dev.shinyepo.resourcegenerator.menus.types.ScreenTab;
import dev.shinyepo.resourcegenerator.menus.widgets.*;
import dev.shinyepo.resourcegenerator.util.GuiNumericUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class ControllerSummaryTab extends ScreenTab<ControllerContainer, ControllerScreen> {
    private static final Identifier ACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/summary_on.png");
    private static final Identifier INACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/summary_off.png");

    private AbstractMiscWidget ownerWidget;
    private AbstractMiscWidget balanceWidget;
    private AbstractMiscWidget changeWidget;
    private AbstractMiscWidget inventoryWidget;
    private AbstractMiscWidget cardSlotWidget;

    public ControllerSummaryTab(ControllerScreen parent, ControllerContainer menu, int index, boolean isInventoryTab) {
        super("Summary", parent, menu, index, isInventoryTab);
        initWidgets();
    }

    private void initWidgets() {
        int leftPos = getParent().getLeftPos();
        int topPos = getParent().getTopPos();
        int y = 20;
        int dy = 20;

        inventoryWidget = new InventoryWidget(getFont(), leftPos + 7, topPos + 83);

        cardSlotWidget = new CardWidget(getFont(), leftPos + 151, topPos + 7);

        ownerWidget = new OwnerWidget(getFont(),
                leftPos + 8, topPos + y,
                Component.literal(getMenu().getOwnerName()));

        Long balance = getMenu().getValue();
        String abbreviatedValue = GuiNumericUtil.abbreviate(balance);
        balanceWidget = new BalanceWidget(getFont(),
                leftPos + 8, topPos + y + dy,
                Component.literal(abbreviatedValue));

        Long change = getMenu().getValue();
        String abbreviatedChange = GuiNumericUtil.abbreviate(change);
        changeWidget = new ChangeWidget(getFont(),
                leftPos + 8, topPos + y + (dy * 2),
                Component.literal(abbreviatedChange));
    }

    @Override
    public void init() {
        registerWidgets();
    }

    protected void registerWidgets() {
        getParent().registerWidget(ownerWidget);
        getParent().registerWidget(balanceWidget);
        getParent().registerWidget(changeWidget);
        getParent().registerWidget(inventoryWidget);
        getParent().registerWidget(cardSlotWidget);
    }


    @Override
    public void display(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        String owner = getMenu().getOwnerName();
        if (owner.isEmpty()) {
            ownerWidget.setMessage("Owner not assigned!");
            balanceWidget.setVisible(false);
            changeWidget.setVisible(false);
            return;
        }
        balanceWidget.setVisible(true);
        changeWidget.setVisible(true);

        ownerWidget.setMessage(getMenu().getOwnerName());
        balanceWidget.setMessage(getMenu().getValue());
        changeWidget.setMessage(getMenu().getValueChange());
    }

    @Override
    public boolean handleScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
        return false;
    }

    @Override
    public void onTabSwitch() {
        registerWidgets();
    }

    @Override
    public void cleanup() {
        getParent().clearWidgets();
    }

    @Override
    public void renderTabTooltips(GuiGraphicsExtractor graphics, int leftPos, int topPos, int mouseX, int mouseY) {

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
