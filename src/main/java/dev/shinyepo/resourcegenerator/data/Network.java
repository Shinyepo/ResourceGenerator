package dev.shinyepo.resourcegenerator.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Network {
    private UUID networkId;
    private Long value;
    private UUID ownerId;
    private Map<UUID, Integer> users = new HashMap<>();


    public static final Codec<Network> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.CODEC.fieldOf("networkId").forGetter(Network::getNetworkId),
                    Codec.LONG.fieldOf("value").forGetter(Network::getValue),
                    UUIDUtil.CODEC.fieldOf("ownerId").forGetter(Network::getOwnerId),
                    Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.INT).fieldOf("users").forGetter(Network::getUsers)
            ).apply(instance, Network::new));

    public Network(UUID networkId, Long value, UUID ownerId, Map<UUID, Integer> users) {
        this.networkId = networkId;
        this.value = value;
        this.ownerId = ownerId;
        this.users = users;
    }

    public Network(UUID userId) {
        networkId = UUID.randomUUID();
        value = 0L;
        ownerId = userId;
        users.put(userId, Permissions.OWNER.ordinal());
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public UUID getNetworkId() {
        return networkId;
    }

    public void setNetworkId(UUID networkId) {
        this.networkId = networkId;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Map<UUID, Integer> getUsers() {
        return users;
    }

    public void updateUser(UUID userId, Permissions permissions) {
        users.put(userId, permissions.ordinal());
    }

    public void setUsers(Map<UUID, Integer> users) {
        this.users = users;
    }

    public void addUser(UUID userId) {
        this.users.put(userId, 0);
    }
}
