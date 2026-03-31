package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.IPriceUpgrade;
import dev.shinyepo.resourcegenerator.blocks.entities.types.UpgradeEntity;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class OutputUpgradeEntity extends UpgradeEntity implements IPriceUpgrade {

    public OutputUpgradeEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.OUTPUT_UPGRADE_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public long apply(long price) {
        return Math.round(price * 0.95);
    }
}
