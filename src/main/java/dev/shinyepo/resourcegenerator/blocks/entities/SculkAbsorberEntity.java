package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Producer;
import dev.shinyepo.resourcegenerator.configs.ProducerConfig;
import dev.shinyepo.resourcegenerator.properties.CustomProperties;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class SculkAbsorberEntity extends Producer {
    private Holder<Biome> currBiome;
    private Set<BlockPos> sculkPositions = new HashSet<>();

    public SculkAbsorberEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.SCULK_ABSORBER_ENTITY.get(), ProducerConfig.SCULK_ABSORBER, pos, blockState);

        configureSides(Direction.DOWN, Direction.UP);
    }

    @Override
    public void tick(ServerLevel level) {
        if (currBiome == null) {
            currBiome = level.getBiome(getBlockPos());
        }
        if (getBlockState().getValue(CustomProperties.OPERATIONAL)) {
            super.tick(level);
        }

        if (getBlockState().getValue(CustomProperties.OPERATIONAL) && level.getGameTime() % 5 == 0) {
            tryConsumeSculk();
        }
    }

    private void tryConsumeSculk() {
        if (currBiome.is(Biomes.DEEP_DARK)) return;
        if (sculkPositions.isEmpty()) return;
        if (level.getRandom().nextFloat() < 0.05f) {
            BlockPos sculkPos = sculkPositions.iterator().next();
            sculkPositions.remove(sculkPos);
            level.removeBlock(sculkPos, false);
            level.playSound(
                    null,
                    sculkPos,
                    SoundEvents.SCULK_BLOCK_BREAK,
                    SoundSource.BLOCKS,
                    1.0f, // volume
                    1.0f  // pitch
            );
        }
    }

    public void setSculkPositions(Set<BlockPos> sculkPositions) {
        this.sculkPositions = sculkPositions;
    }
}
