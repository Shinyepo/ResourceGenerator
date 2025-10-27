package dev.shinyepo.resourcegenerator.menus.controller.tabs;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.Upgrade;
import dev.shinyepo.resourcegenerator.data.client.AccountUpgradeData;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerScreen;
import dev.shinyepo.resourcegenerator.menus.types.ScreenTab;
import dev.shinyepo.resourcegenerator.menus.widgets.ScrollableUpgradeList;
import dev.shinyepo.resourcegenerator.util.GuiMouseUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class ControllerDetailsTab extends ScreenTab<ControllerContainer, ControllerScreen> {
    private static final ResourceLocation ACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/details_on.png");
    private static final ResourceLocation INACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/details_off.png");
    private ScrollableUpgradeList widget;

    public ControllerDetailsTab(ControllerScreen parent, ControllerContainer menu, int index) {
        super("Details", parent, menu, index);
    }

    @Override
    public void display(GuiGraphics graphics, int mouseX, int mouseY) {

        ControllerScreen parent = getParent();
        int topPos = parent.getGuiTop();
        int leftPos = parent.getGuiLeft();
        if (widget == null) {
            this.widget = new ScrollableUpgradeList(getParent(), 160, topPos + 20, topPos + 80);

            widget.setX(leftPos + 5);
            getParent().registerWidget(Button.builder(Component.literal("Buy"), btn -> {
                ScrollableUpgradeList.UpgradeEntry upgradeEntry = getParent().getSelected();
                if (upgradeEntry != null) {
                    Upgrade upgrade = upgradeEntry.getUpgrade();
                    Map<ResourceLocation, Integer> playerUpgrades = AccountUpgradeData.get();
                    int playerTier = playerUpgrades.getOrDefault(upgradeEntry.getUpgrade().id(), 0);
                    boolean maxTierFlag = upgrade.maxTier() >= playerTier + 1;
                    if (upgrade.upgradeCost(playerUpgrades.getOrDefault(upgrade.id(), 0)) <= getMenu().getValue() && maxTierFlag)
                        getMenu().buyUpgrade(upgradeEntry.getUpgrade().id(), playerTier + 1);
                }
            }).pos(leftPos + 120, topPos + 142).size(48, 16).build());
            getParent().registerWidget(widget);
        }


        ScrollableUpgradeList.UpgradeEntry upgradeEntry = getParent().getSelected();
        if (upgradeEntry != null) {
            Upgrade upgrade = upgradeEntry.getUpgrade();
            Map<ResourceLocation, Integer> playerUpgrades = AccountUpgradeData.get();
            int currentTier = playerUpgrades.getOrDefault(upgrade.id(), 0);
            boolean maxTierFlag = currentTier == upgrade.maxTier();
            String upgradeCost = maxTierFlag ? "MAX TIER" : String.valueOf(upgrade.upgradeCost(currentTier + 1));
            String nextBonus = maxTierFlag ? "MAX TIER" : String.valueOf(upgrade.totalBonus(currentTier + 1));
            graphics.drawString(getFont(), Component.literal("Balance: " + this.getMenu().getValue()), 6, 92, BASIC, false);
            graphics.drawString(getFont(), Component.literal("Tier: " + currentTier), 6, 102, BASIC, false);
            graphics.drawString(getFont(), Component.literal("Current bonus: " + upgrade.totalBonus(currentTier)), 6, 112, BASIC, false);
            graphics.drawString(getFont(), Component.literal("Upgrade Cost: " + upgradeCost), 6, 122, BASIC, false);
            graphics.drawString(getFont(), Component.literal("Next bonus: " + nextBonus), 6, 132, BASIC, false);

            graphics.drawString(getFont(), Component.literal("?"), 6, 152, BASIC, false);
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
    public void renderTabTooltips(GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY) {
    }

    @Override
    public boolean handleScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
        return widget != null && widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public ResourceLocation getInactiveTexture() {
        return INACTIVE_TAB;
    }

    @Override
    public ResourceLocation getActiveTexture() {
        return ACTIVE_TAB;
    }
}
