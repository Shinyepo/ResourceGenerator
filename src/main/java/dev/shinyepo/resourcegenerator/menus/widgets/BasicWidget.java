package dev.shinyepo.resourcegenerator.menus.widgets;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class BasicWidget {
    private final Identifier atlas = Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/shared/misc_atlas.png");
    private int width;
    private int height;
    private int x;
    private int y;
    private int u;
    private int v;

    private BasicWidget(int width, int height, int x, int y, int u, int v) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
    }

    public BasicWidget() {
    }


    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(GuiGraphicsExtractor graphics) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                atlas,
                x,
                y,
                u, v,
                width, height,
                256, 256
        );
    }

    public static class BasicWidgetBuilder {
        private int width;
        private int height;
        private int x;
        private int y;
        private int u;
        private int v;

        public BasicWidgetBuilder setSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public BasicWidgetBuilder setPosition(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public BasicWidgetBuilder setUV(int u, int v) {
            this.u = u;
            this.v = v;
            return this;
        }

        public BasicWidget build() {
            return new BasicWidget(width, height, x, y, u, v);
        }
    }

}