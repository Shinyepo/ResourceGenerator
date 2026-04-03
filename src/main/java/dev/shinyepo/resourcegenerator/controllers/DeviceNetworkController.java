package dev.shinyepo.resourcegenerator.controllers;

import dev.shinyepo.resourcegenerator.controllers.helpers.DeviceNetworkConstructor;
import dev.shinyepo.resourcegenerator.data.DeviceNetwork;
import dev.shinyepo.resourcegenerator.persistence.DeviceNetworkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class DeviceNetworkController {
    private static final Map<ServerLevel, DeviceNetworkController> INSTANCES = new WeakHashMap<>();
    private final DeviceNetworkConstructor networkConstructor;

    private final DeviceNetworkSavedData dataStore;

    protected DeviceNetworkController(ServerLevel level) {
        dataStore = DeviceNetworkSavedData.getOrCreate(level);
        this.networkConstructor = new DeviceNetworkConstructor(dataStore);
    }

    public static DeviceNetworkController getInstance(ServerLevel level) {
        return INSTANCES.computeIfAbsent(level, DeviceNetworkController::new);
    }

    public static void unloadData(ServerLevel level) {
        INSTANCES.remove(level);
        DeviceNetworkConstructor.clear(level);
    }

    public void handleNetworkOnPlace(ServerLevel level, BlockPos pos) {
        networkConstructor.enqueueNewNetworkConnection(level, pos);
    }

    public void handleNetworkOnDestroy(ServerLevel level, BlockPos pos) {
        networkConstructor.enqueueNetworkRebuild(level, pos);
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            DeviceNetworkController controller = INSTANCES.get(level);
            if (controller != null) {
                controller.flushPendingOperations(level);
            }
        }
    }

    private void flushPendingOperations(ServerLevel level) {
        networkConstructor.flushPendingOperations(level);
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
        return 0L;
    }

    public int getNetworksSize() {
        return dataStore.getNetworks().size();
    }

    public BlockPos getReceiverFromNetwork(UUID networkId) {
        if (dataStore.getNetwork(networkId) != null) {
            return dataStore.getNetwork(networkId).getReceiver();
        }
        return null;
    }

    public DeviceNetwork getNetwork(UUID networkId) {
        return dataStore.getNetwork(networkId);
    }
}