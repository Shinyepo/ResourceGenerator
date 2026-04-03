package dev.shinyepo.resourcegenerator.networking.packets;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.controllers.AccountController;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public record BuyAccountUpgradeC2S(UUID requestingPlayer, UUID accountId, Identifier id,
                                   Integer tier) implements CustomPacketPayload {
    public static final Type<BuyAccountUpgradeC2S> TYPE = new Type<>(fromNamespaceAndPath(ResourceGenerator.MODID, "buy.account.upgrade.c2s"));

    @Override
    public @NotNull CustomPacketPayload.Type<BuyAccountUpgradeC2S> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, BuyAccountUpgradeC2S> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            BuyAccountUpgradeC2S::requestingPlayer,
            UUIDUtil.STREAM_CODEC,
            BuyAccountUpgradeC2S::accountId,
            Identifier.STREAM_CODEC,
            BuyAccountUpgradeC2S::id,
            ByteBufCodecs.INT,
            BuyAccountUpgradeC2S::tier,
            BuyAccountUpgradeC2S::new);

    public void handler(IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerLevel level = (ServerLevel) context.player().level();
            AccountController controller = AccountController.getInstance(level);
            controller.buyUpgrade(level, requestingPlayer, accountId, id, tier);
        });
    }
}
