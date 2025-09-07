package dev.shinyepo.resourcegenerator.network.domain;

import dev.shinyepo.resourcegenerator.network.domain.devices.INetworkDevice;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.UUID;

public class TestNetwork {
    private final UUID id;
    private final HashMap<BlockPos, INetworkDevice> devices = new HashMap<>();

    public TestNetwork() {
        this.id = UUID.randomUUID();
    }

    public TestNetwork(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public HashMap<BlockPos, INetworkDevice> getDevices() {
        return devices;
    }

    public INetworkDevice findDevice(BlockPos pos) {
        return devices.get(pos);
    }

    public void removeDevice(BlockPos pos) {
        devices.remove(pos);
    }

    public INetworkDevice addDevice(BlockPos pos, INetworkDevice device) {
        return devices.put(pos, device);
    }
}
