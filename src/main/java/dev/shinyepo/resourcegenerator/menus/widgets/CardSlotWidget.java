package dev.shinyepo.resourcegenerator.menus.widgets;

public class CardSlotWidget {
    private static final int width = 18;
    private static final int height = 18;
    private static final int x = 151;
    private static final int y = 7;
    private static final int u = 162;
    private static final int v = 0;

    public static BasicWidget create() {
        BasicWidget.BasicWidgetBuilder builder = new BasicWidget.BasicWidgetBuilder();
        builder.setSize(width, height)
                .setUV(u, v)
                .setPosition(x, y);

        return builder.build();
    }
}
