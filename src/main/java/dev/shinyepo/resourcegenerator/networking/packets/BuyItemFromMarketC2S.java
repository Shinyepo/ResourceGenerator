package dev.shinyepo.resourcegenerator.networking.packets;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.controllers.AccountController;
import dev.shinyepo.resourcegenerator.registries.MarketOfferRegistry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public record BuyItemFromMarketC2S(ItemStack item, UUID ownerId) implements CustomPacketPayload {
    public static final Type<BuyItemFromMarketC2S> TYPE = new Type<>(fromNamespaceAndPath(ResourceGenerator.MODID, "buy.item.from.market.c2s"));

    @Override
    public @NotNull CustomPacketPayload.Type<BuyItemFromMarketC2S> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, BuyItemFromMarketC2S> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            BuyItemFromMarketC2S::item,
            UUIDUtil.STREAM_CODEC,
            BuyItemFromMarketC2S::ownerId,
            BuyItemFromMarketC2S::new);

    public void handler(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (item.getCount() > 64 || item.getCount() < 0) return;
            ServerLevel level = (ServerLevel) context.player().level();
            AccountController controller = AccountController.getInstance(level);
            UUID accountId = controller.getOrCreateAccount(ownerId);
            if (accountId == null) return;
            Long balance = controller.getAccountBalance(accountId);

            var offer = MarketOfferRegistry.getMarketOffer(item.getItem());
            if (offer == null) return;

            long price = item.getCount() * offer.getPrice();
            if (price > balance) return;

            ServerPlayer player = (ServerPlayer) context.player();
            var carriedItem = player.containerMenu.getCarried();
            if (carriedItem.is(item.getItem())) {
                var carriedCount = carriedItem.getCount();
                var buyCount = item.getCount();
                if (carriedCount + buyCount <= 64) {
                    controller.changeAccountBalance(level, accountId, -price);
                    carriedItem.setCount(carriedCount + buyCount);
                    player.containerMenu.setCarried(carriedItem);
                }
                return;
            }

            controller.changeAccountBalance(level, accountId, -price);
            player.containerMenu.setCarried(item);
        });
    }
}
