package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.PipeEntity;
import dev.shinyepo.resourcegenerator.blocks.types.NetworkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Pipe extends NetworkBlock {
    public Pipe(Properties properties) {
        super(PipeEntity::new, properties);

        SHAPE = Shapes.or(Block.box(7, 7, 0, 9, 9, 16),
                Block.box(7, 0, 7, 9, 7, 9),
                Block.box(7, 9, 7, 9, 16, 9),
                Block.box(9, 7, 7, 16, 9, 9),
                Block.box(0, 7, 7, 7, 9, 9));

        registerDefaultState(getStateDefinition().any());
    }
}
