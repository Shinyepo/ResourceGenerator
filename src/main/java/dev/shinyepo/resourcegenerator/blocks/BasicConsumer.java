package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.BasicConsumerEntity;
import dev.shinyepo.resourcegenerator.blocks.types.NetworkBlock;
import dev.shinyepo.resourcegenerator.properties.CustomProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;

public class BasicConsumer extends NetworkBlock {
    private static final BooleanProperty OPERATIONAL = CustomProperties.OPERATIONAL;

    public BasicConsumer(Properties properties) {
        super(BasicConsumerEntity::new, properties);

        SHAPE = Shapes.or(Block.box(7, 7, 7, 9, 9, 9),
                Block.box(6, 6, 6, 10, 10, 10),
                Block.box(0, 0, 0, 16, 1, 16),
                Block.box(1, 1, 1, 15, 2, 15),
                Block.box(2, 2, 2, 14, 3, 14),
                Block.box(3, 3, 3, 13, 4, 13),
                Block.box(3.25, 4, 3.25, 4.75, 5, 4.75),
                Block.box(10.9, 4.999999999999999, 3.6757359312880715, 11.9, 6.199999999999999, 4.67573593128807),
                Block.box(11.25, 4, 11.25, 12.75, 5, 12.75),
                Block.box(10.9, 4.999999999999999, 10.475735931288076, 11.9, 6.199999999999999, 11.475735931288074),
                Block.box(11.25, 4, 3.25, 12.75, 5, 4.75),
                Block.box(4.099999999999999, 4.999999999999999, 10.475735931288071, 5.099999999999998, 6.199999999999999, 11.47573593128807),
                Block.box(3.25, 4, 11.25, 4.75, 5, 12.75),
                Block.box(4.099999999999999, 4.999999999999999, 3.6757359312880715, 5.099999999999998, 6.199999999999999, 4.67573593128807));

        registerDefaultState(getStateDefinition().any()
                .setValue(OPERATIONAL, false));
        setBasicContainerFactory(ConsumerContainer::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPERATIONAL);
    }
}
