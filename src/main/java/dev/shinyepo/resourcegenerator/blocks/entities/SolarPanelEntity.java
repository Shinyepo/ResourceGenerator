package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Producer;
import dev.shinyepo.resourcegenerator.configs.ProducerConfig;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanelEntity extends Producer {
    public SolarPanelEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.SOLAR_ENTITY.get(), ProducerConfig.SOLAR_PANEL, pos, blockState);
    }
}
