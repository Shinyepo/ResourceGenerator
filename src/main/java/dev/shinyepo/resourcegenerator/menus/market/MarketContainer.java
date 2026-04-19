package dev.shinyepo.resourcegenerator.menus.market;

import dev.shinyepo.resourcegenerator.blocks.entities.MarketEntity;
import dev.shinyepo.resourcegenerator.menus.types.AbstractContainerBase;
import dev.shinyepo.resourcegenerator.registries.BlockRegistry;
import dev.shinyepo.resourcegenerator.registries.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class MarketContainer extends AbstractContainerBase {
    private MarketEntity marketEntity;

    public MarketContainer(int windowId, Player player, BlockPos pos) {
        super(MenuRegistry.MARKET_MENU.get(), windowId, pos, 1, 0, BlockRegistry.MARKET.get());
        if (player.level().getBlockEntity(pos) instanceof MarketEntity marketEntity) {
            this.marketEntity = marketEntity;

            addSlot(marketEntity.getCardHandler(), 0, 152, 8);
            addSlot(marketEntity.getOutputHandler(), 0, 86, 36);
            layoutPlayerInventorySlots(player.getInventory());
        }
    }

    public String getOwnerName() {
        return marketEntity.getOwnerName();
    }

}
