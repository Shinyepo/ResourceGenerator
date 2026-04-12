package dev.shinyepo.resourcegenerator.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class ItemTransferNetwork implements Network {
    private UUID networkId;
    private HashSet<BlockPos> itemPipes = new HashSet<>();
    private final HashMap<BlockPos, Set<BlockCapabilityCache<ResourceHandler<ItemResource>, Direction>>> capabilityCaches = new HashMap<>();
    private final HashMap<BlockPos, BlockPos> closestOutputCache = new HashMap<>();
    private ResourceKey<Level> dimension;

    public static final Codec<ItemTransferNetwork> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.STRING_CODEC.fieldOf("networkId").forGetter(ItemTransferNetwork::getNetworkId),
                    BlockPos.CODEC.listOf().xmap(HashSet::new, List::copyOf).fieldOf("itemPipes").forGetter(ItemTransferNetwork::getItemPipes),
                    Identifier.CODEC.xmap(loc -> ResourceKey.create(Registries.DIMENSION, loc), ResourceKey::identifier).fieldOf("dimension").forGetter(ItemTransferNetwork::getDimension)
            ).apply(instance, ItemTransferNetwork::new));

    public ItemTransferNetwork(UUID networkId, HashSet<BlockPos> itemPipes, ResourceKey<Level> dimension) {
        this.networkId = networkId;
        this.itemPipes = itemPipes;
        this.dimension = dimension;
    }

    //Create network from ItemPipe
    public ItemTransferNetwork(ResourceKey<Level> dimension, BlockPos pos) {
        this.networkId = UUID.randomUUID();
        this.dimension = dimension;
        addItemPipe(pos);
    }

    public UUID getNetworkId() {
        return networkId;
    }

    public void setNetworkId(UUID networkId) {
        this.networkId = networkId;
    }

    public HashSet<BlockPos> getItemPipes() {
        return itemPipes;
    }

    public void setItemPipes(HashSet<BlockPos> itemPipes) {
        this.itemPipes = itemPipes;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public void setDimension(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    public void addItemPipe(BlockPos pos) {
        this.itemPipes.add(pos);
    }

    public void removeItemPipe(BlockPos pos) {
        this.itemPipes.remove(pos);
    }

    public void addItemPipes(HashSet<BlockPos> pipes) {
        this.itemPipes.addAll(pipes);
    }

    public boolean isMarkedForDeletion() {
        int items = itemPipes.size();
        return items == 0;
    }

    @Override
    public void clearAllDevices() {
        itemPipes.clear();
    }

    @Override
    public Set<BlockPos> getAllDevices() {
        return new HashSet<>(itemPipes);
    }

    @Override
    public <T extends DeviceType> void addDevice(T device, BlockPos pos) {
        if (device == ItemTransferDeviceType.ITEM_PIPE) {
            itemPipes.add(pos);
        }
        //Throw exception?
    }

    @Override
    public <T extends DeviceType> void removeDevice(T device, BlockPos pos) {
        if (device == ItemTransferDeviceType.ITEM_PIPE) {
            itemPipes.remove(pos);
        }
        //Throw exception?
    }

    public void addCapabilityCache(BlockPos pos, Set<BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction>> cache) {
        closestOutputCache.clear();
        if (cache.isEmpty()) {
            capabilityCaches.remove(pos);
            return;
        }
        capabilityCaches.put(pos, cache);
    }

    public boolean canOutputItems() {
        return !capabilityCaches.isEmpty();
    }

    public ResourceHandler<ItemResource> getClosestOutput(BlockPos input) {
        if (capabilityCaches.isEmpty()) return null;
        BlockPos closest = closestOutputCache.computeIfAbsent(input, this::cacheClosestOutput);

        var cap = capabilityCaches.get(closest);
        if (cap == null || cap.isEmpty()) return null;
        return cap.iterator().next().getCapability();
    }

    private BlockPos cacheClosestOutput(BlockPos input) {
        BlockPos closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (BlockPos pos : capabilityCaches.keySet()) {
            double distance = input.distSqr(pos);
            if (distance < closestDistance) {
                closest = pos;
                closestDistance = distance;
            }
        }

        return closest;
    }

    public enum ItemTransferDeviceType implements DeviceType {
        ITEM_PIPE
    }
}
