package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.BasicConsumerEntity;
import dev.shinyepo.resourcegenerator.blocks.types.NetworkBlock;
import dev.shinyepo.resourcegenerator.menus.consumer.ConsumerContainer;
import dev.shinyepo.resourcegenerator.properties.CustomProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BasicConsumer extends NetworkBlock {
    private static final BooleanProperty OPERATIONAL = CustomProperties.OPERATIONAL;

    public BasicConsumer(Properties properties) {
        super(properties);

        SHAPE = Block.box(0, 0, 0, 16, 16, 16);

        setBlockEntity(BasicConsumerEntity::new);

        registerDefaultState(getStateDefinition().any()
                .setValue(OPERATIONAL, false));
        setBasicContainerFactory(ConsumerContainer::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPERATIONAL);
    }
}
