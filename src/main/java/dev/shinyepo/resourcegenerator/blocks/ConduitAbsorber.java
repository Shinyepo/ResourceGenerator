package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.ConduitAbsorberEntity;
import dev.shinyepo.resourcegenerator.blocks.types.NetworkBlock;
import dev.shinyepo.resourcegenerator.properties.CustomProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class ConduitAbsorber extends NetworkBlock {
    private static final BooleanProperty OPERATIONAL = CustomProperties.OPERATIONAL;

    public ConduitAbsorber(Properties properties) {
        super(properties);

        SHAPE = Block.box(0, 0, 0, 16, 16, 16);
        setBlockEntity(ConduitAbsorberEntity::new);

        registerDefaultState(getStateDefinition().any()
                .setValue(OPERATIONAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPERATIONAL);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NonNull BlockPlaceContext context) {
        return Objects.requireNonNull(super.getStateForPlacement(context))
                .setValue(OPERATIONAL, false);
    }
}
