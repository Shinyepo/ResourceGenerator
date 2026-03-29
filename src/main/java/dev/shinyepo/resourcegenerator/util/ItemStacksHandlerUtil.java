package dev.shinyepo.resourcegenerator.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemStacksHandlerUtil {

    public static ItemStacksResourceHandler createInputItemHandler(int slots, Runnable onChanged) {
        return createInputItemHandler(slots, onChanged, List.of());
    }

    @Nonnull
    public static ItemStacksResourceHandler createInputItemHandler(int slots, Runnable onChanged, List<TagKey<Item>> validInputs) {
        return new ItemStacksResourceHandler(slots) {

            @Override
            protected void onContentsChanged(int slot, ItemStack previousContents) {
                onChanged.run();
            }

            @Override
            public boolean isValid(int slot, ItemResource resource) {
                if (!validInputs.isEmpty()) {
                    return resource.tags().anyMatch(validInputs::contains);
                }
                return true;
            }
        };
    }

    @Nonnull
    public static ItemStacksResourceHandler createOutputOnlyHandler(int slots, Runnable onChange) {
        return new ItemStacksResourceHandler(slots) {
            @Override
            public int insert(ItemResource resource, int amount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public boolean isValid(int index, ItemResource resource) {
                return false;
            }

            @Override
            protected void onContentsChanged(int slot, ItemStack previousContents) {
//                onChange.run();
            }
        };
    }
}
