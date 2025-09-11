package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.SolarPanelEntity;
import dev.shinyepo.resourcegenerator.blocks.types.NetworkBlock;
import dev.shinyepo.resourcegenerator.properties.CustomProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;

public class SolarPanel extends NetworkBlock {
    private static final BooleanProperty OPERATIONAL = CustomProperties.OPERATIONAL;

    public SolarPanel(Properties properties) {
        super(SolarPanelEntity::new, properties);

        SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 3, 16),
                Block.box(4, 3, 3, 5, 7, 4),
                Block.box(4, 3, 12, 5, 7, 13),
                Block.box(11, 3, 12, 12, 7, 13),
                Block.box(11, 3, 3, 12, 7, 4),
                Block.box(5.7, 6, 5.7, 10.3, 7, 10.3),
                Block.box(0, 7, 0, 16, 8, 16));

        registerDefaultState(getStateDefinition().any()
                .setValue(OPERATIONAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPERATIONAL);
    }
}
