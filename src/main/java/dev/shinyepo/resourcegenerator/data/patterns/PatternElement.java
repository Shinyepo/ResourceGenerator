package dev.shinyepo.resourcegenerator.data.patterns;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;

public record PatternElement(BlockPos offset, TagKey<Block> allowedBlocks, PatternElementType type) {
    public static final Codec<PatternElement> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos.CODEC.fieldOf("offset").forGetter(PatternElement::offset),
                    TagKey.codec(Registries.BLOCK).fieldOf("allowedBlocks").forGetter(PatternElement::allowedBlocks),
                    StringRepresentable.fromEnum(PatternElementType::values).fieldOf("type").forGetter(PatternElement::type)
            ).apply(instance, PatternElement::new));
}
