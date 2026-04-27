package dev.shinyepo.resourcegenerator.menus.controller.tabs;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerScreen;
import dev.shinyepo.resourcegenerator.menus.types.GuiElement;
import dev.shinyepo.resourcegenerator.menus.types.ScreenTab;
import dev.shinyepo.resourcegenerator.menus.widgets.AbstractAccountWidget;
import dev.shinyepo.resourcegenerator.menus.widgets.BalanceWidget;
import dev.shinyepo.resourcegenerator.menus.widgets.ChangeWidget;
import dev.shinyepo.resourcegenerator.menus.widgets.OwnerWidget;
import dev.shinyepo.resourcegenerator.util.GuiNumericUtil;
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

    private AbstractAccountWidget ownerWidget;
    private AbstractAccountWidget balanceWidget;
    private AbstractAccountWidget changeWidget;

    public ControllerSummaryTab(ControllerScreen parent, ControllerContainer menu, int index, boolean isInventoryTab) {
        super("Summary", parent, menu, index, isInventoryTab);
        initWidgets();
    }

    private void initWidgets() {
        int y = 20;
        int dy = 20;

        ownerWidget = new OwnerWidget(getFont(),
                getParent().getLeftPos() + 8, getParent().getTopPos() + y,
                Component.literal(getMenu().getOwnerName()));

        Long balance = getMenu().getValue();
        String abbreviatedValue = GuiNumericUtil.abbreviate(balance);
        balanceWidget = new BalanceWidget(getFont(),
                getParent().getLeftPos() + 8, getParent().getTopPos() + y + dy,
                Component.literal(abbreviatedValue));

        Long change = getMenu().getValue();
        String abbreviatedChange = GuiNumericUtil.abbreviate(change);
        changeWidget = new ChangeWidget(getFont(),
                getParent().getLeftPos() + 8, getParent().getTopPos() + y + (dy * 2),
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

        balanceWidget.setMessage(getMenu().getValue());
        ownerWidget.setMessage(getMenu().getOwnerName());
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
