package dev.shinyepo.resourcegenerator.networking.packets;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.blocks.entities.types.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public record RequestPatternTierSyncC2S(BlockPos pos) implements CustomPacketPayload {
    public static final Type<RequestPatternTierSyncC2S> TYPE = new Type<>(fromNamespaceAndPath(ResourceGenerator.MODID, "request.pattern.tier.sync.c2s"));

    @Override
    public @NotNull CustomPacketPayload.Type<RequestPatternTierSyncC2S> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestPatternTierSyncC2S> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            RequestPatternTierSyncC2S::pos,
            RequestPatternTierSyncC2S::new);

    public void handler(IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerLevel level = (ServerLevel) context.player().level();
            var block = level.getBlockEntity(pos);
            if (block instanceof Consumer consumer) {
                consumer.cyclePattern();
            }
        });
    }
}
