package dev.shinyepo.resourcegenerator.blocks.entities.types;

import com.mojang.serialization.Codec;
import dev.shinyepo.resourcegenerator.networking.CustomMessages;
import dev.shinyepo.resourcegenerator.networking.packets.SyncOwnerS2C;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

public class Receiver extends NetworkDeviceEntity implements IAccountEntity {
    private UUID accountId;
    protected String ownerName = "";

    public Receiver(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void tick() {

    }

    @Override
    public UUID getAccountId() {
        return accountId;
    }

    @Override
    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
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
