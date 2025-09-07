package dev.shinyepo.resourcegenerator.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shinyepo.resourcegenerator.data.Network;
import dev.shinyepo.resourcegenerator.data.Permissions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.UUID;

public class ResourceGeneratorSavedData extends SavedData {
    public static final SavedDataType<ResourceGeneratorSavedData> ID = new SavedDataType<>(
            "resource_generator_networks",
            ResourceGeneratorSavedData::new,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.list(Network.CODEC).xmap(ArrayList::new, ArrayList::new).fieldOf("networks").forGetter(ResourceGeneratorSavedData::getNetworks)
            ).apply(instance, ResourceGeneratorSavedData::new))
    );

    public static ResourceGeneratorSavedData getStorage(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(ID);
    }

    private ArrayList<Network> networks = new ArrayList<>();

    public ResourceGeneratorSavedData() {

    }

    public ResourceGeneratorSavedData(ArrayList<Network> networks) {
        this.networks = networks;
    }

    public ArrayList<Network> getNetworks() {
        return networks;
    }

    public Network findNetwork(UUID id) {
        return networks.stream().filter(n -> n.getNetworkId() == id).findFirst().orElse(null);
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
        Network network = findNetwork(id);
        if (network == null) return null;
        network.setValue(value);
        setDirty();
        return network;
    }

    public Network addUserToNetwork(UUID id, UUID userId) {
        Network network = findNetwork(id);
        if (network == null) return null;
        network.addUser(userId);
        setDirty();
        return network;
    }

    public Network updateUserPermissions(UUID networkId, UUID userId, Permissions permissions) {
        Network network = findNetwork(networkId);
        if (network == null) return null;
        network.updateUser(userId, permissions);
        setDirty();
        return network;
    }
}
