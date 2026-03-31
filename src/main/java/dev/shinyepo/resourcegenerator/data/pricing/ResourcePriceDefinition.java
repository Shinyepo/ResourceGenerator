package dev.shinyepo.resourcegenerator.data.pricing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shinyepo.resourcegenerator.blocks.entities.types.IPriceUpgrade;
import dev.shinyepo.resourcegenerator.blocks.entities.types.UpgradeEntity;
import dev.shinyepo.resourcegenerator.registries.PriceDefinitionRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;

import java.util.HashMap;
import java.util.Optional;

public record ResourcePriceDefinition(Identifier resource, long price, PriceRuleType type,
                                      Identifier reference,
                                      float multiplier) {
    public static final Codec<ResourcePriceDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("resource").forGetter(ResourcePriceDefinition::resource),
                    Codec.LONG.optionalFieldOf("price", 0L).forGetter(ResourcePriceDefinition::price),
                    StringRepresentable.fromEnum(PriceRuleType::values).fieldOf("type").forGetter(ResourcePriceDefinition::type),
                    Identifier.CODEC.optionalFieldOf("reference").forGetter(def -> java.util.Optional.ofNullable(def.reference())),
                    Codec.FLOAT.optionalFieldOf("multiplier", 1F).forGetter(ResourcePriceDefinition::multiplier)
            ).apply(instance, ResourcePriceDefinition::new)
    );

    public ResourcePriceDefinition(Identifier resource, Long price, PriceRuleType type, Optional<Identifier> reference, Float multiplier) {
        this(resource, price, type, reference.orElse(null), multiplier);
    }

    public static ResourcePriceDefinition fixed(Identifier resource, long price) {
        return new ResourcePriceDefinition(resource, price, PriceRuleType.FIXED, null, 1F);
    }

    public static ResourcePriceDefinition reference(Identifier resource, Identifier reference, float multiplier) {
        return new ResourcePriceDefinition(resource, 0, PriceRuleType.REFERENCE_MULTIPLIER, reference, multiplier);
    }

    public long getPrice(HashMap<BlockPos, UpgradeEntity> upgrades) {
        long priceValue = switch (type) {
            case FIXED -> price;
            case REFERENCE_MULTIPLIER -> {
                if (reference == null) yield 0;
                ResourcePriceDefinition referencePrice = PriceDefinitionRegistry.getPriceData(reference);
                if (referencePrice == null) yield 0;
                yield (long) (referencePrice.getPrice(new HashMap<>()) * multiplier);
            }
        };

        //TODO: Better way of checking upgrades and applying their effect
        for (UpgradeEntity upgrade : upgrades.values()) {
            if (upgrade instanceof IPriceUpgrade priceUpgrade) {
                priceValue = priceUpgrade.apply(priceValue);
            }
        }

        return priceValue;
    }
}
