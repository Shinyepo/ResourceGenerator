package dev.shinyepo.resourcegenerator.controllers.helpers;

import dev.shinyepo.resourcegenerator.data.ItemTransferNetwork;
import dev.shinyepo.resourcegenerator.persistence.ItemTransferNetworkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ItemTransferNetworkActions {
    private final ItemTransferNetworkSavedData dataStore;

    public ItemTransferNetworkActions(ItemTransferNetworkSavedData dataStore) {
        this.dataStore = dataStore;
    }

    public void addCapabilityCache(UUID networkId, BlockPos pos, Set<BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction>> cache) {
        ItemTransferNetwork network = dataStore.getNetwork(networkId);
        if (network == null) return;

        network.addCapabilityCache(pos, cache);
    }

    public boolean canOutputItems(UUID networkId) {
        ItemTransferNetwork network = dataStore.getNetwork(networkId);
        if (network == null) return false;
        return network.canOutputItems();
    }

    public List<BlockPos> getOrderedOutputs(UUID networkId, BlockPos input) {
        ItemTransferNetwork network = dataStore.getNetwork(networkId);
        if (network == null) return null;
        return network.getOrderedOutputs(input);
    }

    public ResourceHandler<ItemResource> getOutputHandler(UUID networkId, BlockPos output) {
        ItemTransferNetwork network = dataStore.getNetwork(networkId);
        if (network == null) return null;

        return network.getResourceHandler(output);
    }
}
