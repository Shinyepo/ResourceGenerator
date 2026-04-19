package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.MarketEntity;
import dev.shinyepo.resourcegenerator.blocks.types.HorizontalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;

public class Market extends HorizontalBlock {
    public Market(Properties properties) {
        super(properties);
        SHAPES.putAll(Shapes.rotateAll(Shapes.or(
                Block.box(6.25, 14, 6.25, 9.75, 15, 9.75),
                Block.box(0, 0, 14.5, 1.5, 2, 16),
                Block.box(14.5, 0, 14.5, 16, 2, 16),
                Block.box(0, 0, 0, 1.5, 2, 1.5),
                Block.box(14.5, 0, 0, 16, 2, 1.5),
                Block.box(0.5, 0.01, 0.5, 15.5, 14.01, 15.5),
                Block.box(3, 13.5, 1.6999999999999997, 13, 14.5, 4.849999999999999))));

        setBlockEntity(MarketEntity::new);
    }
}
