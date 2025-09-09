package dev.shinyepo.resourcegenerator.networking.packets;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.blocks.entities.types.IAccountEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public record SyncOwnerS2C(String ownerName, BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncOwnerS2C> TYPE = new Type<>(fromNamespaceAndPath(ResourceGenerator.MODID, "sync.owner.s2c"));

    @Override
    public @NotNull CustomPacketPayload.Type<SyncOwnerS2C> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncOwnerS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SyncOwnerS2C::ownerName,
            BlockPos.STREAM_CODEC,
            SyncOwnerS2C::pos,
            SyncOwnerS2C::new);

    public void handler(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level == null) return;
            if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof IAccountEntity controller) {
                controller.setOwnerName(ownerName);
            }
        });
    }
}
