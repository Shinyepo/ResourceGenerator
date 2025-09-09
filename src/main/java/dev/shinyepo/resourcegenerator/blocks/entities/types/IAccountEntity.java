package dev.shinyepo.resourcegenerator.blocks.entities.types;

import java.util.UUID;

public interface IAccountEntity {
    UUID getAccountId();

    void setAccountId(UUID accountId);

    String getOwnerName();

    void setOwnerName(String ownerName);
}
