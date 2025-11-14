package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Producer;
import dev.shinyepo.resourcegenerator.configs.ProducerConfig;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanelEntity extends Producer {
    public SolarPanelEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.SOLAR_ENTITY.get(), ProducerConfig.SOLAR_PANEL, pos, blockState);

        configureSides(Direction.DOWN);
    }

    @Override
    public void tick(ServerLevel level) {
        if (level.dimensionType().hasSkyLight() &&
                isDayTime(level) &&
                !level.isRaining() &&
                level.canSeeSky(getBlockPos().above()) &&
                level.getBrightness(LightLayer.SKY, getBlockPos().above()) > 0)
            super.tick(level);
    }

    private boolean isDayTime(ServerLevel level) {
        long dayTime = level.getDayTime() % 24000L;
        return dayTime >= 0 && dayTime < 12000;
    }
}
