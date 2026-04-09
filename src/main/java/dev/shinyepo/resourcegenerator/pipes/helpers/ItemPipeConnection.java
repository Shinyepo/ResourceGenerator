package dev.shinyepo.resourcegenerator.pipes.helpers;

import net.minecraft.util.StringRepresentable;

public enum ItemPipeConnection implements StringRepresentable {
    NONE,
    CABLE,
    BLOCK;
    public static final ItemPipeConnection[] VALUES = values();

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
