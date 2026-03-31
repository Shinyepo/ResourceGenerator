package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.BasicEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.Consumer;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ResourceImitatorEntity extends BasicEntity {
    private ItemStack imitatedResource = ItemStack.EMPTY;
    private BlockPos consumerPos = null;

    public ResourceImitatorEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.RESOURCE_IMITATOR_ENTITY.get(), worldPosition, blockState);
    }


    /*
        Returns
        - ItemStack.EMPTY - if the resource was successfully set
        - resourceToImitate - if the resource is already imitated
        - replacedResource - if the resource was successfully replaced
     */
    public ItemStack setImitatedResource(ItemStack resourceToImitate) {
        //This resource is already imitated
        if (this.imitatedResource.is(resourceToImitate.getItem())) {
            return resourceToImitate;
        }

        ItemStack imitationResult = ItemStack.EMPTY;

        if (this.imitatedResource.isEmpty()) {
            setResource(resourceToImitate);
        } else {
            imitationResult = replaceResource(resourceToImitate);
        }

        setChanged();
        notifyConsumer();
        return imitationResult;
    }

    private void setResource(ItemStack resource) {
        this.imitatedResource = resource;
    }

    private ItemStack replaceResource(ItemStack resource) {
        ItemStack replacedResource = this.imitatedResource;
        this.imitatedResource = resource;
        return replacedResource;
    }

    public ItemStack getImitatedResource() {
        return imitatedResource;
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
        notifyConsumer();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (imitatedResource.isEmpty()) return;
        output.store("imitatedResource", ItemStack.CODEC, imitatedResource);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        imitatedResource = input.read("imitatedResource", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }
}
