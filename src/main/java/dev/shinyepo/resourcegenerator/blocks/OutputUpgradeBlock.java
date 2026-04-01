package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.OutputUpgradeEntity;
import dev.shinyepo.resourcegenerator.blocks.types.UpgradeBlock;
import net.minecraft.world.level.block.Block;

public class OutputUpgradeBlock extends UpgradeBlock {

    public OutputUpgradeBlock(Properties properties) {
        super(properties);

        SHAPE = Block.box(0, 0, 0, 16, 16, 16);

        setBlockEntity(OutputUpgradeEntity::new);
        registerDefaultState(getStateDefinition().any());
    }
}
