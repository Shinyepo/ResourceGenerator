package dev.shinyepo.resourcegenerator.blocks.entities;

import com.google.common.collect.Lists;
import dev.shinyepo.resourcegenerator.blocks.entities.types.Producer;
import dev.shinyepo.resourcegenerator.configs.ProducerConfig;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static dev.shinyepo.resourcegenerator.properties.CustomProperties.OPERATIONAL;

public class ConduitAbsorberEntity extends Producer {
    private boolean isActive;
    private final List<BlockPos> effectBlocks = Lists.newArrayList();

    public ConduitAbsorberEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.CONDUIT_ABSORBER_ENTITY.get(), ProducerConfig.CONDUIT_ABSORBER, pos, blockState);

        configureSides(Direction.DOWN, Direction.UP);
    }

    @Override
    public void tick(ServerLevel level) {
        List<BlockPos> effectBlocks = this.effectBlocks;
        if (level.getGameTime() % 20 == 0) {
            boolean active = updateShape(level, worldPosition, effectBlocks);
            if (active != isActive) {
                SoundEvent event = active ? SoundEvents.CONDUIT_ACTIVATE : SoundEvents.CONDUIT_DEACTIVATE;
                level.playSound(null, worldPosition, event, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.setBlock(worldPosition, getBlockState().setValue(OPERATIONAL, active), Block.UPDATE_ALL);
            }

            isActive = active;
            if (isActive) {
                super.tick(level);
            }
        }
    }

    private static boolean updateShape(Level level, BlockPos worldPosition, List<BlockPos> effectBlocks) {
        effectBlocks.clear();

        for (int ox = -1; ox <= 1; ++ox) {
            for (int oy = -1; oy <= 1; ++oy) {
                for (int oz = -1; oz <= 1; ++oz) {
                    //Skip center - for absorber
                    if (ox == 0 && oz == 0 && oy == 0) {
                        continue;
                    }
                    BlockPos testPos = worldPosition.offset(ox, oy, oz);
                    if (!level.isWaterAt(testPos)) {
                        return false;
                    }
                }
            }
        }

        for (int ox = -2; ox <= 2; ++ox) {
            for (int oy = -2; oy <= 2; ++oy) {
                for (int ozx = -2; ozx <= 2; ++ozx) {
                    int ax = Math.abs(ox);
                    int ay = Math.abs(oy);
                    int az = Math.abs(ozx);
                    if ((ax > 1 || ay > 1 || az > 1) && (ox == 0 && (ay == 2 || az == 2) || oy == 0 && (ax == 2 || az == 2) || ozx == 0 && (ax == 2 || ay == 2))) {
                        BlockPos testPos = worldPosition.offset(ox, oy, ozx);
                        BlockState testBlock = level.getBlockState(testPos);
                        if (testBlock.isConduitFrame(level, testPos, worldPosition)) {
                            effectBlocks.add(testPos);
                        }
                    }
                }
            }
        }

        return effectBlocks.size() >= 16;
    }
}
