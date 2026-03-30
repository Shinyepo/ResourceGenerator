package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.OutputUpgradeEntity;
import dev.shinyepo.resourcegenerator.blocks.types.UpgradeBlock;
import net.minecraft.world.level.block.Block;

public class OutputUpgradeBlock extends UpgradeBlock {

    public OutputUpgradeBlock(Properties properties) {
        super(OutputUpgradeEntity::new, properties);

        SHAPE = Block.box(0, 0, 0, 16, 16, 16);
        registerDefaultState(getStateDefinition().any());
    }
}
