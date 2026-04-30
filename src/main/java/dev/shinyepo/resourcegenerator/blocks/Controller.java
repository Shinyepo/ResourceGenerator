package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.ControllerEntity;
import dev.shinyepo.resourcegenerator.blocks.types.HorizontalNetworkBlock;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Controller extends HorizontalNetworkBlock {

    public Controller(Properties properties) {
        super(properties);

        SHAPES.putAll(Shapes.rotateAll(Shapes.or(
                Block.box(0, 0, 0, 16, 16, 16))));
        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.NORTH));

        setBlockEntity(ControllerEntity::new);
        setDataContainerFactory(ControllerContainer::new);
    }
}
