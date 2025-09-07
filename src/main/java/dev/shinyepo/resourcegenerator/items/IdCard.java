package dev.shinyepo.resourcegenerator.items;

import dev.shinyepo.resourcegenerator.datacomponents.IdCardData;
import dev.shinyepo.resourcegenerator.registries.DataComponentRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class IdCard extends Item {
    public IdCard(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown()) return InteractionResult.FAIL;
        ItemStack item = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            IdCardData data = item.get(DataComponentRegistry.ID_CARD.get());
            System.out.println("item data : " + data);
            if (data != null) return InteractionResult.FAIL;
            IdCardData newData = new IdCardData(player.getGameProfile().getName(), player.getGameProfile().getId());
            item.set(DataComponentRegistry.ID_CARD.get(), newData);
            System.out.println("new data : " + newData);
            player.getInventory().setChanged();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        IdCardData data = stack.get(DataComponentRegistry.ID_CARD.get());

        if (data != null) {
            tooltipAdder.accept(Component.literal("Owner " + data.username()));
            tooltipAdder.accept(Component.literal("Id " + data.userId()));
        } else {
            tooltipAdder.accept(Component.literal("Not set"));
        }
    }
}
