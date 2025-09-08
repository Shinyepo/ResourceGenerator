package dev.shinyepo.resourcegenerator.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
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
}
