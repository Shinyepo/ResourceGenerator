package dev.shinyepo.resourcegenerator.blocks.entities.types;

import dev.shinyepo.resourcegenerator.controllers.AccountController;
import dev.shinyepo.resourcegenerator.controllers.DeviceNetworkController;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Map;
import java.util.UUID;

public class Receiver extends NetworkDeviceEntity implements IAccountEntity {
    protected final AccountEntity accountEntity = new AccountEntity(this);
    protected Long value = 0L;
    protected Long prevValue = 0L;

    public Receiver(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void tick(ServerLevel level) {
        if (level.getGameTime() % 20 == 0) {
            if (getAccountId() != null && networkCapability.getNetworkId() != null) {
                DeviceNetworkController controller = DeviceNetworkController.getInstance(level);
                AccountController accountController = AccountController.getInstance(level);
                Long balance = controller.getNetworksBalance(networkCapability.getNetworkId());
                prevValue = value;
                accountController.addBalanceFromMachines(level, getAccountId(), balance);
                value = accountController.getAccountBalance(getAccountId());
                controller.resetNetworksBalance(networkCapability.getNetworkId());
            }
        }
    }

    public UUID getAccountId() {
        return accountEntity.getAccountId();
    }

    public String getOwnerName() {
        return accountEntity.getOwnerName();
    }

    public void setOwnerName(String ownerName) {
        accountEntity.setOwnerName(ownerName);
    }

    public Map<Identifier, Integer> getUpgrades() {
        if (getAccountId() != null) {
            ServerLevel serverLevel = (ServerLevel) level;
            AccountController accountController = AccountController.getInstance(serverLevel);
            return accountController.getUpgrades(getAccountId());
        }
        return null;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        accountEntity.onChange();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        accountEntity.serialize(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        accountEntity.deserialize(input);
    }
}
