package dev.shinyepo.resourcegenerator.networking.packets;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.blocks.entities.types.Consumer;
import dev.shinyepo.resourcegenerator.data.sync.entity.ConsumerEntitySyncData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncConsumerEntityDataS2TCC(BlockPos pos, ConsumerEntitySyncData data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncConsumerEntityDataS2TCC> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "sync.consumer.entity.data.s2tcc"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncConsumerEntityDataS2TCC> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SyncConsumerEntityDataS2TCC::pos,
            ConsumerEntitySyncData.STREAM_CODEC,
            SyncConsumerEntityDataS2TCC::data,
            SyncConsumerEntityDataS2TCC::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handler(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level == null) return;
            if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof Consumer consumer) {
                consumer.setSyncData(data);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal(e.getMessage()));
            System.out.println(e.getLocalizedMessage());
            return null;
        });
        ;
    }
}
