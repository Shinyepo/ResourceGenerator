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
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class IdCard extends Item {
    public IdCard(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, Player player, @NonNull InteractionHand hand) {
        if (!player.isShiftKeyDown()) return InteractionResult.FAIL;
        ItemStack item = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            IdCardData data = item.get(DataComponentRegistry.ID_CARD.get());
            System.out.println("item data : " + data);
            if (data != null) return InteractionResult.FAIL;
            IdCardData newData = new IdCardData(player.nameAndId().name(), player.nameAndId().id());
            item.set(DataComponentRegistry.ID_CARD.get(), newData);
            System.out.println("new data : " + newData);
            player.getInventory().setChanged();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> tooltipAdder, @NonNull TooltipFlag flag) {
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
