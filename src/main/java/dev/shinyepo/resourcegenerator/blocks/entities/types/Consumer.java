package dev.shinyepo.resourcegenerator.blocks.entities.types;

import com.mojang.serialization.Codec;
import dev.shinyepo.resourcegenerator.configs.ConsumerConfig;
import dev.shinyepo.resourcegenerator.controllers.AccountController;
import dev.shinyepo.resourcegenerator.networking.CustomMessages;
import dev.shinyepo.resourcegenerator.networking.packets.SyncOwnerS2C;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Map;
import java.util.UUID;

public class Consumer extends NetworkDeviceEntity implements IAccountEntity {
    private UUID accountId;
    private String ownerName = "";
    protected ItemStack product = new ItemStack(Items.IRON_INGOT);
    private ConsumerConfig config;

    public Consumer(BlockEntityType<?> type, ConsumerConfig config, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.config = config;
    }

    @Override
    public void tick(ServerLevel level) {
        if (level.getGameTime() % 20 == 0) {

        }
    }

    @Override
    public UUID getAccountId() {
        return accountId;
    }

    @Override
    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
        if (!level.isClientSide())
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
    }

    @Override
    public String getOwnerName() {
        return "";
    }

    public Map<Identifier, Integer> getUpgrades() {
        if (accountId != null) {
            ServerLevel serverLevel = (ServerLevel) level;
            AccountController accountController = AccountController.getInstance(serverLevel);
            return accountController.getUpgrades(accountId);
        }
        return null;
    }

    @Override
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (!ownerName.isEmpty())
            CustomMessages.sendToAllPlayers(new SyncOwnerS2C(ownerName, this.getBlockPos()));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (accountId != null) {
            output.store("accountId", UUIDUtil.CODEC, accountId);
        }
        if (!"".equals(ownerName)) {
            output.store("ownerName", Codec.STRING, ownerName);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        accountId = input.read("accountId", UUIDUtil.CODEC).orElse(null);
        ownerName = input.read("ownerName", Codec.STRING).orElse("");
    }
}
