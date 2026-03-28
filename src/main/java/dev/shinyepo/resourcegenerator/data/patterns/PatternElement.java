package dev.shinyepo.resourcegenerator.data.patterns;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public record PatternElement(BlockPos offset, TagKey<Block> allowedBlocks) {
    public static final Codec<PatternElement> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos.CODEC.fieldOf("offset").forGetter(PatternElement::offset),
                    TagKey.codec(Registries.BLOCK).fieldOf("allowedBlocks").forGetter(PatternElement::allowedBlocks)
            ).apply(instance, PatternElement::new));
}
