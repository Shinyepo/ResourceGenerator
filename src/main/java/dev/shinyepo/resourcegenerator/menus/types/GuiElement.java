package dev.shinyepo.resourcegenerator.menus.types;

public enum GuiElement {
    BACKGROUND(0xFFC6C6C6),
    BASIC(0xFF404040),
    RED(0xFF800000),
    GREEN(0xFF6AA84F);

    private final int color;

    GuiElement(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
