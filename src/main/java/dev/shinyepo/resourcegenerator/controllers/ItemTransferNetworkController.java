package dev.shinyepo.resourcegenerator.controllers;

import dev.shinyepo.resourcegenerator.controllers.helpers.ItemTransferNetworkConstructor;
import dev.shinyepo.resourcegenerator.persistence.ItemTransferNetworkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Map;
import java.util.WeakHashMap;

public class ItemTransferNetworkController {
    private static final Map<ServerLevel, ItemTransferNetworkController> INSTANCES = new WeakHashMap<>();
    private final ItemTransferNetworkConstructor networkConstructor;

    private final ItemTransferNetworkSavedData savedData;

    private ItemTransferNetworkController(ServerLevel level) {
        savedData = ItemTransferNetworkSavedData.getOrCreate(level);
        this.networkConstructor = new ItemTransferNetworkConstructor(savedData);
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

    private void flushPendingOperation(ServerLevel level) {
        networkConstructor.flushPendingOperations(level);
    }
}
