package dev.shinyepo.resourcegenerator.data.market;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class MarketOffer {
    private final Item item;

    private final Type offerType;

    private final Long basePrice;

    private final float minMultiplier;
    private final float maxMultiplier;

    private final int maxQuantity;
    private int currentQuantity;
    private Long finalPrice = 0L;
    private float finalMultiplier;

    private MarketOffer(Item id, Type offerType, Long basePrice, float minMultiplier, float maxMultiplier, int maxQuantity) {
        this.item = id;
        this.offerType = offerType;
        this.basePrice = basePrice;
        this.minMultiplier = minMultiplier;
        this.maxMultiplier = maxMultiplier;
        this.maxQuantity = maxQuantity;
    }

    public Type getOfferType() {
        return offerType;
    }

    public Item getItem() {
        return item;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public Long getPrice() {
        if (finalPrice == 0) calculatePrice();
        return finalPrice;
    }

    public float getPriceMultiplier() {
        if (finalMultiplier == 0) calculatePrice();
        return finalMultiplier;
    }

    private void calculatePrice() {
        float random = (float) Math.random();
        finalMultiplier = minMultiplier + random * (maxMultiplier - minMultiplier);
        finalPrice = (long) Math.round(basePrice * finalMultiplier);
    }

    public enum Type {
        BUY,
        SELL
    }

    public static class Builder {
        private Item item;
        private Type type;
        private Long basePrice;
        private float minMultiplier;
        private float maxMultiplier;
        private int maxQuantity;

        public Builder setItem(Block block) {
            this.item = block.asItem();
            return this;
        }

        public Builder setItem(Item item) {
            this.item = item;
            return this;
        }

        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        public Builder setBasePrice(Long basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        public Builder setMinMultiplier(float minMultiplier) {
            this.minMultiplier = minMultiplier;
            return this;
        }

        public Builder setMaxMultiplier(float maxMultiplier) {
            this.maxMultiplier = maxMultiplier;
            return this;
        }

        public Builder setMaxQuantity(int maxQuantity) {
            this.maxQuantity = maxQuantity;
            return this;
        }

        public MarketOffer build() {
            return new MarketOffer(item, type, basePrice, minMultiplier, maxMultiplier, maxQuantity);
        }
    }
}
