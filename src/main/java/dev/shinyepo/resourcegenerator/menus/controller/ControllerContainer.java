package dev.shinyepo.resourcegenerator.menus.controller;

import dev.shinyepo.resourcegenerator.blocks.entities.ControllerEntity;
import dev.shinyepo.resourcegenerator.menus.types.ContainerBase;
import dev.shinyepo.resourcegenerator.registries.BlockRegistry;
import dev.shinyepo.resourcegenerator.registries.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ControllerContainer extends ContainerBase {

    public ControllerContainer(int windowId, Player player, BlockPos pos) {
        this(windowId, player, pos, new SimpleContainerData(1));
    }

    public ControllerContainer(int windowId, Player player, BlockPos pos, ContainerData data) {
        super(MenuRegistry.CONTROLLER_MENU.get(), windowId, pos, 1, 0, BlockRegistry.CONTROLLER.get());

        BlockEntity be = player.level().getBlockEntity(pos);
        if (player.level().getBlockEntity(pos) instanceof ControllerEntity controller) {
            addSlot(new SlotItemHandler(controller.getCardHandler(), 0, 152, 8));

            layoutPlayerInventorySlots(player.getInventory(), 8, 84);
        }
    }
}
