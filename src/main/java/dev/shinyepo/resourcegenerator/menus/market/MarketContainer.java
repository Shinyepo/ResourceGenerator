package dev.shinyepo.resourcegenerator.menus.market;

import dev.shinyepo.resourcegenerator.blocks.entities.MarketEntity;
import dev.shinyepo.resourcegenerator.menus.types.AbstractContainerBase;
import dev.shinyepo.resourcegenerator.registries.BlockRegistry;
import dev.shinyepo.resourcegenerator.registries.DataComponentRegistry;
import dev.shinyepo.resourcegenerator.registries.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class MarketContainer extends AbstractContainerBase {

    public MarketContainer(int windowId, Player player, BlockPos pos) {
        super(MenuRegistry.MARKET_MENU.get(), windowId, pos, 1, 0, BlockRegistry.MARKET.get());
        if (player.level().getBlockEntity(pos) instanceof MarketEntity marketEntity) {

            addSlot(marketEntity.getCardHandler(), 0, 252, 8);
            layoutPlayerInventorySlots(player.getInventory(), 108, this.defaultInventoryY);
        }
    }

    public UUID getOwnerId() {
        var cardItem = this.getSlot(0).getItem();
        if (cardItem.isEmpty()) return null;
        var cardData = cardItem.get(DataComponentRegistry.ID_CARD);
        if (cardData == null) return null;
        return cardData.userId();
    }

}
