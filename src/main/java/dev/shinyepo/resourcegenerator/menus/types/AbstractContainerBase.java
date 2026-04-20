package dev.shinyepo.resourcegenerator.menus.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.NonNull;

public abstract class AbstractContainerBase extends AbstractContainerMenu {
    private final BlockPos pos;
    private final Block parentBlock;
    private final int SLOT_COUNT;
    private final int INPUT_RANGE;

    protected final int defaultInventoryX = 8;
    protected final int defaultInventoryY = 84;

    public AbstractContainerBase(MenuType<?> menuType, int windowId, BlockPos pos, int slotCount, int inputRange, Block block) {
        super(menuType, windowId);
        this.pos = pos;
        this.SLOT_COUNT = slotCount;
        this.parentBlock = block;
        this.INPUT_RANGE = inputRange;
    }

    protected void addItemSlotRange(ItemStacksResourceHandler handler, int row, int column, int x, int y) {
        int index = 0;
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < row; j++) {
                addSlot(new ResourceHandlerSlot(handler, handler::set, index, x + (y * j), y + (y * i)));
                index++;
            }
        }
    }

    protected void addSlot(ItemStacksResourceHandler handler, int i, int x, int y) {
        addSlot(new ResourceHandlerSlot(handler, handler::set, i, x, y));
    }

    protected int addSlotRange(Container playerInventory, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new Slot(playerInventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    protected int addSlotBox(Container playerInventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(playerInventory, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    protected void layoutPlayerInventorySlots(Container playerInventory) {
        layoutPlayerInventorySlots(playerInventory, defaultInventoryX, defaultInventoryY);
    }


    protected void layoutPlayerInventorySlots(Container playerInventory, int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }


    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int quickMovedSlotIndex) {
        // The quick moved slot stack
        ItemStack quickMovedStack = ItemStack.EMPTY;
        // The quick moved slot
        Slot quickMovedSlot = this.slots.get(quickMovedSlotIndex);

        // If the slot is in the valid range and the slot is not empty
        if (quickMovedSlot.hasItem()) {
            // Get the raw stack to move
            ItemStack rawStack = quickMovedSlot.getItem();
            // Set the slot stack to a copy of the raw stack
            quickMovedStack = rawStack.copy();

        /*
        The following quick move logic can be simplified to if in data inventory,
        try to move to player inventory/hotbar and vice versa for containers
        that cannot transform data (e.g. chests).
        */

            // If the quick move was performed on the data inventory result slot
            if (quickMovedSlotIndex < SLOT_COUNT) {
                // Try to move the result slot into the player inventory/hotbar
                if (!this.moveItemStackTo(rawStack, SLOT_COUNT, SLOT_COUNT + Inventory.INVENTORY_SIZE, false)) {
                    // If cannot move, no longer quick move
                    return ItemStack.EMPTY;
                }

                // Perform logic on result slot quick move
                quickMovedSlot.onQuickCraft(rawStack, quickMovedStack);
            }
            // Else if the quick move was performed on the player inventory or hotbar slot
            else if (quickMovedSlotIndex < Inventory.INVENTORY_SIZE + SLOT_COUNT) {
                // Try to move the inventory/hotbar slot into the data inventory input slots
                if (!this.moveItemStackTo(rawStack, 0, INPUT_RANGE, false)) {
                    // If cannot move and in player inventory slot, try to move to hotbar
                    if (quickMovedSlotIndex < SLOT_COUNT + 27) {
                        if (!this.moveItemStackTo(rawStack, SLOT_COUNT + 27, SLOT_COUNT + Inventory.INVENTORY_SIZE, false)) {
                            // If cannot move, no longer quick move
                            return ItemStack.EMPTY;
                        }
                    }
                    // Else try to move hotbar into player inventory slot
                    else if (!this.moveItemStackTo(rawStack, SLOT_COUNT, SLOT_COUNT + 27, false)) {
                        // If cannot move, no longer quick move
                        return ItemStack.EMPTY;
                    }
                }
            }
            // Else if the quick move was performed on the data inventory input slots, try to move to player inventory/hotbar
            else if (!this.moveItemStackTo(rawStack, SLOT_COUNT, SLOT_COUNT + Inventory.INVENTORY_SIZE, false)) {
                // If cannot move, no longer quick move
                return ItemStack.EMPTY;
            }

            if (rawStack.isEmpty()) {
                // If the raw stack has completely moved out of the slot, set the slot to the empty stack
                quickMovedSlot.setByPlayer(ItemStack.EMPTY);
            } else {
                // Otherwise, notify the slot that that the stack count has changed
                quickMovedSlot.setChanged();
            }

        /*
        The following if statement and Slot#onTake call can be removed if the
        menu does not represent a container that can transform stacks (e.g.
        chests).
        */
            if (rawStack.getCount() == quickMovedStack.getCount()) {
                // If the raw stack was not able to be moved to another slot, no longer quick move
                return ItemStack.EMPTY;
            }
            // Execute logic on what to do post move with the remaining stack
            quickMovedSlot.onTake(player, rawStack);
        }

        return quickMovedStack; // Return the slot stack
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, parentBlock);
    }
}
