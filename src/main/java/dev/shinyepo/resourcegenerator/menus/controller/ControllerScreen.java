package dev.shinyepo.resourcegenerator.menus.controller;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class ControllerScreen extends AbstractContainerScreen<ControllerContainer> {
    private final ResourceLocation GUI = fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/controller.png");

    public ControllerScreen(ControllerContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.inventoryLabelY = this.imageHeight - 94;
//        this.titleLabelX = 32;
        this.imageWidth = 196;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float v, int i, int i1) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
        String owner = this.menu.getOwnerName();
//        if (!"".equals(owner)) {
        graphics.drawString(this.font, Component.literal("Owner: " + owner), 10, 24, 0xFF404040, false);
        graphics.drawString(this.font, Component.literal("Value: " + this.menu.getValue()), 10, 34, 0xFF404040, false);

//        }
    }
}
