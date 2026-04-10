package dev.shinyepo.resourcegenerator.controllers.helpers;

import dev.shinyepo.resourcegenerator.blocks.entities.CableEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.ItemPipeEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.INetworkDevice;
import dev.shinyepo.resourcegenerator.capabilities.INetworkCapability;
import dev.shinyepo.resourcegenerator.data.ItemTransferNetwork;
import dev.shinyepo.resourcegenerator.data.Network;
import dev.shinyepo.resourcegenerator.persistence.ISavedData;
import dev.shinyepo.resourcegenerator.registries.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class ItemTransferNetworkConstructor extends AbstractNetworkConstructor {
    public ItemTransferNetworkConstructor(ISavedData dataStore) {
        super(dataStore);
    }

    @Override
    protected boolean isDeviceValid(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof ItemPipeEntity)) {
            return false;
        }
        return true;
    }

    @Override
    protected Network.DeviceType getValidNetworkDevice(ServerLevel level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof INetworkDevice device) {
            if (device instanceof ItemPipeEntity) {
                return ItemTransferNetwork.ItemTransferDeviceType.ITEM_PIPE;
            }
        }

        return null;
    }

    @Override
    public UUID mergeNetworks(ServerLevel level, Set<UUID> networkIds, Network.DeviceType device, BlockPos pos) {
        Iterator<UUID> iterator = networkIds.iterator();
        UUID targetNetworkId = iterator.next();

        
        ItemTransferNetwork targetNetwork = (ItemTransferNetwork) getDataStore().getNetwork(targetNetworkId);
        if (targetNetwork == null) {
            targetNetworkId = createNetwork(level.dimension(), device, pos);
            targetNetwork = (ItemTransferNetwork) getDataStore().getNetwork(targetNetworkId);
        } else {
            targetNetwork.addDevice(device, pos);
        }

        for (UUID nId : networkIds) {
            if (nId.equals(targetNetworkId)) continue;
            ItemTransferNetwork existingNetwork = (ItemTransferNetwork) getDataStore().getNetwork(nId);

            //TODO: Add devices from old to new network
            targetNetwork.addItemPipes(existingNetwork.getItemPipes());
            UUID finalTargetNetworkId = targetNetworkId;
            //TODO: Notify existing devices of change
            existingNetwork.getItemPipes().forEach((dev) -> notifyDevicesOfNetworkChange(level, dev, finalTargetNetworkId));
            getDataStore().removeNetwork(nId);
        }
        notifyDevicesOfNetworkChange(level, pos, targetNetworkId);
        getDataStore().setDirty();

        return targetNetworkId;

    }

    @Override
    protected boolean isNeighbourValid(ServerLevel level, BlockPos neighbor, Direction dir) {
        return level.getBlockEntity(neighbor) instanceof ItemPipeEntity || level.getCapability(Capabilities.Item.BLOCK, neighbor, dir.getOpposite()) != null;
    }

    @Override
    protected INetworkCapability isNeighbourCapValid(ServerLevel level, BlockPos neighbor, Direction dir) {
        if (level.getBlockEntity(neighbor) instanceof CableEntity) return null;
        return level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, neighbor, null);
    }
}
