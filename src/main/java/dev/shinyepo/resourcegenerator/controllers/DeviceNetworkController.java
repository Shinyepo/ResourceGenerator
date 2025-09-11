package dev.shinyepo.resourcegenerator.controllers;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.blocks.entities.types.INetworkDevice;
import dev.shinyepo.resourcegenerator.data.DeviceNetwork;
import dev.shinyepo.resourcegenerator.persistence.DeviceNetworkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class DeviceNetworkController {
    private static DeviceNetworkController INSTANCE;
    private final DeviceNetworkSavedData dataStore;

    protected DeviceNetworkController(ServerLevel level) {
        dataStore = DeviceNetworkSavedData.getOrCreate(level);
    }

    public static DeviceNetworkController getInstance(ServerLevel level) {
        if (INSTANCE == null) INSTANCE = new DeviceNetworkController(level);
        return INSTANCE;
    }

    public void increaseNetworksBalance(UUID networkId, Long amount) {
        DeviceNetwork network = dataStore.getNetwork(networkId);
        if (network != null) {
            network.addBalance(amount);
        }
    }

    public void resetNetworksBalance(UUID networkId) {
        DeviceNetwork network = dataStore.getNetwork(networkId);
        if (network != null) {
            network.resetBalance();
        }
    }

    public Long getNetworksBalance(UUID networkId) {
        DeviceNetwork network = dataStore.getNetwork(networkId);
        if (network != null) {
            return network.getBalance();
        }
        ResourceGenerator.LOGGER.warn("Could not find network in data store.");
        return 0L;
    }

    public UUID addDeviceToNetwork(UUID networkId, INetworkDevice device, BlockPos pos) {
        DeviceNetwork network = dataStore.getNetwork(networkId);
        if (network != null) {
            network.addNetworkDevice(device, pos);
            dataStore.setDirty();
            return network.getNetworkId();
        }
        return null;
    }

    public UUID createNewNetwork(ResourceKey<Level> dimension, INetworkDevice networkDevice, BlockPos pos) {
        return dataStore.createNetwork(dimension, networkDevice, pos);
    }

    public int getNetworksSize() {
        return dataStore.getNetworks().size();
    }

    public void removeDeviceFromNetwork(UUID networkId, INetworkDevice device, BlockPos pos) {
        DeviceNetwork network = dataStore.getNetwork(networkId);
        if (network != null) {
            network.removeDevice(device, pos);
            if (network.isMarkedForDeletion()) {
                System.out.println("Network is empty, deleting...");
                dataStore.removeNetwork(networkId);
            }
            dataStore.setDirty();
        }
    }
}
