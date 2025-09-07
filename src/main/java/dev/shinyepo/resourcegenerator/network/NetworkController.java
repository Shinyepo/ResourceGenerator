package dev.shinyepo.resourcegenerator.network;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.network.domain.TestNetwork;
import dev.shinyepo.resourcegenerator.network.domain.devices.INetworkDevice;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.UUID;

public class NetworkController {
    private static final NetworkController INSTANCE = new NetworkController();
    private static Boolean INITIALIZED = false;
    private final HashMap<UUID, TestNetwork> networks = new HashMap<>();

    public static void init() {
        if (!INITIALIZED) {
            INITIALIZED = true;
            getInstance();
            ResourceGenerator.LOGGER.info("Device network controller initialized.");
        }
    }

    public static void safeShutdown() {
        if (INITIALIZED) {
            assert getInstance() != null;
            getInstance().networks.clear();
            INITIALIZED = false;
            ResourceGenerator.LOGGER.info("Safely shutdown device network controller.");
        }
    }

    public static NetworkController getInstance() {
        if (!INITIALIZED) return null;
        if (INSTANCE == null) {
            return new NetworkController();
        }
        return INSTANCE;
    }

    public HashMap<UUID, TestNetwork> getNetworks() {
        return networks;
    }

    public TestNetwork addNetwork(TestNetwork testNetwork) {
        return networks.put(testNetwork.getId(), testNetwork);
    }

    public TestNetwork findNetwork(UUID id) {
        return networks.get(id);
    }

    public void removeNetwork(UUID id) {
        networks.remove(id);
    }

    public TestNetwork createNetwork() {
        TestNetwork testNetwork = new TestNetwork();
        testNetwork = addNetwork(testNetwork);
        return testNetwork;
    }

    public TestNetwork loadInNetwork(UUID id, BlockPos pos, INetworkDevice device) {

        if (networks.get(id) == null) {
            TestNetwork testNetwork = new TestNetwork(id);
            addNetwork(testNetwork);
            testNetwork.addDevice(pos, device);
            return testNetwork;
        } else {
            TestNetwork testNetwork = networks.get(id);
            testNetwork.addDevice(pos, device);
            return testNetwork;
        }
    }
}
