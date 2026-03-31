package dev.shinyepo.resourcegenerator.controllers;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.Account;
import dev.shinyepo.resourcegenerator.data.Upgrade;
import dev.shinyepo.resourcegenerator.persistence.AccountSavedData;
import dev.shinyepo.resourcegenerator.registries.UpgradeRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class AccountController {
    private static final Map<ServerLevel, AccountController> INSTANCES = new WeakHashMap<>();
    private final AccountSavedData dataStore;

    protected AccountController(ServerLevel level) {
        dataStore = AccountSavedData.getOrCreate(level);
    }

    public static AccountController getInstance(ServerLevel level) {
        return INSTANCES.computeIfAbsent(level, AccountController::new);
    }

    public static void unloadData(ServerLevel level) {
        INSTANCES.remove(level);
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

    public Long addBalanceFromMachines(UUID accountId, Long amount) {
        Account account = dataStore.getAccount(accountId);
        if (account != null) {
            Integer productionUpgradeTier = account.getUpgrade(UpgradeRegistry.ABSORPTION_AMOUNT);
            Upgrade upgrade = UpgradeRegistry.getUpgradeData(UpgradeRegistry.ABSORPTION_AMOUNT);
            float upgradeBonus = upgrade.totalBonus(productionUpgradeTier);
            Long amountWithUpgrades = (long) (amount * upgradeBonus);
            Long balanceAfterChange = account.changeValue(amountWithUpgrades);
            dataStore.setDirty();
            return balanceAfterChange;
        }
        return 0L;
    }

    public Map<Identifier, Integer> getUpgrades(UUID accountId) {
        Account account = dataStore.getAccount(accountId);
        if (account != null) {
            return account.getUpgrades();
        }
        return null;
    }

    public boolean buyUpgrade(UUID accountId, Identifier id, Integer tier) {
        Account account = dataStore.getAccount(accountId);
        if (account != null) {
            return account.buyUpgrade(id, tier);
        }
        return false;
    }

    public void removeUpgrade(UUID accountId, Identifier id) {
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
