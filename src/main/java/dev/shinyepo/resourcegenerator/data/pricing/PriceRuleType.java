package dev.shinyepo.resourcegenerator.data.pricing;

import net.minecraft.util.StringRepresentable;

public enum PriceRuleType implements StringRepresentable {
    FIXED,
    REFERENCE_MULTIPLIER;

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}