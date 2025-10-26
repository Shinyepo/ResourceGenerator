package dev.shinyepo.resourcegenerator.menus.controller;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.Upgrade;
import dev.shinyepo.resourcegenerator.menus.controller.tabs.ControllerDetailsTab;
import dev.shinyepo.resourcegenerator.menus.controller.tabs.ControllerSummaryTab;
import dev.shinyepo.resourcegenerator.menus.types.TabContainerScreen;
import dev.shinyepo.resourcegenerator.menus.widgets.ScrollableUpgradeList;
import dev.shinyepo.resourcegenerator.registries.UpgradeRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class ControllerScreen extends TabContainerScreen<ControllerContainer> {
    private final ResourceLocation GUI = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/controller.png");
    private ScrollableUpgradeList.UpgradeEntry selected;

    public ControllerScreen(ControllerContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 196;
    }

    @Override
    protected void init() {
        super.init();
        createTabs(
                new ControllerSummaryTab(this, this.menu, 0, true),
                new ControllerDetailsTab(this, this.menu, 1)
        );
    }

    public void setSelected(ScrollableUpgradeList.UpgradeEntry selected) {
        this.selected = selected;
    }

    public ScrollableUpgradeList.UpgradeEntry getSelected() {
        return selected;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float v, int i, int i1) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        super.renderBg(graphics, v, i, i1);
    }

    public <T extends ObjectSelectionList.Entry<T>> void buildList(Consumer<T> modListViewConsumer, Function<Upgrade, T> newEntry) {
        UpgradeRegistry.UPGRADES.getEntries().forEach(entry -> modListViewConsumer.accept(newEntry.apply(entry.get())));
    }
}
