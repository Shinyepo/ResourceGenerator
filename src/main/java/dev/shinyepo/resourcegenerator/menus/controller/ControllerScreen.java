package dev.shinyepo.resourcegenerator.menus.controller;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.menus.controller.tabs.ControllerDetailsTab;
import dev.shinyepo.resourcegenerator.menus.controller.tabs.ControllerSummaryTab;
import dev.shinyepo.resourcegenerator.menus.types.TabContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class ControllerScreen extends TabContainerScreen<ControllerContainer> {
    private final ResourceLocation GUI = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller/controller.png");

    public ControllerScreen(ControllerContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.inventoryLabelY = this.imageHeight - 94;
//        this.titleLabelX = 32;
        this.imageWidth = 196;
    }

    @Override
    protected void init() {
        super.init();
        createTabs(
                new ControllerSummaryTab(getFont(), this.menu, 0),
                new ControllerDetailsTab(getFont(), this.menu, 1)
        );
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float v, int i, int i1) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        super.renderBg(graphics, v, i, i1);
    }
}
