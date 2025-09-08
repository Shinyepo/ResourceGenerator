package dev.shinyepo.resourcegenerator.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.Network;
import dev.shinyepo.resourcegenerator.data.Permissions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.UUID;

public class NetworkSavedData extends SavedData {
    public static final SavedDataType<NetworkSavedData> ID = new SavedDataType<>(
            "resource_generator_networks",
            NetworkSavedData::new,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.list(Network.CODEC).xmap(ArrayList::new, ArrayList::new).fieldOf("networks").forGetter(NetworkSavedData::getNetworks)
            ).apply(instance, NetworkSavedData::new))
    );

    private ArrayList<Network> networks = new ArrayList<>();

    private NetworkSavedData() {

    }

    private NetworkSavedData(ArrayList<Network> networks) {
        this.networks = networks;
    }

    public static NetworkSavedData getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(ID);
    }

    public ArrayList<Network> getNetworks() {
        return networks;
    }

    public Network findNetworkByOwner(UUID ownerId) {
        return networks.stream().filter(n -> n.getOwnerId().compareTo(ownerId) == 0).findFirst().orElse(null);
    }

    public Network findNetworkById(UUID networkId) {
        return networks.stream().filter(n -> n.getNetworkId().compareTo(networkId) == 0).findFirst().orElse(null);
    }

    public UUID getOrCreateNetwork(UUID userId) {
        Network network = findNetworkByOwner(userId);
        if (network == null) network = createNetwork(userId);
        setDirty();
        return network.getNetworkId();
    }

    public Network createNetwork(UUID userId) {
        boolean userOwnsNetwork = networks.stream().anyMatch(network -> network.getOwnerId().compareTo(userId) == 0);
        if (userOwnsNetwork) return null;
        Network network = new Network(userId);
        networks.add(network);
        setDirty();
        return network;
    }

    public Network updateNetwork(UUID id, Long value) {
        Network network = findNetworkByOwner(id);
        if (network == null) return null;
        network.setValue(value);
        setDirty();
        return network;
    }

    public Network addUserToNetwork(UUID id, UUID userId) {
        Network network = findNetworkByOwner(id);
        if (network == null) return null;
        network.addUser(userId);
        setDirty();
        return network;
    }

    public Network updateUserPermissions(UUID networkId, UUID userId, Permissions permissions) {
        Network network = findNetworkByOwner(networkId);
        if (network == null) return null;
        network.updateUser(userId, permissions);
        setDirty();
        return network;
    }

    public Long changeNetworksValue(UUID networkId, long amount) {
        Network network = findNetworkById(networkId);
        if (network == null) {
            ResourceGenerator.LOGGER.error("Could not find network - {}", networkId);
            return 0L;
        }
        setDirty();
        return network.changeValue(amount);
    }
}
