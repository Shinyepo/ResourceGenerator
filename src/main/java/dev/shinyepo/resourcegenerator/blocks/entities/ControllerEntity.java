package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.IDataEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.Receiver;
import dev.shinyepo.resourcegenerator.data.ContainerDataWrapper;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class ControllerEntity extends Receiver implements IDataEntity {
    private final ContainerData dataSlot = new ContainerDataWrapper(
            new ContainerDataWrapper.Entry(() -> Math.toIntExact(value), v -> value = (long) v),
            new ContainerDataWrapper.Entry(() -> Math.toIntExact(value - prevValue), v -> prevValue = (long) v)
    );

    public ControllerEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.CONTROLLER_ENTITY.get(), pos, blockState);
        configureSides(Direction.DOWN, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH);
    }

    public ItemStacksResourceHandler getCardHandler() {
        return accountEntity.getCardHandler();
    }

    @Override
    public void tick(ServerLevel level) {
        super.tick(level);
    }

    public ContainerData getDataSlot() {
        return dataSlot;
    }
}
