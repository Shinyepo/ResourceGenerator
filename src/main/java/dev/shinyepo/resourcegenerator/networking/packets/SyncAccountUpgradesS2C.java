package dev.shinyepo.resourcegenerator.networking.packets;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.client.AccountUpgradeData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public record SyncAccountUpgradesS2C(Map<ResourceLocation, Integer> upgrades) implements CustomPacketPayload {
    public static final Type<SyncAccountUpgradesS2C> TYPE = new Type<>(fromNamespaceAndPath(ResourceGenerator.MODID, "sync.account.upgrades.s2c"));

    @Override
    public @NotNull CustomPacketPayload.Type<SyncAccountUpgradesS2C> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAccountUpgradesS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ResourceLocation.STREAM_CODEC,
                    ByteBufCodecs.INT
            ),
            SyncAccountUpgradesS2C::upgrades,
            SyncAccountUpgradesS2C::new);

    public void handler(IPayloadContext context) {
        context.enqueueWork(() -> {
            AccountUpgradeData.set(upgrades);
        });
    }
}
