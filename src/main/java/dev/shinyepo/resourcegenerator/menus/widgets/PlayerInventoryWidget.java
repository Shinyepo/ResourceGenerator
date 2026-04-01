package dev.shinyepo.resourcegenerator.menus.widgets;

public class PlayerInventoryWidget {
    private static final int width = 162;
    private static final int height = 76;
    private static final int x = 7;
    private static final int y = 83;
    private static final int u = 0;
    private static final int v = 0;

    public static BasicWidget create() {
        BasicWidget.BasicWidgetBuilder builder = new BasicWidget.BasicWidgetBuilder();
        builder.setSize(width, height)
                .setUV(u, v)
                .setPosition(x, y);

        return builder.build();
    }
}
