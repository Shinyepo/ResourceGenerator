package dev.shinyepo.resourcegenerator.menus.controller.tabs;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
import dev.shinyepo.resourcegenerator.menus.types.ScreenTab;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class ControllerDetailsTab extends ScreenTab<ControllerContainer> {
    private static final ResourceLocation ACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/details_on.png");
    private static final ResourceLocation INACTIVE_TAB = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/tabs/details_off.png");

    public ControllerDetailsTab(Font font, ControllerContainer menu, int index) {
        super("Details", font, menu, index);
    }

    @Override
    public void display(GuiGraphics graphics) {
        graphics.drawString(getFont(), Component.literal("Details tab is active"), 10, 34, BASIC, false);
        Map<ResourceLocation, Integer> upgrades = getMenu().getUpgrades();
        if (upgrades != null)
            upgrades.forEach((name, tier) -> {
                graphics.drawString(getFont(), Component.literal("Name: " + name + ", Tier: " + tier), 10, 44, BASIC, false);
            });
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
