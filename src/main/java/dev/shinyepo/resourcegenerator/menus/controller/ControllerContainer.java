package dev.shinyepo.resourcegenerator.menus.controller;

import dev.shinyepo.resourcegenerator.blocks.entities.ControllerEntity;
import dev.shinyepo.resourcegenerator.menus.types.ContainerBase;
import dev.shinyepo.resourcegenerator.networking.CustomMessages;
import dev.shinyepo.resourcegenerator.networking.packets.BuyAccountUpgradeC2S;
import dev.shinyepo.resourcegenerator.networking.packets.RequestUpgradesSyncC2S;
import dev.shinyepo.resourcegenerator.registries.BlockRegistry;
import dev.shinyepo.resourcegenerator.registries.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.UUID;

public class ControllerContainer extends ContainerBase {
    private ControllerEntity controllerEntity;
    private final ContainerData data;

    public ControllerContainer(int windowId, Player player, BlockPos pos) {
        this(windowId, player, pos, new SimpleContainerData(2));
    }

    public ControllerContainer(int windowId, Player player, BlockPos pos, ContainerData data) {
        super(MenuRegistry.CONTROLLER_MENU.get(), windowId, pos, 1, 0, BlockRegistry.CONTROLLER.get());
        this.data = data;
        if (player.level().getBlockEntity(pos) instanceof ControllerEntity controller) {
            this.controllerEntity = controller;
            addSlot(new SlotItemHandler(controller.getCardHandler(), 0, 152, 8));
            addDataSlots(data);

            layoutPlayerInventorySlots(player.getInventory(), 8, 84);
        }
    }

    public String getOwnerName() {
        return this.controllerEntity.getOwnerName();
    }

    public void syncUpgradeData() {
        CustomMessages.sendToServer(new RequestUpgradesSyncC2S(controllerEntity.getBlockPos()));
    }

    public Long getValue() {
        return (long) data.get(0);
    }

    public Long getValueChange() {
        return (long) data.get(1);
    }

    public void buyUpgrade(ResourceLocation id, Integer tier) {
        UUID accountId = this.controllerEntity.getAccountId();
        if (accountId != null) {
            CustomMessages.sendToServer(new BuyAccountUpgradeC2S(accountId, id, tier));
        }
    }

    public void setValue(long l) {
        data.set(0, (int) l);
    }
}
