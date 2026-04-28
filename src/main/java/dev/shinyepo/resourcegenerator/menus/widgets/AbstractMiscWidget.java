package dev.shinyepo.resourcegenerator.menus.widgets;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.menus.types.GuiElement;
import dev.shinyepo.resourcegenerator.util.GuiNumericUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;


public abstract class AbstractMiscWidget extends AbstractWidget {
    protected final Identifier atlas = Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/shared/misc_atlas.png");
    private int u = 0;
    private int v = 0;
    private int spriteHeight = 16;
    private int spriteWidth = 16;
    protected final Font font;
    private final String tooltipPrefix;

    public AbstractMiscWidget(Font font, int x, int y, int width, int height, Component message) {
        this(font, x, y, width, height, message, "");
    }

    public AbstractMiscWidget(Font font, int x, int y, int width, int height, Component message, String tooltipPrefix) {
        super(x, y, width, height, message);
        this.font = font;
        this.tooltipPrefix = tooltipPrefix;
        setTooltip(Tooltip.create(Component.literal(this.tooltipPrefix + message.getString())));
    }

    //Override so we dont play sound when widget is clicked.
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return false;
    }

    public void setMessage(@NonNull Long value) {
        String abbreviatedValue = GuiNumericUtil.abbreviate(value);
        super.setMessage(Component.literal(abbreviatedValue));

        updateWidth();
        updateTooltip(value);
    }

    public void setMessage(String value) {
        if (value.equals(getMessage().getString())) return;

        super.setMessage(Component.literal(value));
        updateWidth();
        updateTooltip(value);
    }

    protected void updateTooltip(Long value) {
        String formatedBalance = GuiNumericUtil.format(value);
        Component tooltip = Component.literal(this.tooltipPrefix + formatedBalance);
        super.setTooltip(Tooltip.create(tooltip));
    }

    protected void updateTooltip(String value) {
        Component tooltip = Component.literal(this.tooltipPrefix + value);
        super.setTooltip(Tooltip.create(tooltip));
    }

    protected void updateWidth() {
        int width = getFont().width(getMessage()) + spriteWidth + 4;
        this.setWidth(width);
    }

    private Font getFont() {
        return font;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                atlas,
                this.getX(),
                this.getY(),
                u, v,
                spriteWidth, spriteHeight,
                256, 256
        );

        graphics.text(getFont(), getMessage(), this.getX() + 20, this.getY() + 4, GuiElement.BASIC.getColor(), false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {

    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected void setUV(int u, int v) {
        this.u = u;
        this.v = v;
    }

    protected void setSpriteDimensions(int spriteWidth, int spriteHeight) {
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
    }
}
