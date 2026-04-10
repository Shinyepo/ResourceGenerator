package dev.shinyepo.resourcegenerator.controllers.helpers;

import dev.shinyepo.resourcegenerator.capabilities.INetworkCapability;
import dev.shinyepo.resourcegenerator.configs.SideConfig;
import dev.shinyepo.resourcegenerator.data.Network;
import dev.shinyepo.resourcegenerator.persistence.ISavedData;
import dev.shinyepo.resourcegenerator.registries.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.*;

public abstract class AbstractNetworkConstructor {
    protected final Map<ServerLevel, Map<BlockPos, Operation>> pendingOps = new WeakHashMap<>();
    private final ISavedData dataStore;

    protected AbstractNetworkConstructor(ISavedData dataStore) {
        this.dataStore = dataStore;
    }

    public void clear(ServerLevel level) {
        pendingOps.remove(level);
    }

    protected ISavedData getDataStore() {
        return dataStore;
    }


    //Trigger on removing device from network
    public void enqueueNetworkRebuild(ServerLevel level, BlockPos removePos) {
        INetworkCapability cap = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, removePos, null);
        if (cap == null) return;
        pendingOps
                .computeIfAbsent(level, k -> new HashMap<>())
                .put(removePos.immutable(), new Operation(cap.getNetworkId(), PendingNetworkOp.DESTROY));
    }

    //Trigger on placing new network device
    public void enqueueNewNetworkConnection(ServerLevel level, BlockPos newDevice) {
        pendingOps
                .computeIfAbsent(level, k -> new HashMap<>())
                .put(newDevice.immutable(), new Operation(null, PendingNetworkOp.PLACE));
    }

    protected void processDestroy(ServerLevel level, BlockPos pos, UUID networkId) {
        removeDeviceFromNetwork(level, networkId, pos);
        rebuildNetworkFromWorld(level, networkId);
    }

    protected abstract boolean isDeviceValid(ServerLevel level, BlockPos pos);

    protected abstract Network.DeviceType getValidNetworkDevice(ServerLevel level, BlockPos pos);

    protected abstract boolean isNeighbourValid(ServerLevel level, BlockPos neighbor, Direction dir);

    protected abstract INetworkCapability isNeighbourCapValid(ServerLevel level, BlockPos neighbor, Direction dir);

    protected abstract UUID mergeNetworks(ServerLevel level, Set<UUID> networkIds, Network.DeviceType device, BlockPos pos);

    private void rebuildNetworkFromWorld(ServerLevel level, UUID networkId) {
        Network network = dataStore.getNetwork(networkId);
        if (network == null) {
            return;
        }

        List<Set<BlockPos>> components = fetchSubNetworks(level, network);
        //No subnetworks, remove it
        if (components.isEmpty()) {
            removeNetwork(networkId);
            return;
        }

        //Only one subnetwork, rebuild it
        if (components.size() == 1) {
            Set<BlockPos> component = components.getFirst();
            if (component.isEmpty()) {
                removeNetwork(networkId);
                return;
            }
            rebuildSingleNetwork(level, network, component);
            return;
        }

        // Split into multiple networks.
        rebuildMultipleNetworks(level, components.iterator(), network);
    }

    protected void rebuildSingleNetwork(ServerLevel level, Network network, Set<BlockPos> component) {
        network.clearAllDevices();

        for (BlockPos pos : component) {
            Network.DeviceType device = getValidNetworkDevice(level, pos);
            if (device == null) {
                continue;
            }

            network.addDevice(device, pos);
            notifyDevicesOfNetworkChange(level, pos, network.getNetworkId());
        }

        dataStore.setDirty();
    }

    private List<Set<BlockPos>> fetchSubNetworks(ServerLevel level, Network network) {
        Set<BlockPos> visited = new HashSet<>();
        List<Set<BlockPos>> components = new ArrayList<>();

        Set<BlockPos> validDevices = verifyAllDevices(network.getAllDevices(), level);

        for (BlockPos device : validDevices) {
            if (!visited.contains(device)) {
                Set<BlockPos> component = new HashSet<>();
                dfs(level, device, component, validDevices);
                visited.addAll(component);
                components.add(component);
            }
        }

        return components;
    }

    protected Set<UUID> getAdjacentNetworks(ServerLevel level, SideConfig[] sides, BlockPos pos) {
        Set<UUID> adjacentNetworks = new HashSet<>();
        for (int i = 0; i < sides.length; i++) {
            if (sides[i] == SideConfig.NETWORK) {
                Direction dir = Direction.values()[i];
                INetworkCapability relCap = isNeighbourCapValid(level, pos.relative(dir), dir);
                if (relCap != null) {
                    adjacentNetworks.add(relCap.getNetworkId());
                }
            }
        }
        return adjacentNetworks;
    }

    private void dfs(ServerLevel level, BlockPos start, Set<BlockPos> component, Set<BlockPos> allDevices) {
        ArrayDeque<BlockPos> stack = new ArrayDeque<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            BlockPos current = stack.pop();
            if (component.add(current)) {
                for (Direction dir : Direction.values()) {
                    BlockPos neighbor = current.relative(dir);
                    boolean isValid = isNeighbourValid(level, neighbor, dir);
                    if (isValid && allDevices.contains(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }
    }


    public void removeNetwork(UUID networkId) {
        dataStore.removeNetwork(networkId);
        dataStore.setDirty();
    }

    private Set<BlockPos> verifyAllDevices(Set<BlockPos> allDevices, ServerLevel level) {
        Set<BlockPos> validDevices = new HashSet<>();
        for (BlockPos devicePos : allDevices) {
            if (isDeviceValid(level, devicePos)) {
                validDevices.add(devicePos);
            }
        }
        return validDevices;
    }

    protected void notifyDevicesOfNetworkChange(ServerLevel level, BlockPos pos, UUID networkId) {
        INetworkCapability capability = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, pos, null);
        if (capability != null) {
            capability.setNetworkId(networkId);
        }
    }

    public UUID createNetwork(ResourceKey<Level> dimension, Network.DeviceType networkDevice, BlockPos pos) {
        return dataStore.createNetwork(dimension, networkDevice, pos);
    }

    public UUID addDeviceToNetwork(UUID networkId, Network.DeviceType device, BlockPos pos) {
        Network network = dataStore.getNetwork(networkId);
        if (network != null) {
            network.addDevice(device, pos);
            dataStore.setDirty();
            return network.getNetworkId();
        }
        return null;
    }

    protected void removeDeviceFromNetwork(ServerLevel level, UUID networkId, BlockPos pos) {
        Network.DeviceType device = getValidNetworkDevice(level, pos);
        if (device == null) return;

        Network network = dataStore.getNetwork(networkId);
        if (network != null) {
            network.removeDevice(device, pos);
            if (network.isMarkedForDeletion()) {
                dataStore.removeNetwork(networkId);
            }
            dataStore.setDirty();
        }
    }

    protected UUID computeAdjacentNetworks(ServerLevel level, Network.DeviceType mainDevice, BlockPos pos, Set<UUID> networks) {
        if (networks.isEmpty()) {
            return createNetwork(level.dimension(), mainDevice, pos);
        } else if (networks.size() == 1) {
            UUID targetId = networks.iterator().next();
            return addDeviceToNetwork(targetId, mainDevice, pos);
        } else {
            return mergeNetworks(level, networks, mainDevice, pos);
        }
    }

    public void computeNetworkOnNewConnection(ServerLevel level, BlockPos pos) {
        Network.DeviceType mainDevice = getValidNetworkDevice(level, pos);
        INetworkCapability mainCap = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, pos, null);
        if (mainDevice == null || mainCap == null) return;

        Set<UUID> adjacentNetworks = getAdjacentNetworks(level, mainCap.getSides(), pos);

        mainCap.setNetworkId(computeAdjacentNetworks(level, mainDevice, pos, adjacentNetworks));
    }

    protected void rebuildMultipleNetworks(ServerLevel level, Iterator<Set<BlockPos>> iterator, Network network) {
        Set<BlockPos> firstComponent = iterator.next();
        rebuildSingleNetwork(level, network, firstComponent);

        while (iterator.hasNext()) {
            Set<BlockPos> component = iterator.next();
            if (component.isEmpty()) {
                continue;
            }

            BlockPos firstPos = component.iterator().next();
            Network.DeviceType firstDevice = getValidNetworkDevice(level, firstPos);
            if (firstDevice == null) {
                continue;
            }

            UUID newId = createNetwork(level.dimension(), firstDevice, firstPos);

            for (BlockPos node : component) {
                Network.DeviceType nodeDevice = getValidNetworkDevice(level, node);
                if (nodeDevice == null) {
                    continue;
                }

                UUID addedTo = addDeviceToNetwork(newId, nodeDevice, node);
                notifyDevicesOfNetworkChange(level, node, addedTo);
            }
        }
        dataStore.setDirty();
    }

    public void flushPendingOperations(ServerLevel level) {
//        Debug purpose sout - WHY DO NETWORKS NOT DELETE THEMSELVES SOMETIMES?!
        //Cause SavedData#setDirty only marks data to be later saved. When client is crashed data is not saved.
        //Recompile networks in SavedData on world load?
//        System.out.println(getClass().getSimpleName() + " size: " + getDataStore().getNetworkCount());
        Map<BlockPos, Operation> pending = pendingOps.get(level);
        if (pending == null || pending.isEmpty()) {
            return;
        }

        pendingOps.put(level, new HashMap<>());

        for (Map.Entry<BlockPos, Operation> entry : pending.entrySet()) {
            BlockPos pos = entry.getKey();
            Operation op = entry.getValue();

            if (op.op == PendingNetworkOp.DESTROY) {
                processDestroy(level, pos, op.networkId);
            } else {
                computeNetworkOnNewConnection(level, pos);
            }
        }
    }

    protected enum PendingNetworkOp {
        PLACE,
        DESTROY
    }

    protected static class Operation {
        UUID networkId;
        PendingNetworkOp op;

        public Operation(UUID networkId, PendingNetworkOp op) {
            this.networkId = networkId;
            this.op = op;
        }
    }
}
