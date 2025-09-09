package dev.shinyepo.resourcegenerator.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shinyepo.resourcegenerator.data.Account;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountSavedData extends SavedData {
    private Map<UUID, Account> accounts = new HashMap<>();
    public static final SavedDataType<AccountSavedData> TYPE = new SavedDataType<>(
            "resource_generator_networks",
            AccountSavedData::new,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.unboundedMap(UUIDUtil.STRING_CODEC, Account.CODEC).fieldOf("accounts").forGetter(AccountSavedData::getAccounts)
            ).apply(instance, AccountSavedData::new))
    );


    private AccountSavedData() {

    }

    public AccountSavedData(Map<UUID, Account> accounts) {
        this.accounts = accounts;
    }

    public static AccountSavedData getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public Map<UUID, Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Map<UUID, Account> accounts) {
        this.accounts = accounts;
    }

    public Account getAccount(UUID accountId) {
        return this.accounts.get(accountId);
    }

    public UUID createAccount(UUID ownerId) {
        Account account = new Account(ownerId);
        accounts.put(account.getAccountId(), account);
        setDirty();
        return account.getAccountId();
    }

    public Account getAccountByOwner(UUID ownerId) {
        return accounts.values().stream().filter(acc -> acc.getOwnerId().compareTo(ownerId) == 0).findFirst().orElse(null);
    }
}
