package dev.shinyepo.resourcegenerator.controllers;

import dev.shinyepo.resourcegenerator.blocks.entities.types.INetworkDevice;
import dev.shinyepo.resourcegenerator.capabilities.INetworkCapability;
import dev.shinyepo.resourcegenerator.configs.SideConfig;
import dev.shinyepo.resourcegenerator.data.DeviceNetwork;
import dev.shinyepo.resourcegenerator.persistence.DeviceNetworkSavedData;
import dev.shinyepo.resourcegenerator.registries.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.*;

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

    public UUID createNetwork(ResourceKey<Level> dimension, INetworkDevice networkDevice, BlockPos pos) {
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
                dataStore.removeNetwork(networkId);
            }
            dataStore.setDirty();
        }
    }

    public void removeNetwork(UUID networkId) {
        dataStore.removeNetwork(networkId);
        dataStore.setDirty();
    }

    public UUID mergeNetworks(ServerLevel level, Set<UUID> networkIds, INetworkDevice device, BlockPos pos) {
        UUID networkId = createNetwork(level.dimension(), device, pos);
        DeviceNetwork newNetwork = dataStore.getNetwork(networkId);

        for (UUID nId : networkIds) {
            DeviceNetwork existingNetwork = dataStore.getNetwork(nId);

            newNetwork.addTransmitters(existingNetwork.getTransmitters());
            newNetwork.addProducers(existingNetwork.getProducers());
            newNetwork.addReceivers(existingNetwork.getReceivers());

            newNetwork.addBalance(existingNetwork.getBalance());
            dataStore.removeNetwork(nId);
        }

        newNetwork.getReceivers().forEach((dev) -> notifyDevicesOfNetworkChange(level, dev, networkId));
        newNetwork.getTransmitters().forEach((dev) -> notifyDevicesOfNetworkChange(level, dev, networkId));
        newNetwork.getProducers().forEach((dev) -> notifyDevicesOfNetworkChange(level, dev, networkId));

        dataStore.setDirty();

        return networkId;

    }

    public void handleNetworkOnPlace(ServerLevel level, BlockPos pos) {
        Set<UUID> adjacentNetworks = new HashSet<>();
        INetworkCapability mainCap = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, pos, null);
        INetworkDevice mainDevice = (INetworkDevice) level.getBlockEntity(pos);
        if (mainCap != null) {
            SideConfig[] sides = mainCap.getSides();
            for (int i = 0; i < sides.length; i++) {
                if (sides[i] == SideConfig.NETWORK) {
                    Direction dir = Direction.values()[i];
                    INetworkCapability relCap = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, pos.relative(dir), dir.getOpposite());
                    if (relCap != null) {
                        adjacentNetworks.add(relCap.getNetworkId());
                    }
                }
            }

            mainCap.setNetworkId(computeAdjacentNetworks(level, mainDevice, pos, adjacentNetworks));
        }
    }


    private UUID computeAdjacentNetworks(ServerLevel level, INetworkDevice mainDevice, BlockPos pos, Set<UUID> networks) {
        if (networks.isEmpty()) {
            return createNetwork(level.dimension(), mainDevice, pos);
        } else if (networks.size() == 1) {
            UUID targetId = networks.iterator().next();
            return addDeviceToNetwork(targetId, mainDevice, pos);
        } else {
            return mergeNetworks(level, networks, mainDevice, pos);
        }
    }

    public void handleNetworkOnDestroy(ServerLevel level, BlockPos pos) {
        INetworkDevice device = (INetworkDevice) level.getBlockEntity(pos);
        INetworkCapability networkCapability = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, pos, null);

        if (networkCapability != null) {
            removeDeviceFromNetwork(networkCapability.getNetworkId(), device, pos);
            DeviceNetwork network = getNetwork(networkCapability.getNetworkId());
            if (network != null) {
                List<Set<BlockPos>> components = fetchSubNetworks(network);
                computeNetwork(level, network.getNetworkId(), components);
            }
        }
    }

    private void computeNetwork(ServerLevel level, UUID networkId, List<Set<BlockPos>> components) {
        if (components.size() > 1) {
            for (Set<BlockPos> comp : components) {
                BlockPos firstPos = comp.iterator().next();
                INetworkDevice device = (INetworkDevice) level.getBlockEntity(firstPos);
                UUID newId = createNetwork(level.dimension(), device, firstPos);
                for (BlockPos node : comp) {
                    UUID addedTo = addDeviceToNetwork(newId, (INetworkDevice) level.getBlockEntity(node), node);
                    notifyDevicesOfNetworkChange(level, node, addedTo);
                }
            }
            removeNetwork(networkId);
        }
    }

    private List<Set<BlockPos>> fetchSubNetworks(DeviceNetwork network) {
        Set<BlockPos> visited = new HashSet<>();
        List<Set<BlockPos>> components = new ArrayList<>();

        for (BlockPos device : network.getAllDevices()) {
            if (!visited.contains(device)) {
                Set<BlockPos> component = new HashSet<>();

                dfs(device, component, network.getAllDevices());
                visited.addAll(component);
                components.add(component);
            }
        }
        return components;
    }

    private void dfs(BlockPos start, Set<BlockPos> component, Set<BlockPos> allDevices) {
        Deque<BlockPos> stack = new ArrayDeque<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            BlockPos current = stack.pop();
            if (component.add(current)) {
                for (Direction dir : Direction.values()) {
                    BlockPos neighbor = current.relative(dir);
                    if (allDevices.contains(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }
    }

    private void notifyDevicesOfNetworkChange(ServerLevel level, BlockPos pos, UUID networkId) {
        INetworkCapability capability = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, pos, null);
        if (capability != null) {
            capability.setNetworkId(networkId);
        }
    }

    public DeviceNetwork getNetwork(UUID networkId) {
        return dataStore.getNetwork(networkId);
    }
}
