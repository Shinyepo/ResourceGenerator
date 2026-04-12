package dev.shinyepo.resourcegenerator.controllers;

import dev.shinyepo.resourcegenerator.controllers.helpers.ItemTransferNetworkActions;
import dev.shinyepo.resourcegenerator.controllers.helpers.ItemTransferNetworkConstructor;
import dev.shinyepo.resourcegenerator.persistence.ItemTransferNetworkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public class ItemTransferNetworkController {
    private static final Map<ServerLevel, ItemTransferNetworkController> INSTANCES = new WeakHashMap<>();
    private final ItemTransferNetworkConstructor networkConstructor;
    private final ItemTransferNetworkActions networkActions;

    private final ItemTransferNetworkSavedData dataStore;

    private ItemTransferNetworkController(ServerLevel level) {
        dataStore = ItemTransferNetworkSavedData.getOrCreate(level);
        this.networkActions = new ItemTransferNetworkActions(dataStore);
        this.networkConstructor = new ItemTransferNetworkConstructor(dataStore);
    }

    public static ItemTransferNetworkController getInstance(ServerLevel level) {
        return INSTANCES.computeIfAbsent(level, ItemTransferNetworkController::new);
    }

    public void unloadData(ServerLevel level) {
        INSTANCES.remove(level);
        networkConstructor.clear(level);
    }

    public void handleNetworkOnPlace(ServerLevel level, BlockPos pos) {
        networkConstructor.enqueueNewNetworkConnection(level, pos);
    }

    public void handleNetworkOnDestroy(ServerLevel level, BlockPos pos) {
        networkConstructor.enqueueNetworkRebuild(level, pos);
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            ItemTransferNetworkController controller = INSTANCES.get(level);
            if (controller != null) {
                controller.flushPendingOperation(level);
            }
        }
    }

    public void addCapabilityCacheToNetwork(UUID networkId, BlockPos pos, Set<BlockCapabilityCache<ResourceHandler<ItemResource>, Direction>> cache) {
        networkActions.addCapabilityCache(networkId, pos, cache);
    }

    public boolean canOutputItems(UUID networkId) {
        return networkActions.canOutputItems(networkId);
    }

    private void flushPendingOperation(ServerLevel level) {
        networkConstructor.flushPendingOperations(level);
    }

    public ResourceHandler<ItemResource> getClosestOutput(UUID networkId, BlockPos worldPosition) {
        return networkActions.getClosestOutput(networkId, worldPosition);
    }
}
