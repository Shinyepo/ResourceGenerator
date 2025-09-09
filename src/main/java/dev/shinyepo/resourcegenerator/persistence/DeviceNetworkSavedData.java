package dev.shinyepo.resourcegenerator.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shinyepo.resourcegenerator.blocks.entities.types.INetworkDevice;
import dev.shinyepo.resourcegenerator.data.DeviceNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeviceNetworkSavedData extends SavedData {
    private Map<UUID, DeviceNetwork> networks = new HashMap<>();
    public static final SavedDataType<DeviceNetworkSavedData> TYPE = new SavedDataType<>(
            "device_networks",
            DeviceNetworkSavedData::new,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.unboundedMap(UUIDUtil.STRING_CODEC, DeviceNetwork.CODEC).fieldOf("networks").forGetter(DeviceNetworkSavedData::getNetworks)
            ).apply(instance, DeviceNetworkSavedData::new))
    );

    public DeviceNetworkSavedData() {
    }

    public DeviceNetworkSavedData(Map<UUID, DeviceNetwork> networks) {
        this.networks = networks;
    }

    public static DeviceNetworkSavedData getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public Map<UUID, DeviceNetwork> getNetworks() {
        return networks;
    }

    public void setNetworks(HashMap<UUID, DeviceNetwork> networks) {
        this.networks = networks;
    }

    public DeviceNetwork getNetwork(UUID networkId) {
        return this.networks.get(networkId);
    }

    public UUID createNetwork(ResourceKey<Level> dimension, INetworkDevice networkDevice, BlockPos pos) {
        DeviceNetwork network = new DeviceNetwork(dimension, networkDevice, pos);
        networks.put(network.getNetworkId(), network);
        setDirty();
        return network.getNetworkId();
    }
}
