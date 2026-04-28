package dev.shinyepo.resourcegenerator.menus.controller.tabs;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.Upgrade;
import dev.shinyepo.resourcegenerator.data.client.AccountUpgradeData;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerScreen;
import dev.shinyepo.resourcegenerator.menus.types.GuiElement;
import dev.shinyepo.resourcegenerator.menus.types.ScreenTab;
import dev.shinyepo.resourcegenerator.menus.widgets.*;
import dev.shinyepo.resourcegenerator.util.GuiMouseUtil;
import dev.shinyepo.resourcegenerator.util.GuiNumericUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class ControllerDetailsTab extends ScreenTab<ControllerContainer, ControllerScreen> {
    private static final Identifier ACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/details_on.png");
    private static final Identifier INACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/details_off.png");
    private ScrollableUpgradeList widget;
    private Button buyButton;
    private AbstractMiscWidget balanceWidget;
    private AbstractMiscWidget changeWidget;
    private AbstractMiscWidget tierWidget;

    public ControllerDetailsTab(ControllerScreen parent, ControllerContainer menu, int index) {
        super("Details", parent, menu, index);

        initWidgets();
    }

    private void initWidgets() {
        ControllerScreen parent = getParent();
        int topPos = parent.getTopPos();
        int leftPos = parent.getLeftPos();

        this.widget = new ScrollableUpgradeList(getParent(), 160, topPos + 20, topPos + 86);
        widget.setX(leftPos + 5);
        widget.refreshList();

        Long balance = getMenu().getValue();
        String abbreviatedValue = GuiNumericUtil.abbreviate(balance);
        this.balanceWidget = new BalanceWidget(getFont(), leftPos + 6, topPos + 90, Component.literal(abbreviatedValue));

        this.changeWidget = new ChangeWidget(getFont(), leftPos + 6, topPos + 110, Component.literal("0"), "Cost: ", ChangeWidget.ChangeType.NO_CHANGE);
        changeWidget.setVisible(false);

        this.tierWidget = new TierWidget(getFont(), leftPos + 6, topPos + 130, Component.literal(""));
        tierWidget.setVisible(false);

        this.buyButton = Button.builder(Component.literal("Buy"), btn -> {
            ScrollableUpgradeList.UpgradeEntry upgradeEntry = getParent().getSelected();
            if (upgradeEntry != null) {
                Upgrade upgrade = upgradeEntry.getUpgrade();
                Map<Identifier, Integer> playerUpgrades = AccountUpgradeData.get();
                int playerTier = playerUpgrades.getOrDefault(upgradeEntry.getUpgrade().id(), 0);
                long upgradeCost = upgrade.upgradeCost(playerUpgrades.getOrDefault(upgrade.id(), 0) + 1);
                boolean maxTierFlag = upgrade.maxTier() >= playerTier + 1;
                boolean costFlag = upgradeCost <= getMenu().getValue();
                if (costFlag && maxTierFlag) {
                    getMenu().buyUpgrade(upgradeEntry.getUpgrade().id(), playerTier + 1);
                    getMenu().setValue(getMenu().getValue() - upgradeCost);
                }
            }
        }).pos(leftPos + 120, topPos + 142).size(48, 16).build();
    }

    @Override
    public void init() {
        registerWidgets();
    }

    private void registerWidgets() {
        getParent().registerWidget(widget);
        getParent().registerWidget(buyButton);
        getParent().registerWidget(balanceWidget);
        getParent().registerWidget(changeWidget);
        getParent().registerWidget(tierWidget);
    }

    @Override
    public void display(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        balanceWidget.setMessage(getMenu().getValue());


        ControllerScreen parent = getParent();
        int topPos = parent.getTopPos();
        int leftPos = parent.getLeftPos();


        ScrollableUpgradeList.UpgradeEntry upgradeEntry = getParent().getSelected();
        if (upgradeEntry != null) {
            changeWidget.setVisible(true);
            tierWidget.setVisible(true);
            Upgrade upgrade = upgradeEntry.getUpgrade();
            Map<Identifier, Integer> playerUpgrades = AccountUpgradeData.get();
            int currentTier = playerUpgrades.getOrDefault(upgrade.id(), 0);
            boolean maxTierFlag = currentTier == upgrade.maxTier();
            long upgradeCost = upgrade.upgradeCost(currentTier + 1);
            tierWidget.setMessage(currentTier + "/" + upgrade.maxTier());

            if (maxTierFlag) {
                changeWidget.setMessage(0L);
                buyButton.active = false;
            } else {
                changeWidget.setMessage(-upgradeCost);
                buyButton.active = true;
            }

            graphics.text(getFont(), Component.literal("?"), 6, 152, GuiElement.BASIC.getColor(), false);
            if (GuiMouseUtil.isMouseOver(mouseX, mouseY, leftPos + 6, topPos + 152, 7)) {
                graphics.setTooltipForNextFrame(getFont(), List.of(Component.translatable("gui." + upgrade.id().toLanguageKey() + ".desc")), Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Override
    public void cleanup() {
        getParent().clearWidgets();
        getParent().setSelected(null);
    }

    @Override
    public void renderTabTooltips(GuiGraphicsExtractor graphics, int leftPos, int topPos, int mouseX, int mouseY) {
    }

    @Override
    public boolean handleScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
        return widget != null && widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void onTabSwitch() {
        getMenu().syncUpgradeData();
        registerWidgets();
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
