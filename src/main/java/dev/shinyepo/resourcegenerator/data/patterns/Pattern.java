package dev.shinyepo.resourcegenerator.data.patterns;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Pattern {
    public int tier;
    public int size;
    public int depth;
    public List<PatternElement> elements;

    public static final Codec<Pattern> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("tier").forGetter(Pattern::getTier),
                    Codec.INT.fieldOf("size").forGetter(Pattern::getSize),
                    Codec.INT.fieldOf("depth").forGetter(Pattern::getDepth),
                    Codec.list(PatternElement.CODEC).fieldOf("elements").forGetter(Pattern::getElements)
            ).apply(instance, Pattern::new));

    private Pattern(int tier, int size, int depth, List<PatternElement> elements) {
        this.tier = tier;
        this.size = size;
        this.depth = depth;
        this.elements = elements;
    }

    public List<PatternElement> getElements() {
        return elements;
    }

    public int getDepth() {
        return depth;
    }

    public int getSize() {
        return size;
    }

    public int getTier() {
        return tier;
    }

    public static class BasePatternBuilder {
        private int tier;
        private int size;
        private int depth;
        private final List<PatternElement> elements = new ArrayList<>();

        private int currentRow = 0;

        public BasePatternBuilder withTier(int tier) {
            this.tier = tier;
            return this;
        }

        public BasePatternBuilder withSize(int size) {
            this.size = size;
            return this;
        }

        public BasePatternBuilder withDepth(int depth) {
            this.depth = depth;
            return this;
        }

        public BasePatternBuilder pattern(List<TagKey<Block>> blocks) {
            if (blocks.size() != size || currentRow > size - 1) {
                throw new IllegalArgumentException("Number of blocks provided does not match pattern size");
            }
            int half = (size - 1) / 2;
            int offsetZ = half - currentRow;

            for (int x = 0; x < blocks.size(); x++) {
                int offsetX = x - half;
                elements.add(new PatternElement(new BlockPos(offsetX, 0, offsetZ), blocks.get(x)));
            }

            currentRow++;
            return this;
        }

        public Pattern build() {
            if (currentRow != size) {
                throw new IllegalStateException("Pattern not fully defined, missing rows");
            }
            return new Pattern(tier, size, depth, elements);
        }
    }
}