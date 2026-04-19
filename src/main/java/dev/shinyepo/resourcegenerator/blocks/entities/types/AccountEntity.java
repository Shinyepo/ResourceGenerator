package dev.shinyepo.resourcegenerator.blocks.entities.types;

import com.mojang.serialization.Codec;
import dev.shinyepo.resourcegenerator.controllers.AccountController;
import dev.shinyepo.resourcegenerator.data.NBTTags;
import dev.shinyepo.resourcegenerator.datacomponents.IdCardData;
import dev.shinyepo.resourcegenerator.networking.CustomMessages;
import dev.shinyepo.resourcegenerator.networking.packets.SyncOwnerS2C;
import dev.shinyepo.resourcegenerator.registries.DataComponentRegistry;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import dev.shinyepo.resourcegenerator.util.ItemStacksHandlerUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.List;
import java.util.UUID;

public class AccountEntity implements IAccountEntity {
    private final ItemStacksResourceHandler cardHandler = ItemStacksHandlerUtil.createInputItemHandler(NBTTags.CARD_HANDLER, 1, this::assignAccount, List.of(TagRegistry.ID_CARDS));
    private final BlockEntity blockEntity;

    private String ownerName = "";
    private UUID accountId;

    public AccountEntity(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public void assignAccount() {
        if (blockEntity.getLevel() == null || blockEntity.getLevel() instanceof ClientLevel) return;
        ItemResource card = cardHandler.getResource(0);
        if (!card.isEmpty()) {
            IdCardData cardData = card.get(DataComponentRegistry.ID_CARD.get());
            if (cardData != null && cardData.userId() != null && getAccountId() == null) {
                AccountController accountController = AccountController.getInstance((ServerLevel) blockEntity.getLevel());
                this.setAccountId(accountController.getOrCreateAccount(cardData.userId()));
                this.setOwnerName(cardData.username());
            }
        }
        blockEntity.setChanged();
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
        blockEntity.setChanged();
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        blockEntity.setChanged();
    }

    public ItemStacksResourceHandler getCardHandler() {
        return cardHandler;
    }

    public void onChange() {
        if (!ownerName.isEmpty())
            CustomMessages.sendToAllPlayers(new SyncOwnerS2C(ownerName, blockEntity.getBlockPos()));
    }

    public void serialize(ValueOutput output) {
        cardHandler.serialize(output);
        if (accountId != null) {
            output.store("accountId", UUIDUtil.CODEC, accountId);
        }
        if (!ownerName.isEmpty()) {
            output.store("ownerName", Codec.STRING, ownerName);
        }
    }

    public void deserialize(ValueInput input) {
        cardHandler.deserialize(input);
        accountId = input.read("accountId", UUIDUtil.CODEC).orElse(null);
        ownerName = input.read("ownerName", Codec.STRING).orElse("");
    }
}
