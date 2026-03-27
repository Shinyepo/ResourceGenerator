package dev.shinyepo.resourcegenerator.menus.widgets;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import net.minecraft.resources.Identifier;

public class SingleSlotWidget {
    private final Identifier texture = Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "textures/gui/shared/inventory_slots.png");
    private static final int width = 18;
    private static final int height = 18;
    private static final int u = 0;
    private static final int v = 0;

    public static BasicWidget create(int x, int y) {
        BasicWidget.BasicWidgetBuilder builder = new BasicWidget.BasicWidgetBuilder();
        builder.setSize(width, height)
                .setUV(u, v)
                .setPosition(x, y);

        return builder.build();
    }
}
