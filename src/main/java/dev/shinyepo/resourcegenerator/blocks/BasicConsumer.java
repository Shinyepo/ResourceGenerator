package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.BasicConsumerEntity;
import dev.shinyepo.resourcegenerator.blocks.types.NetworkBlock;
import dev.shinyepo.resourcegenerator.menus.consumer.ConsumerContainer;
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
                Block.box(10.9, 5, 3.7, 11.9, 6.2, 4.7),
                Block.box(11.25, 4, 11.25, 12.75, 5, 12.75),
                Block.box(10.9, 5, 10.5, 11.9, 6.2, 11.5),
                Block.box(11.25, 4, 3.25, 12.75, 5, 4.75),
                Block.box(4, 5, 10.5, 5, 6.2, 11.5),
                Block.box(3.25, 4, 11.25, 4.75, 5, 12.75),
                Block.box(5, 5, 3.7, 5, 6.2, 4.7));

        registerDefaultState(getStateDefinition().any()
                .setValue(OPERATIONAL, false));
        setDataContainerFactory(ConsumerContainer::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPERATIONAL);
    }
}
