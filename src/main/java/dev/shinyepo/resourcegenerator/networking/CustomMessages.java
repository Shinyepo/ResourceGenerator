package dev.shinyepo.resourcegenerator.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;

public class CustomMessages {
    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        ClientPacketDistributor.sendToServer(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToPlayer(MSG message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }

    public static <MSG extends CustomPacketPayload> void sendToAllPlayers(MSG message) {
        PacketDistributor.sendToAllPlayers(message);
    }

    //Doesnt work why!?
    public static <MSG extends CustomPacketPayload> void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos pos, MSG message) {
        PacketDistributor.sendToPlayersTrackingChunk(level, pos, message);
    }
}
