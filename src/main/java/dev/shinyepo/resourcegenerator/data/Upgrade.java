package dev.shinyepo.resourcegenerator.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shinyepo.resourcegenerator.ResourceGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record Upgrade(ResourceLocation id, int tier, int maxTier, int baseCost, float costMultiplier,
                      int baseBonus,
                      int bonusMultiplier) {

    public static final Codec<Upgrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(Upgrade::id),
            Codec.INT.fieldOf("tier").forGetter(Upgrade::tier)
    ).apply(instance, Upgrade::new));

    public Upgrade(ResourceLocation id, int tier) {
        this(id, tier, 0, 0, 0, 0, 0);
    }

    public long upgradeCost(int tier) {
        return Math.round(baseCost * Math.pow(bonusMultiplier, tier - 1));
    }

    public float totalBonus(int tier) {
        return baseBonus + (tier * bonusMultiplier);
    }

    @Override
    public String toString() {
        return "Upgrade[" +
                "id=" + id + ", " +
                "desc=" + Component.translatable(id.toLanguageKey() + ".desc") + ", " +
                "tier=" + tier + ", " +
                "baseCost=" + baseCost + ", " +
                "costMultiplier=" + costMultiplier + ", " +
                "baseBonus=" + baseBonus + ", " +
                "bonusMultiplier=" + bonusMultiplier + ']';
    }


    public static class Builder {
        private ResourceLocation id;
        private int tier = 0;
        private int maxTier;
        private int baseCost = 0;
        private float costMultiplier = 0;
        private int baseBonus = 0;
        private int bonusMultiplier = 0;

        public Builder setId(String id) {
            this.id = ResourceLocation.fromNamespaceAndPath(ResourceGenerator.MODID, id);
            return this;
        }

        public Builder setMaxTier(int maxTier) {
            this.maxTier = maxTier;
            return this;
        }

        public Builder setBaseCost(int baseCost) {
            this.baseCost = baseCost;
            return this;
        }

        public Builder setCostMultiplier(float costMultiplier) {
            this.costMultiplier = costMultiplier;
            return this;
        }

        public Builder setBaseBonus(int baseBonus) {
            this.baseBonus = baseBonus;
            return this;
        }

        public Builder setBonusMultiplier(int bonusMultiplier) {
            this.bonusMultiplier = bonusMultiplier;
            return this;
        }

        public Upgrade build() {
            return new Upgrade(id, tier, maxTier, baseCost, costMultiplier, baseBonus, bonusMultiplier);
        }
    }

}
