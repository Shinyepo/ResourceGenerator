package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.ConsumerOutputEntity;
import dev.shinyepo.resourcegenerator.blocks.types.ConsumerStructureBlock;
import net.minecraft.world.level.block.Block;

public class ConsumerOutput extends ConsumerStructureBlock {

    public ConsumerOutput(Properties properties) {
        super(properties);

        SHAPE = Block.box(0, 0, 0, 16, 16, 16);

        setBlockEntity(ConsumerOutputEntity::new);
        registerDefaultState(getStateDefinition().any());
    }


}
