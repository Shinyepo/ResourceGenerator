package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.AccountEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.IAccountEntity;
import dev.shinyepo.resourcegenerator.data.NBTTags;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import dev.shinyepo.resourcegenerator.util.ItemStacksHandlerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.Nullable;

public class MarketEntity extends BlockEntity implements IAccountEntity {
    private final AccountEntity accountEntity = new AccountEntity(this);
    private final ItemStacksResourceHandler outputHandler = ItemStacksHandlerUtil.createOutputOnlyHandler(NBTTags.OUTPUT_HANDLER, 1, this::setChanged);

    public MarketEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityRegistry.MARKET_ENTITY.get(), worldPosition, blockState);
    }

    public ItemStacksResourceHandler getCardHandler() {
        return accountEntity.getCardHandler();
    }

    public ItemStacksResourceHandler getOutputHandler() {
        return outputHandler;
    }

    public ResourceHandler<ItemResource> getItemCapability(@Nullable Direction direction) {
        return direction == Direction.DOWN ? outputHandler : null;
    }

    public String getOwnerName() {
        return accountEntity.getOwnerName();
    }

    @Override
    public void setOwnerName(String ownerName) {
        accountEntity.setOwnerName(ownerName);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        accountEntity.onChange();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        accountEntity.serialize(output);
        outputHandler.serialize(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        accountEntity.deserialize(input);
        outputHandler.deserialize(input);
    }
}
