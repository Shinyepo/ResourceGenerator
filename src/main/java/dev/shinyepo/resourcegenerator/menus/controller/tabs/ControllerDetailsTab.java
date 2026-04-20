package dev.shinyepo.resourcegenerator.menus.controller.tabs;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.Upgrade;
import dev.shinyepo.resourcegenerator.data.client.AccountUpgradeData;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerScreen;
import dev.shinyepo.resourcegenerator.menus.types.GuiElement;
import dev.shinyepo.resourcegenerator.menus.types.ScreenTab;
import dev.shinyepo.resourcegenerator.menus.widgets.ScrollableUpgradeList;
import dev.shinyepo.resourcegenerator.util.GuiMouseUtil;
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

    public ControllerDetailsTab(ControllerScreen parent, ControllerContainer menu, int index) {
        super("Details", parent, menu, index);
    }

    @Override
    public void display(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {

        ControllerScreen parent = getParent();
        int topPos = parent.getTopPos();
        int leftPos = parent.getLeftPos();
        if (widget == null) {
            this.widget = new ScrollableUpgradeList(getParent(), 160, topPos + 20, topPos + 86);

            widget.setX(leftPos + 5);
            getParent().registerWidget(Button.builder(Component.literal("Buy"), btn -> {
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
            }).pos(leftPos + 120, topPos + 142).size(48, 16).build());
            getParent().registerWidget(widget);
            widget.refreshList();
        }


        ScrollableUpgradeList.UpgradeEntry upgradeEntry = getParent().getSelected();
        if (upgradeEntry != null) {
            Upgrade upgrade = upgradeEntry.getUpgrade();
            Map<Identifier, Integer> playerUpgrades = AccountUpgradeData.get();
            int currentTier = playerUpgrades.getOrDefault(upgrade.id(), 0);
            boolean maxTierFlag = currentTier == upgrade.maxTier();
            long upgradeCost = upgrade.upgradeCost(currentTier + 1);
            float nextBonus = upgrade.totalBonus(currentTier + 1);
            getParent().formatAndDisplayValue(graphics, "Balance: ", getMenu().getValue(), 6, 92, mouseX, mouseY);
            graphics.text(getFont(), Component.literal("Tier: " + currentTier), 6, 102, GuiElement.BASIC.getColor(), false);
            graphics.text(getFont(), Component.literal("Current bonus: " + upgrade.totalBonus(currentTier)), 6, 112, GuiElement.BASIC.getColor(), false);
            if (maxTierFlag) {
                graphics.text(getFont(), Component.literal("Max Tier"), 6, 122, GuiElement.RED.getColor(), false);
            } else {
                getParent().formatAndDisplayValue(graphics, "Upgrade Cost: ", upgradeCost, 6, 122, mouseX, mouseY);
                graphics.text(getFont(), Component.literal("Next bonus: " + nextBonus), 6, 132, GuiElement.BASIC.getColor(), false);
            }

            graphics.text(getFont(), Component.literal("?"), 6, 152, GuiElement.BASIC.getColor(), false);
            if (GuiMouseUtil.isMouseOver(mouseX, mouseY, leftPos + 6, topPos + 152, 7)) {
                graphics.setTooltipForNextFrame(getFont(), List.of(Component.translatable("gui." + upgrade.id().toLanguageKey() + ".desc")), Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Override
    public void cleanup() {
        widget = null;
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
