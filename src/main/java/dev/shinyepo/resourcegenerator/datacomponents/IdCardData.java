package dev.shinyepo.resourcegenerator.datacomponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record IdCardData(String username, UUID userId) {
    public static final Codec<IdCardData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("username").forGetter(IdCardData::username),
                    UUIDUtil.STRING_CODEC.fieldOf("userId").forGetter(IdCardData::userId)
            ).apply(instance, IdCardData::new));

    public static final StreamCodec<ByteBuf, IdCardData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, IdCardData::username,
            UUIDUtil.STREAM_CODEC, IdCardData::userId,
            IdCardData::new
    );
}
