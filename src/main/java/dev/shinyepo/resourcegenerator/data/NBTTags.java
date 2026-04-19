package dev.shinyepo.resourcegenerator.data;

public enum NBTTags {
    DEFAULT(""),
    OUTPUT_HANDLER("outputHandler"),
    CARD_HANDLER("cardHandler");

    private final String tag;

    NBTTags(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
