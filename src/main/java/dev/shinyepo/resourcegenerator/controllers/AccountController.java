package dev.shinyepo.resourcegenerator.controllers;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.Account;
import dev.shinyepo.resourcegenerator.data.Upgrade;
import dev.shinyepo.resourcegenerator.persistence.AccountSavedData;
import dev.shinyepo.resourcegenerator.registries.UpgradeRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class AccountController {
    private static final Map<ServerLevel, AccountController> INSTANCES = new WeakHashMap<>();
    private static final Map<ServerLevel, Map<UUID, AccountOperation>> PENDING_OPS = new WeakHashMap<>();
    private final AccountSavedData dataStore;

    protected AccountController(ServerLevel level) {
        dataStore = AccountSavedData.getOrCreate(level);
    }

    public static AccountController getInstance(ServerLevel level) {
        return INSTANCES.computeIfAbsent(level, AccountController::new);
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            AccountController controller = INSTANCES.get(level);
            if (controller != null) {
                controller.flushPendingOperations(level);
            }
        }
    }

    public void flushPendingOperations(ServerLevel level) {
        Map<UUID, AccountOperation> ops = PENDING_OPS.get(level);
        if (ops == null || ops.isEmpty()) {
            return;
        }

        PENDING_OPS.put(level, new WeakHashMap<>());

        for (Map.Entry<UUID, AccountOperation> entry : ops.entrySet()) {
            UUID accountId = entry.getKey();
            AccountOperation op = entry.getValue();

            Account account = dataStore.getAccount(accountId);
            if (account == null) {
                continue;
            }
            account.changeValue(op.balanceDelta);

            op.upgrades.forEach(account::buyUpgrade);
        }
        dataStore.setDirty();
    }

    public static void unloadData(ServerLevel level) {
        INSTANCES.remove(level);
    }

    public void changeAccountBalance(ServerLevel level, UUID accountId, Long amount) {
        Account account = dataStore.getAccount(accountId);
        if (account != null) {
            PENDING_OPS.computeIfAbsent(level, k -> new WeakHashMap<>())
                    .computeIfAbsent(accountId, k -> new AccountOperation(0))
                    .updateBalanceDelta(amount);
        }
    }

    public void addBalanceFromMachines(ServerLevel level, UUID accountId, Long amount) {
        Account account = dataStore.getAccount(accountId);
        if (account != null) {
            Integer productionUpgradeTier = account.getUpgrade(UpgradeRegistry.ABSORPTION_AMOUNT);
            Upgrade upgrade = UpgradeRegistry.getUpgradeData(UpgradeRegistry.ABSORPTION_AMOUNT);
            float upgradeBonus = upgrade.totalBonus(productionUpgradeTier);
            long amountWithUpgrades = (long) (amount * upgradeBonus);
            changeAccountBalance(level, accountId, amountWithUpgrades);
        }
    }

    public Map<Identifier, Integer> getUpgrades(UUID accountId) {
        Account account = dataStore.getAccount(accountId);
        if (account != null) {
            return account.getUpgrades();
        }
        return null;
    }

    public boolean buyUpgrade(ServerLevel level, UUID accountId, Identifier id, Integer tier) {
        Account account = dataStore.getAccount(accountId);
        if (account != null) {
            PENDING_OPS.computeIfAbsent(level, k -> new WeakHashMap<>())
                    .computeIfAbsent(accountId, k -> new AccountOperation(0))
                    .buyUpgrade(id, tier);
            return true;
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

    private static class AccountOperation {
        long balanceDelta;
        Map<Identifier, Integer> upgrades = new WeakHashMap<>();

        public AccountOperation(long value) {
            this.balanceDelta = value;
        }

        public void updateBalanceDelta(long change) {
            balanceDelta += change;
        }

        public void buyUpgrade(Identifier id, int tier) {
            upgrades.put(id, tier);
        }
    }
}
