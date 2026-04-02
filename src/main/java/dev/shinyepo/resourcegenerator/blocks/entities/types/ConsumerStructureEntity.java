package dev.shinyepo.resourcegenerator.blocks.entities.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ConsumerStructureEntity extends BasicEntity {
    private BlockPos consumerPos = null;

    public ConsumerStructureEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    public void assignConsumer(BlockPos consumerPos) {
        this.consumerPos = consumerPos;
    }

    public void notifyConsumer() {
        if (level == null || level.isClientSide()) return;
        if (consumerPos == null) return;
        if (level.getBlockEntity(consumerPos) instanceof Consumer consumerEntity) {
            consumerEntity.forceVerifyPattern();
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        //Causes infinite loop on saving and quiting
//        notifyConsumer();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (consumerPos == null) return;
        output.store("consumerPos", BlockPos.CODEC, consumerPos);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        consumerPos = input.read("consumerPos", BlockPos.CODEC).orElse(null);
    }
}
