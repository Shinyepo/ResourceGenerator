package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanelEntity extends BlockEntity {
    public SolarPanelEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.SOLAR_ENTITY.get(), pos, blockState);
    }
}
