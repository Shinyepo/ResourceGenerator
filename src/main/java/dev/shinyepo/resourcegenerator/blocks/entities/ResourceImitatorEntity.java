package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.ConsumerStructureEntity;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ResourceImitatorEntity extends ConsumerStructureEntity {
    private ItemStack imitatedResource = ItemStack.EMPTY;

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
