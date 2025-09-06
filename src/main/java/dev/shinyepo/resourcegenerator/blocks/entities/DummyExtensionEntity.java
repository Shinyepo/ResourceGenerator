package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class DummyExtensionEntity extends BlockEntity {
    private BlockPos mainPos = BlockPos.ZERO;

    public DummyExtensionEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.DUMMY_ENTITY.get(), pos, blockState);
    }

    public void setMainPos(BlockPos mainPos) {
        this.mainPos = mainPos;
        setChanged();
    }

    public BlockPos getMainPos() {
        return mainPos;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable("mainPos", BlockPos.CODEC, mainPos);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        mainPos = input.read("mainPos", BlockPos.CODEC).orElse(BlockPos.ZERO);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
        BlockPos mainPos = input.read("mainPos", BlockPos.CODEC).orElse(BlockPos.ZERO);
        if (mainPos != BlockPos.ZERO) {
            this.mainPos = mainPos;
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
