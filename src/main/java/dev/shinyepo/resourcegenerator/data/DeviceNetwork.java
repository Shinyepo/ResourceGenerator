package dev.shinyepo.resourcegenerator.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shinyepo.resourcegenerator.blocks.entities.types.INetworkDevice;
import dev.shinyepo.resourcegenerator.blocks.entities.types.Producer;
import dev.shinyepo.resourcegenerator.blocks.entities.types.Receiver;
import dev.shinyepo.resourcegenerator.blocks.entities.types.Transmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class DeviceNetwork {
    private UUID networkId;
    private HashSet<BlockPos> producers = new HashSet<>();
    private HashSet<BlockPos> transmitters = new HashSet<>();
    private HashSet<BlockPos> receivers = new HashSet<>();
    private ResourceKey<Level> dimension;
    private transient Long balance = 0L;

    public static final Codec<DeviceNetwork> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.STRING_CODEC.fieldOf("networkId").forGetter(DeviceNetwork::getNetworkId),
                    BlockPos.CODEC.listOf().xmap(HashSet::new, List::copyOf).fieldOf("producers").forGetter(DeviceNetwork::getProducers),
                    BlockPos.CODEC.listOf().xmap(HashSet::new, List::copyOf).fieldOf("transmitters").forGetter(DeviceNetwork::getTransmitters),
                    BlockPos.CODEC.listOf().xmap(HashSet::new, List::copyOf).fieldOf("receivers").forGetter(DeviceNetwork::getReceivers),
                    ResourceLocation.CODEC.xmap(loc -> ResourceKey.create(Registries.DIMENSION, loc), ResourceKey::location).fieldOf("dimension").forGetter(DeviceNetwork::getDimension)
            ).apply(instance, DeviceNetwork::new));

    public DeviceNetwork(ResourceKey<Level> dimension, INetworkDevice device, BlockPos pos) {
        networkId = UUID.randomUUID();
        this.dimension = dimension;
        addNetworkDevice(device, pos);
    }

    public DeviceNetwork(UUID networkId, HashSet<BlockPos> producers, HashSet<BlockPos> transmitters, HashSet<BlockPos> receivers, ResourceKey<Level> dimension) {
        this.networkId = networkId;
        this.producers = producers;
        this.transmitters = transmitters;
        this.receivers = receivers;
        this.dimension = dimension;
    }

    public UUID getNetworkId() {
        return networkId;
    }

    public void setNetworkId(UUID networkId) {
        this.networkId = networkId;
    }

    public void addBalance(Long amount) {
        this.balance += amount;
    }

    public void resetBalance() {
        this.balance = 0L;
    }

    public Long getBalance() {
        return balance;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public void setDimension(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    public HashSet<BlockPos> getProducers() {
        return producers;
    }

    public void setProducers(HashSet<BlockPos> producers) {
        this.producers = producers;
    }

    public void addProducers(HashSet<BlockPos> producers) {
        this.producers.addAll(producers);
    }

    public HashSet<BlockPos> getTransmitters() {
        return transmitters;
    }

    public void setTransmitters(HashSet<BlockPos> transmitters) {
        this.transmitters = transmitters;
    }

    public void addTransmitters(HashSet<BlockPos> transmitters) {
        this.transmitters.addAll(transmitters);
    }

    public HashSet<BlockPos> getReceivers() {
        return receivers;
    }

    public void setReceivers(HashSet<BlockPos> receivers) {
        this.receivers = receivers;
    }

    public void addReceivers(HashSet<BlockPos> receivers) {
        this.receivers.addAll(receivers);
    }

    public void addNetworkDevice(INetworkDevice device, BlockPos pos) {
        if (device instanceof Producer) {
            producers.add(pos);
        }
        if (device instanceof Transmitter) {
            transmitters.add(pos);
        }
        if (device instanceof Receiver) {
            receivers.add(pos);
        }
        //TODO: Throw exception?
    }

    public boolean isMarkedForDeletion() {
        int devices = producers.size() + transmitters.size() + receivers.size();
        return devices <= 0;
    }

    public void removeDevice(INetworkDevice device, BlockPos pos) {
        if (device instanceof Producer) {
            producers.remove(pos);
        }
        if (device instanceof Transmitter) {
            transmitters.remove(pos);
        }
        if (device instanceof Receiver) {
            receivers.remove(pos);
        }

    }
}
