package dev.shinyepo.resourcegenerator.menus.controller;

import dev.shinyepo.resourcegenerator.blocks.entities.ControllerEntity;
import dev.shinyepo.resourcegenerator.menus.types.ContainerBase;
import dev.shinyepo.resourcegenerator.networking.CustomMessages;
import dev.shinyepo.resourcegenerator.networking.packets.BuyAccountUpgradeC2S;
import dev.shinyepo.resourcegenerator.registries.BlockRegistry;
import dev.shinyepo.resourcegenerator.registries.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.UUID;

public class ControllerContainer extends ContainerBase {
    private ControllerEntity controllerEntity;
    private final DataSlot data;

    public ControllerContainer(int windowId, Player player, BlockPos pos) {
        this(windowId, player, pos, DataSlot.standalone());
    }

    public ControllerContainer(int windowId, Player player, BlockPos pos, DataSlot data) {
        super(MenuRegistry.CONTROLLER_MENU.get(), windowId, pos, 1, 0, BlockRegistry.CONTROLLER.get());
        this.data = data;
        if (player.level().getBlockEntity(pos) instanceof ControllerEntity controller) {
            this.controllerEntity = controller;
            addSlot(new SlotItemHandler(controller.getCardHandler(), 0, 152, 8));
            addDataSlot(data);

            layoutPlayerInventorySlots(player.getInventory(), 8, 84);
        }
    }

    public String getOwnerName() {
        return this.controllerEntity.getOwnerName();
    }

    public Long getValue() {
        return (long) data.get();
    }

    public void buyUpgrade(ResourceLocation id, Integer tier) {
        UUID accountId = this.controllerEntity.getAccountId();
        if (accountId != null) {
            CustomMessages.sendToServer(new BuyAccountUpgradeC2S(accountId, id, tier));
        }
    }

    public void setValue(long l) {
        data.set((int) l);
    }
}
