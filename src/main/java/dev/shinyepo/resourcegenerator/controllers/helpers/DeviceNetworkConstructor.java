package dev.shinyepo.resourcegenerator.controllers.helpers;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.blocks.entities.ItemPipeEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.*;
import dev.shinyepo.resourcegenerator.capabilities.INetworkCapability;
import dev.shinyepo.resourcegenerator.data.DeviceNetwork;
import dev.shinyepo.resourcegenerator.data.Network;
import dev.shinyepo.resourcegenerator.persistence.ISavedData;
import dev.shinyepo.resourcegenerator.registries.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class DeviceNetworkConstructor extends AbstractNetworkConstructor {
    public DeviceNetworkConstructor(ISavedData dataStore) {
        super(dataStore);
    }

    @Override
    protected boolean isDeviceValid(ServerLevel level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof INetworkDevice && !(blockEntity instanceof ItemPipeEntity)) {
            return true;
        }
        return false;
    }


    @Override
    protected Network.DeviceType getValidNetworkDevice(ServerLevel level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof INetworkDevice device) {
            switch (device) {
                case Producer _ -> {
                    return DeviceNetwork.DeviceNetworkType.PRODUCER;
                }
                case Transmitter _ -> {
                    return DeviceNetwork.DeviceNetworkType.TRANSMITTER;
                }
                case Receiver _ -> {
                    return DeviceNetwork.DeviceNetworkType.RECEIVER;
                }
                case Consumer _ -> {
                    return DeviceNetwork.DeviceNetworkType.CONSUMER;
                }
                default -> {
                }
            }
        }
        return null;
    }

    @Override
    public UUID mergeNetworks(ServerLevel level, Set<UUID> networkIds, Network.DeviceType device, BlockPos pos) {
        Iterator<UUID> iterator = networkIds.iterator();
        UUID targetNetworkId = iterator.next();

        DeviceNetwork targetNetwork = (DeviceNetwork) getDataStore().getNetwork(targetNetworkId);
        if (targetNetwork == null) {
            targetNetworkId = createNetwork(level.dimension(), device, pos);
            targetNetwork = (DeviceNetwork) getDataStore().getNetwork(targetNetworkId);
        } else {
            targetNetwork.addDevice(device, pos);
        }

        for (UUID nId : networkIds) {
            if (nId.equals(targetNetworkId)) continue;
            DeviceNetwork existingNetwork = (DeviceNetwork) getDataStore().getNetwork(nId);
            if (existingNetwork == null) {
                ResourceGenerator.LOGGER.error("Encountered null network while merging networks");
                continue;
            }

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
            getDataStore().removeNetwork(nId);
        }
        notifyDevicesOfNetworkChange(level, pos, targetNetworkId);
        getDataStore().setDirty();

        return targetNetworkId;

    }

    @Override
    protected boolean isNeighbourValid(ServerLevel level, BlockPos neighbor, Direction dir) {
        if (level.getBlockEntity(neighbor) instanceof ItemPipeEntity) return false;
        INetworkCapability cap = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, neighbor, dir.getOpposite());
        return cap != null;
    }


    @Override
    protected INetworkCapability isNeighbourCapValid(ServerLevel level, BlockPos neighbor, Direction dir) {
        if (level.getBlockEntity(neighbor) instanceof ItemPipeEntity) return null;
        return level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, neighbor, dir.getOpposite());
    }
}
