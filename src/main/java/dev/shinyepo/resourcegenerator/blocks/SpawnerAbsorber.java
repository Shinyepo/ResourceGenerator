package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.SpawnerAbsorberEntity;
import dev.shinyepo.resourcegenerator.blocks.types.HorizontalNetworkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;

public class SpawnerAbsorber extends HorizontalNetworkBlock {

    public SpawnerAbsorber(Properties properties) {
        super(properties);

        SHAPES.putAll(Shapes.rotateAll(Shapes.or(
                Block.box(0, 0, 0, 16, 16, 16))));

        setBlockEntity(SpawnerAbsorberEntity::new);
    }
}
