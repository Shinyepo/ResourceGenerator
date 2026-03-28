package dev.shinyepo.resourcegenerator.data.patterns;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

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

    public boolean matches(BlockPos pos, BlockState block) {
        var isMatching = false;
        var element = elements.stream().filter(x -> x.offset().equals(pos)).findFirst();
        if (element.isPresent()) {
            PatternElement patternElement = element.get();
            isMatching = block.is(patternElement.allowedBlocks());
        }
        return isMatching;
    }

    public void setTier(int pValue) {
        tier = pValue;
    }

    public static class BasePatternBuilder {
        private int tier;
        private int size;
        private int depth;
        private final List<PatternElement> elements = new ArrayList<>();

        public BasePatternBuilder withTier(int tier) {
            this.tier = tier;
            this.size = tier * 2 + 1;
            this.depth = tier;
            return this;
        }

        /*
            [-2, -2, 2]    [-1, -2, 2] [0, -2, 2] [1, -2, 2] [2, -2, 2]
            [-2, -2, 1]    [-1,-1,1]   [0,-1,1]   [1,-1,1]   [2, -2, 1]
            [-2, -2, -0]   [-1,-1,0]   [0,0,0]    [1,-1,0]   [2, -2, 0]
            [-2, -2, -1]   [-1,-1,-1]  [0,-1,-1]  [1,-1,-1]  [2, -2, -1]
            [-2,-2,-2]     [-1,-2,-2]  [0,-2,-2]  [1,-2,-2]  [2,-2,-2]
         */

        private void buildLayout() {
            int half = (size - 1) / 2;
            TagKey<Block> resource = Tags.Blocks.ORES;
            TagKey<Block> upgrade = Tags.Blocks.STORAGE_BLOCKS_GOLD;

            for (int z = half; z >= -half; z--) {
                for (int x = -half; x <= half; x++) {
                    int ring = Math.max(Math.abs(x), Math.abs(z));
                    int y = -ring;
                    BlockPos offset = new BlockPos(x, y, z);

                    if (ring == 0) continue;

                    boolean useResource = (x + z) % 2 == 0;
                    elements.add(new PatternElement(
                            offset,
                            useResource ? resource : upgrade,
                            useResource ? PatternElementType.RESOURCE : PatternElementType.UPGRADE
                    ));
                }
            }
        }

        public Pattern build() {
            buildLayout();
            return new Pattern(tier, size, depth, elements);
        }
    }
}