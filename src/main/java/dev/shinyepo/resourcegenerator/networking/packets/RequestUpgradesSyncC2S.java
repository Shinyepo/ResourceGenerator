package dev.shinyepo.resourcegenerator.networking.packets;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.blocks.entities.types.Receiver;
import dev.shinyepo.resourcegenerator.networking.CustomMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public record RequestUpgradesSyncC2S(BlockPos pos) implements CustomPacketPayload {
    public static final Type<RequestUpgradesSyncC2S> TYPE = new Type<>(fromNamespaceAndPath(ResourceGenerator.MODID, "request.upgrades.sync.c2s"));

    @Override
    public @NotNull CustomPacketPayload.Type<RequestUpgradesSyncC2S> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestUpgradesSyncC2S> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            RequestUpgradesSyncC2S::pos,
            RequestUpgradesSyncC2S::new);

    public void handler(IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerLevel level = (ServerLevel) context.player().level();
            var block = level.getBlockEntity(pos);
            if (block instanceof Receiver receiver) {
                var upgrades = receiver.getUpgrades();
                if (upgrades != null) {
                    CustomMessages.sendToPlayer(new SyncAccountUpgradesS2C(upgrades), (ServerPlayer) context.player());
                }
            }
        });
    }
}
