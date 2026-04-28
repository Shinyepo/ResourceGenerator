package dev.shinyepo.resourcegenerator.networking.packets;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.controllers.AccountController;
import dev.shinyepo.resourcegenerator.networking.CustomMessages;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public record RequestAccountBalanceSyncC2S(UUID ownerId) implements CustomPacketPayload {
    public static final Type<RequestAccountBalanceSyncC2S> TYPE = new Type<>(fromNamespaceAndPath(ResourceGenerator.MODID, "request.account.balance.c2s"));

    @Override
    public @NotNull CustomPacketPayload.Type<RequestAccountBalanceSyncC2S> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestAccountBalanceSyncC2S> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            RequestAccountBalanceSyncC2S::ownerId,
            RequestAccountBalanceSyncC2S::new);

    public void handler(IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerLevel level = (ServerLevel) context.player().level();
            AccountController controller = AccountController.getInstance(level);
            UUID accountId = controller.getOrCreateAccount(ownerId);
            Long balance = controller.getAccountBalance(accountId);
            CustomMessages.sendToPlayer(new SyncAccountBalanceS2C(balance), (ServerPlayer) context.player());
        });
    }
}
