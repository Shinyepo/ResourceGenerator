package dev.shinyepo.resourcegenerator.items;

import dev.shinyepo.resourcegenerator.datacomponents.IdCardData;
import dev.shinyepo.resourcegenerator.registries.DataComponentRegistry;
import net.minecraft.ChatFormatting;
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
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            ItemStack item = player.getItemInHand(hand);

            IdCardData newData = new IdCardData(player.nameAndId().name(), player.nameAndId().id());
            item.set(DataComponentRegistry.ID_CARD.get(), newData);

            player.getInventory().setChanged();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> tooltipAdder, @NonNull TooltipFlag flag) {
        IdCardData data = stack.get(DataComponentRegistry.ID_CARD.get());

        if (data != null) {
            tooltipAdder.accept(Component.literal(data.username()));
        } else {
            tooltipAdder.accept(Component.literal("Not set").withColor(ChatFormatting.RED.getColor()));
        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

    }
}
