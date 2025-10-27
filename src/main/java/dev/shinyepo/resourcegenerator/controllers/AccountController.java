package dev.shinyepo.resourcegenerator.controllers;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.Account;
import dev.shinyepo.resourcegenerator.persistence.AccountSavedData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.Map;
import java.util.UUID;

public class AccountController {
    private static AccountController INSTANCE;
    private final AccountSavedData dataStore;

    protected AccountController(ServerLevel level) {
        dataStore = AccountSavedData.getOrCreate(level);
    }

    public static AccountController getInstance(ServerLevel level) {
        if (INSTANCE == null) INSTANCE = new AccountController(level);
        return INSTANCE;
    }

    public Long changeAccountBalance(UUID accountId, Long amount) {
        Account account = dataStore.getAccount(accountId);
        if (account != null) {
            Long balance = account.changeValue(amount);
            dataStore.setDirty();
            return balance;
        }
        return 0L;
    }

    public Map<ResourceLocation, Integer> getUpgrades(UUID accountId) {
        Account account = dataStore.getAccount(accountId);
        if (account != null) {
            return account.getUpgrades();
        }
        return null;
    }

    public boolean buyUpgrade(UUID accountId, ResourceLocation id, Integer tier) {
        Account account = dataStore.getAccount(accountId);
        if (account != null) {
            return account.buyUpgrade(id, tier);
        }
        return false;
    }

    public void removeUpgrade(UUID accountId, ResourceLocation id) {
        Account account = dataStore.getAccount(accountId);
        if (account != null) {
            account.removeUpgrade(id);
        }
    }

    public Long getAccountBalance(UUID accountId) {
        Account account = dataStore.getAccount(accountId);
        if (account != null) {
            return account.getBalance();
        }
        ResourceGenerator.LOGGER.warn("Could not find account in data store.");
        return 0L;
    }

    public UUID createAccount(UUID ownerId) {
        return dataStore.createAccount(ownerId);
    }

    public UUID getOrCreateAccount(UUID ownerId) {
        Account account = dataStore.getAccountByOwner(ownerId);
        if (account == null) {
            return createAccount(ownerId);
        }
        return account.getAccountId();
    }
}
