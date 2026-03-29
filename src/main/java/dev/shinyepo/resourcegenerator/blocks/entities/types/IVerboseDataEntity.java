package dev.shinyepo.resourcegenerator.blocks.entities.types;

import net.minecraft.server.level.ServerPlayer;

public interface IVerboseDataEntity {
    void syncDataToClient(ServerPlayer player);
}
