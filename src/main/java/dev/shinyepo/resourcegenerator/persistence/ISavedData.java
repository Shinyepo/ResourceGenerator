package dev.shinyepo.resourcegenerator.persistence;

import dev.shinyepo.resourcegenerator.data.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

public interface ISavedData {
    Network getNetwork(UUID networkId);

    int getNetworkCount();

    void removeNetwork(UUID networkId);

    void setDirty();

    UUID createNetwork(ResourceKey<Level> dimension, Network.DeviceType deviceType, BlockPos pos);
}
