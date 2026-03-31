package dev.shinyepo.resourcegenerator.data.patterns;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shinyepo.resourcegenerator.blocks.entities.ResourceImitatorEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.UpgradeEntity;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Pattern {
    public int tier;
    public int size;
    public int depth;
    public List<PatternElement> elements;
    private final HashMap<BlockPos, UpgradeEntity> upgrades = new HashMap<>();

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

    public void verifyPattern(ServerLevel level, BlockPos entityPos, Runnable onInvalid, Consumer<Item> onValid) {
        Item productItem = null;
        boolean shouldInvalidate = false;
        for (PatternElement element : this.elements) {
            BlockPos offsetPos = entityPos.offset(element.offset().getX(), element.offset().getY(), element.offset().getZ());
            BlockState offsetBlock = level.getBlockState(offsetPos);
            boolean isOffsetValid = this.matches(element.offset(), offsetBlock);
            if (!isOffsetValid) {
                shouldInvalidate = true;
                break;
            }
            if (element.type() == PatternElementType.RESOURCE) {
                ItemStack imitatorStack = getResourceFromImitator(level, offsetPos);
                if (imitatorStack == null) {
                    shouldInvalidate = true;
                    break;
                }
                productItem = isResourceValid(productItem, imitatorStack.getItem());
                if (productItem == null) {
                    shouldInvalidate = true;
                    break;
                }
            }
        }
        if (shouldInvalidate) {
            onInvalid.run();
        } else {
            onValid.accept(productItem);
        }
    }

    private Item isResourceValid(Item cachedResource, Item resource) {
        if (cachedResource == null) {
            return resource;
        } else if (!cachedResource.equals(resource)) {
            return null;
        }
        return resource;
    }

    private ItemStack getResourceFromImitator(ServerLevel level, BlockPos imitatorPos) {
        BlockEntity entity = level.getBlockEntity(imitatorPos);
        if (entity instanceof ResourceImitatorEntity imitatorEntity) {
            return imitatorEntity.getImitatedResource();
        }
        return null;
    }

    public boolean matches(BlockPos pos, BlockState block) {
        var element = elements.stream().filter(x -> x.offset().equals(pos)).findFirst();
        if (element.isPresent()) {
            PatternElement patternElement = element.get();
            return block.is(patternElement.allowedBlocks());
        }
        return false;
    }

    public HashMap<BlockPos, UpgradeEntity> getUpgrades() {
        return upgrades;
    }

    public void setTier(int pValue) {
        tier = pValue;
    }

    public void clearUpgrades() {
        upgrades.clear();
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
            TagKey<Block> resource = TagRegistry.RESOURCE_BLOCKS;
            TagKey<Block> upgrade = TagRegistry.UPGRADE_BLOCKS;

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