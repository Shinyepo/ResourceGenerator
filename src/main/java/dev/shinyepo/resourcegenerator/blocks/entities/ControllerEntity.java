package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class ControllerEntity extends BlockEntity {
    private final ItemStackHandler cardHandler;
    public static List<TagKey<Item>> validInputs = List.of(TagRegistry.ID_CARDS);

    public ControllerEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.CONTROLLER_ENTITY.get(), pos, blockState);
        cardHandler = createInputItemHandler(1);
    }

    @Nonnull
    public ItemStackHandler createInputItemHandler(int slots) {
        return new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if (!validInputs.isEmpty()) {
                    return stack.getTags().anyMatch((tag) -> validInputs.contains(tag));
                }
                return true;
            }
        };
    }

    public IItemHandler getCardHandler() {
        return cardHandler;
    }
}
