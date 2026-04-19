package dev.shinyepo.resourcegenerator.util;

import dev.shinyepo.resourcegenerator.data.NBTTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ItemStacksHandlerUtil {
    //ItemPipe
    //Limit amount of items in pipe buffer?
    //get rid of buffer entirely?
    public static ItemStacksResourceHandler createItemTransferHandler(BooleanSupplier isValid) {
        return new ItemStacksResourceHandler(1) {
            @Override
            public boolean isValid(int index, ItemResource resource) {
                return isValid.getAsBoolean();
            }
        };
    }

    public static ItemStacksResourceHandler createInputItemHandler(int slots, Runnable onChanged) {
        return createInputItemHandler(NBTTags.DEFAULT, slots, onChanged, List.of());
    }

    public static ItemStacksResourceHandler createInputItemHandler(int slots, Runnable onChanged, List<TagKey<Item>> validInputs) {
        return createInputItemHandler(NBTTags.DEFAULT, slots, onChanged, validInputs);
    }

    @Nonnull
    public static ItemStacksResourceHandler createInputItemHandler(NBTTags tag, int slots, Runnable onChanged, List<TagKey<Item>> validInputs) {
        return new ItemStacksResourceHandler(slots) {
            @Override
            public void serialize(ValueOutput output) {
                if (tag == NBTTags.DEFAULT) {
                    super.serialize(output);
                    return;
                }
                output.store(tag.getTag(), codec, stacks);
            }

            @Override
            public void deserialize(ValueInput input) {
                if (tag == NBTTags.DEFAULT) {
                    super.deserialize(input);
                    return;
                }
                input.read(tag.getTag(), codec).ifPresent(this::setStacks);
            }

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

    public static ItemStacksResourceHandler createOutputOnlyHandler(NBTTags tag, int slots, Runnable onChange) {
        return new ItemStacksResourceHandler(slots) {
            @Override
            public void serialize(ValueOutput output) {
                if (tag == NBTTags.DEFAULT) {
                    super.serialize(output);
                    return;
                }
                output.store(tag.getTag(), codec, stacks);
            }

            @Override
            public void deserialize(ValueInput input) {
                if (tag == NBTTags.DEFAULT) {
                    super.deserialize(input);
                    return;
                }
                input.read(tag.getTag(), codec);
            }

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
                onChange.run();
            }
        };
    }

    @Nonnull
    public static ItemStacksResourceHandler createOutputOnlyHandler(int slots, Runnable onChange) {
        return createOutputOnlyHandler(NBTTags.DEFAULT, slots, onChange);
    }
}
