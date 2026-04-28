package dev.shinyepo.resourcegenerator.networking.packets;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.client.AccountBalanceData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public record SyncAccountBalanceS2C(Long balance) implements CustomPacketPayload {
    public static final Type<SyncAccountBalanceS2C> TYPE = new Type<>(fromNamespaceAndPath(ResourceGenerator.MODID, "sync.account.balance.s2c"));

    @Override
    public @NotNull CustomPacketPayload.Type<SyncAccountBalanceS2C> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAccountBalanceS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.LONG,
            SyncAccountBalanceS2C::balance,
            SyncAccountBalanceS2C::new);

    public void handler(IPayloadContext context) {
        context.enqueueWork(() -> {
            AccountBalanceData.setBalance(balance);
        });
    }
}
