package dev.shinyepo.resourcegenerator.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shinyepo.resourcegenerator.data.ItemTransferNetwork;
import dev.shinyepo.resourcegenerator.data.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.UUID;

public class ItemTransferNetworkSavedData extends SavedData implements ISavedData {
    private HashMap<UUID, ItemTransferNetwork> networks = new HashMap<>();
    public static final SavedDataType<ItemTransferNetworkSavedData> TYPE = new SavedDataType<>(
            Identifier.parse("resource_generator_item_transfer_networks"),
            ItemTransferNetworkSavedData::new,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.unboundedMap(UUIDUtil.STRING_CODEC, ItemTransferNetwork.CODEC).fieldOf("networks").forGetter(ItemTransferNetworkSavedData::getNetworks)
            ).apply(instance, networks -> new ItemTransferNetworkSavedData(new HashMap<>(networks))))
    );

    public ItemTransferNetworkSavedData() {
    }

    public ItemTransferNetworkSavedData(HashMap<UUID, ItemTransferNetwork> networks) {
        this.networks = networks;
    }

    public static ItemTransferNetworkSavedData getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public HashMap<UUID, ItemTransferNetwork> getNetworks() {
        return this.networks;
    }

    public ItemTransferNetwork getNetwork(UUID networkId) {
        return this.networks.get(networkId);
    }

    @Override
    public int getNetworkCount() {
        return networks.size();
    }

    public UUID createNetwork(ResourceKey<Level> dimension, Network.DeviceType device, BlockPos pos) {
        ItemTransferNetwork network = new ItemTransferNetwork(dimension, pos);
        this.networks.put(network.getNetworkId(), network);
        setDirty();
        return network.getNetworkId();
    }

    public void removeNetwork(UUID networkId) {
        networks.remove(networkId);
        setDirty();
    }
}
