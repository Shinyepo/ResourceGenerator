package dev.shinyepo.resourcegenerator.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Account {
    private UUID accountId;
    private Long balance;
    private UUID ownerId;
    private Map<UUID, Integer> users = new HashMap<>();
    private Map<ResourceLocation, Integer> upgrades = new HashMap<>();


    public static final Codec<Account> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.CODEC.fieldOf("networkId").forGetter(Account::getAccountId),
                    Codec.LONG.fieldOf("value").forGetter(Account::getBalance),
                    UUIDUtil.CODEC.fieldOf("ownerId").forGetter(Account::getOwnerId),
                    Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.INT).fieldOf("users").forGetter(Account::getUsers),
                    Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("upgrades").forGetter(Account::getUpgrades)
            ).apply(instance, Account::new));

    public Account(UUID accountId, Long balance, UUID ownerId, Map<UUID, Integer> users, Map<ResourceLocation, Integer> upgrades) {
        this.accountId = accountId;
        this.balance = balance;
        this.ownerId = ownerId;
        this.users = users;
        this.upgrades = upgrades;
    }

    public Account(UUID userId) {
        accountId = UUID.randomUUID();
        balance = 0L;
        ownerId = userId;
        users.put(userId, Permissions.OWNER.ordinal());
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long changeValue(long amount) {
        return this.balance = balance + amount;
    }

    public Map<UUID, Integer> getUsers() {
        return users;
    }

    public Map<ResourceLocation, Integer> getUpgrades() {
        return upgrades;
    }

    public void addUpgrade(ResourceLocation id, Integer tier) {
        upgrades.put(id, tier);
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
