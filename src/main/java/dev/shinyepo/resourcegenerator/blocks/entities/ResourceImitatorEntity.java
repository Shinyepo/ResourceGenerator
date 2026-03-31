package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.BasicEntity;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ResourceImitatorEntity extends BasicEntity {
    private ItemStack imitatedResource = ItemStack.EMPTY;

    public ResourceImitatorEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.RESOURCE_IMITATOR_ENTITY.get(), worldPosition, blockState);
    }

    public void setImitatedResource(ItemStack imitatedResource) {
        this.imitatedResource = imitatedResource;
        setChanged();
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
