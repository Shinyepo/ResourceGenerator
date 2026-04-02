package dev.shinyepo.resourcegenerator.data.patterns;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum PatternElementType implements StringRepresentable {
    RESOURCE,
    STRUCTURE,
    EMPTY;

    @Override
    public @NonNull String getSerializedName() {
        return name().toLowerCase();
    }
}
