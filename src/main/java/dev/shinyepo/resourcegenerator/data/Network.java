package dev.shinyepo.resourcegenerator.data;

import net.minecraft.core.BlockPos;

import java.util.Set;
import java.util.UUID;

public interface Network {
    UUID getNetworkId();

    void clearAllDevices();

    Set<BlockPos> getAllDevices();

    <T extends DeviceType> void addDevice(T device, BlockPos pos);

    <T extends DeviceType> void removeDevice(T device, BlockPos pos);

    boolean isMarkedForDeletion();

    interface DeviceType {
    }
}
