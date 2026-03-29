package dev.shinyepo.resourcegenerator.data.pricing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;

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
}
