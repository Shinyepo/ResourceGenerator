package dev.shinyepo.resourcegenerator.menus.types;

import dev.shinyepo.resourcegenerator.util.GuiMouseUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TabManager {
    private final List<IScreenTab> tabs = new ArrayList<>();
    private int activeTab = 0;

    public TabManager() {
    }

    public void createTabs(IScreenTab @NotNull ... tabs) {
        for (IScreenTab tab : tabs) {
            this.tabs.add(tab.getIndex(), tab);
        }
    }

    protected void switchTab(int index) {
        activeTab = index;
    }

    public void displayTab(GuiGraphics graphics) {
        tabs.get(activeTab).display(graphics);
    }

    public void renderTabs(GuiGraphics graphics, int leftPos, int topPos) {
        for (IScreenTab tab : tabs) {
            boolean isActiveTab = activeTab == tab.getIndex();
            ResourceLocation tabTexture = isActiveTab ? tab.getActiveTexture() : tab.getInactiveTexture();
            int tabWidth = isActiveTab ? 21 : 23;

            int tabX = isActiveTab ? 173 : 175;
            int tabY = tab.getIndex() * 24 + 4;

            graphics.blit(RenderPipelines.GUI_TEXTURED, tabTexture, leftPos + tabX, topPos + tabY, 0, 0, tabWidth, 24, tabWidth, 24);

        }
    }

    public void renderTooltips(GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY) {
        int tabX = leftPos + 174;
        for (IScreenTab tab : tabs) {
            int tabY = topPos + (tab.getIndex() * 24 + 4);
            if (mouseOver(mouseX, mouseY, tabX, tabY)) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.literal(tab.getName()));
                graphics.setTooltipForNextFrame(tab.getFont(), tooltip, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    private boolean mouseOver(int pMouseX, int pMouseY, int relX, int relY) {
        return GuiMouseUtil.isMouseOver(pMouseX, pMouseY, relX, relY, 21, 24);
    }

    public boolean handleClick(int leftPos, int topPos, double mouseX, double mouseY) {
        int tabX = leftPos + 174;
        for (IScreenTab tab : tabs) {
            int tabY = topPos + (tab.getIndex() * 24 + 4);
            if (mouseOver((int) mouseX, (int) mouseY, tabX, tabY)) {
                if (activeTab != tab.getIndex()) {
                    switchTab(tab.getIndex());
                    return true;
                }
            }
        }
        return false;
    }
}
