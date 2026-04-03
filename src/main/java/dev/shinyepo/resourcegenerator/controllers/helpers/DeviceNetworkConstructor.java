package dev.shinyepo.resourcegenerator.controllers.helpers;

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

public class DeviceNetworkConstructor {
    private final DeviceNetworkSavedData dataStore;
    private static final Map<ServerLevel, Map<BlockPos, Operation>> PENDING_OPS = new WeakHashMap<>();

    public DeviceNetworkConstructor(DeviceNetworkSavedData dataStore) {
        this.dataStore = dataStore;
    }

    public static void clear(ServerLevel level) {
        PENDING_OPS.remove(level);
    }

    public void flushPendingOperations(ServerLevel level) {
        Map<BlockPos, Operation> pending = PENDING_OPS.get(level);
        if (pending == null || pending.isEmpty()) {
            return;
        }

        PENDING_OPS.put(level, new HashMap<>());

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

    private INetworkDevice getValidNetworkDevice(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof INetworkDevice device)) {
            return null;
        }
        return device;
    }

    private Set<BlockPos> verifyAllDevices(Set<BlockPos> allDevices, ServerLevel level) {
        Set<BlockPos> validDevices = new HashSet<>();
        for (BlockPos devicePos : allDevices) {
            if (getValidNetworkDevice(level, devicePos) != null) {
                validDevices.add(devicePos);
            }
        }
        return validDevices;
    }

    //Trigger on removing device from network
    public void enqueueNetworkRebuild(ServerLevel level, BlockPos removePos) {
        INetworkCapability cap = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, removePos, null);
        if (cap == null) return;
        PENDING_OPS
                .computeIfAbsent(level, k -> new HashMap<>())
                .put(removePos.immutable(), new Operation(cap.getNetworkId(), PendingNetworkOp.DESTROY));
    }

    private void processDestroy(ServerLevel level, BlockPos pos, UUID networkId) {
        INetworkDevice device = getValidNetworkDevice(level, pos);
        if (device != null) {
            removeDeviceFromNetwork(networkId, device, pos);
        }

        rebuildNetworkFromWorld(level, networkId);
    }

    private void rebuildNetworkFromWorld(ServerLevel level, UUID networkId) {
        DeviceNetwork network = dataStore.getNetwork(networkId);
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

        Iterator<Set<BlockPos>> iterator = components.iterator();
        Set<BlockPos> firstComponent = iterator.next();
        rebuildSingleNetwork(level, network, firstComponent);

        while (iterator.hasNext()) {
            Set<BlockPos> component = iterator.next();
            if (component.isEmpty()) {
                continue;
            }

            BlockPos firstPos = component.iterator().next();
            INetworkDevice firstDevice = getValidNetworkDevice(level, firstPos);
            if (firstDevice == null) {
                continue;
            }

            UUID newId = createNetwork(level.dimension(), firstDevice, firstPos);

            for (BlockPos node : component) {
                INetworkDevice nodeDevice = getValidNetworkDevice(level, node);
                if (nodeDevice == null) {
                    continue;
                }

                UUID addedTo = addDeviceToNetwork(newId, nodeDevice, node);
                notifyDevicesOfNetworkChange(level, node, addedTo);
            }
        }
        dataStore.setDirty();
    }

    private List<Set<BlockPos>> fetchSubNetworks(ServerLevel level, DeviceNetwork network) {
        Set<BlockPos> visited = new HashSet<>();
        List<Set<BlockPos>> components = new ArrayList<>();

        Set<BlockPos> validDevices = verifyAllDevices(network.getAllDevices(), level);

        for (BlockPos device : validDevices) {
            if (!visited.contains(device)) {
                Set<BlockPos> component = new HashSet<>();
                dfs(device, component, validDevices);
                visited.addAll(component);
                components.add(component);
            }
        }

        return components;
    }

    private void rebuildSingleNetwork(ServerLevel level, DeviceNetwork network, Set<BlockPos> component) {
        network.getProducers().clear();
        network.getTransmitters().clear();
        network.getReceivers().clear();
        network.getConsumers().clear();

        for (BlockPos pos : component) {
            INetworkDevice device = getValidNetworkDevice(level, pos);
            if (device == null) {
                continue;
            }

            network.addNetworkDevice(device, pos);
            notifyDevicesOfNetworkChange(level, pos, network.getNetworkId());
        }

        dataStore.setDirty();
    }

    //Trigger on placing new network device
    public void enqueueNewNetworkConnection(ServerLevel level, BlockPos newDevice) {
        PENDING_OPS
                .computeIfAbsent(level, k -> new HashMap<>())
                .put(newDevice.immutable(), new Operation(null, PendingNetworkOp.PLACE));
    }

    public void computeNetworkOnNewConnection(ServerLevel level, BlockPos pos) {
        INetworkDevice mainDevice = getValidNetworkDevice(level, pos);
        INetworkCapability mainCap = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, pos, null);
        if (mainDevice == null || mainCap == null) return;

        Set<UUID> adjacentNetworks = getAdjacentNetworks(level, mainCap.getSides(), pos);

        mainCap.setNetworkId(computeAdjacentNetworks(level, mainDevice, pos, adjacentNetworks));
    }

    private Set<UUID> getAdjacentNetworks(ServerLevel level, SideConfig[] sides, BlockPos pos) {
        Set<UUID> adjacentNetworks = new HashSet<>();
        for (int i = 0; i < sides.length; i++) {
            if (sides[i] == SideConfig.NETWORK) {
                Direction dir = Direction.values()[i];
                INetworkCapability relCap = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, pos.relative(dir), dir.getOpposite());
                if (relCap != null) {
                    adjacentNetworks.add(relCap.getNetworkId());
                }
            }
        }
        return adjacentNetworks;
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

    public UUID createNetwork(ResourceKey<Level> dimension, INetworkDevice networkDevice, BlockPos pos) {
        return dataStore.createNetwork(dimension, networkDevice, pos);
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

    public UUID mergeNetworks(ServerLevel level, Set<UUID> networkIds, INetworkDevice device, BlockPos pos) {
        Iterator<UUID> iterator = networkIds.iterator();
        UUID targetNetworkId = iterator.next();

        DeviceNetwork targetNetwork = dataStore.getNetwork(targetNetworkId);
        if (targetNetwork == null) {
            targetNetworkId = createNetwork(level.dimension(), device, pos);
            targetNetwork = dataStore.getNetwork(targetNetworkId);
        } else {
            targetNetwork.addNetworkDevice(device, pos);
        }

        for (UUID nId : networkIds) {
            if (nId.equals(targetNetworkId)) continue;
            DeviceNetwork existingNetwork = dataStore.getNetwork(nId);

            targetNetwork.addTransmitters(existingNetwork.getTransmitters());
            targetNetwork.addProducers(existingNetwork.getProducers());
            targetNetwork.addReceivers(existingNetwork.getReceivers());
            targetNetwork.addConsumers(existingNetwork.getConsumers());

            targetNetwork.addBalance(existingNetwork.getBalance());

            UUID finalTargetNetworkId = targetNetworkId;
            existingNetwork.getReceivers().forEach((dev) -> notifyDevicesOfNetworkChange(level, dev, finalTargetNetworkId));
            existingNetwork.getTransmitters().forEach((dev) -> notifyDevicesOfNetworkChange(level, dev, finalTargetNetworkId));
            existingNetwork.getProducers().forEach((dev) -> notifyDevicesOfNetworkChange(level, dev, finalTargetNetworkId));
            existingNetwork.getConsumers().forEach((dev) -> notifyDevicesOfNetworkChange(level, dev, finalTargetNetworkId));
            dataStore.removeNetwork(nId);
        }
        notifyDevicesOfNetworkChange(level, pos, targetNetworkId);
        dataStore.setDirty();

        return targetNetworkId;

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

    private void notifyDevicesOfNetworkChange(ServerLevel level, BlockPos pos, UUID networkId) {
        INetworkCapability capability = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, pos, null);
        if (capability != null) {
            capability.setNetworkId(networkId);
        }
    }


    public void removeNetwork(UUID networkId) {
        dataStore.removeNetwork(networkId);
        dataStore.setDirty();
    }

    private void dfs(BlockPos start, Set<BlockPos> component, Set<BlockPos> allDevices) {
        ArrayDeque<BlockPos> stack = new ArrayDeque<>();
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


    private static class Operation {
        UUID networkId;
        PendingNetworkOp op;

        public Operation(UUID networkId, PendingNetworkOp op) {
            this.networkId = networkId;
            this.op = op;
        }
    }

    private enum PendingNetworkOp {
        PLACE,
        DESTROY
    }
}
