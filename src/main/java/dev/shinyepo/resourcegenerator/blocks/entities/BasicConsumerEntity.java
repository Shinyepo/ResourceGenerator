package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Consumer;
import dev.shinyepo.resourcegenerator.configs.ConsumerConfig;
import dev.shinyepo.resourcegenerator.data.patterns.PatternElement;
import dev.shinyepo.resourcegenerator.data.patterns.PatternElementType;
import dev.shinyepo.resourcegenerator.data.pricing.ResourcePriceDefinition;
import dev.shinyepo.resourcegenerator.properties.CustomProperties;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import dev.shinyepo.resourcegenerator.registries.PriceDefinitionRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import static dev.shinyepo.resourcegenerator.datagen.patterns.CustomPatternProvider.TIER_1_PATTERN;

public class BasicConsumerEntity extends Consumer {
    private boolean initialized = false;

    public BasicConsumerEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.BASIC_CONSUMER_ENTITY.get(), ConsumerConfig.BASIC_CONSUMER, pos, blockState);
        configureSides(Direction.DOWN, Direction.UP);
    }

    @Override
    public void tick(ServerLevel level) {
        if (!initialized) {
            level.registryAccess().get(TIER_1_PATTERN).ifPresent(pattern -> {
                this.pattern = pattern.value();
            });
            validatePattern(level);
            initialized = true;
        }
        if (level.getGameTime() % 20 == 0) {
            validatePattern(level);
        }
        if (!patternValid) return;
        super.tick(level);

    }

    private void validatePattern(ServerLevel level) {
        boolean shouldInvalidate = false;
        if (!initialized || pattern == null) return;
        BlockState productBlock = null;
        for (PatternElement element : pattern.elements) {
            BlockPos offsetPos = worldPosition.offset(element.offset().getX(), element.offset().getY(), element.offset().getZ());
            BlockState offsetBlock = level.getBlockState(offsetPos);
            boolean isOffsetValid = pattern.matches(element.offset(), offsetBlock);
            if (!isOffsetValid) {
                shouldInvalidate = true;
                break;
            }
            if (element.type() == PatternElementType.RESOURCE) {
                if (productBlock == null)
                    productBlock = offsetBlock;
                else if (!offsetBlock.is(productBlock.getBlock())) {
                    shouldInvalidate = true;
                    break;
                }
            }
        }
        if (shouldInvalidate && patternValid) {
            invalidatePattern(level);
        } else if (!shouldInvalidate && !patternValid) {
            product = new ItemStack(productBlock.getBlock());
            ResourcePriceDefinition priceData = PriceDefinitionRegistry.getPriceData(productBlock.getBlock());
            if (priceData != null) {
                price = priceData.getPrice();
                patternValid = true;
                level.setBlock(getBlockPos(), getBlockState().setValue(CustomProperties.OPERATIONAL, true), Block.UPDATE_ALL);
                setChanged();
            }
        }
    }

    private void invalidatePattern(ServerLevel level) {
        patternValid = false;
        level.setBlock(getBlockPos(), getBlockState().setValue(CustomProperties.OPERATIONAL, false), Block.UPDATE_ALL);
        setChanged();
    }
}
