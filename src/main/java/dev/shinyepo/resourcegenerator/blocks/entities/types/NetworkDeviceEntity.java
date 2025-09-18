package dev.shinyepo.resourcegenerator.blocks.entities.types;

import dev.shinyepo.resourcegenerator.capabilities.INetworkCapability;
import dev.shinyepo.resourcegenerator.capabilities.NetworkCapability;
import dev.shinyepo.resourcegenerator.configs.SideConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public abstract class NetworkDeviceEntity extends BlockEntity implements INetworkDevice {
    protected INetworkCapability networkCapability = new NetworkCapability() {
        @Override
        public void markDirty() {
            super.markDirty();
            setChanged();
        }
    };

    public NetworkDeviceEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public abstract void tick(ServerLevel level);

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        networkCapability.setNetworkId(input.read("networkId", UUIDUtil.CODEC).orElse(null));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (networkCapability.getNetworkId() != null) {
            output.store("networkId", UUIDUtil.CODEC, networkCapability.getNetworkId());
        }
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

    protected void configureSides(Direction... directions) {
        for (Direction direction : directions) {
            networkCapability.configureSide(direction);
        }
    }

    public @Nullable INetworkCapability getNetworkCapability(Direction direction) {
        if (direction == null) return networkCapability;
        if (networkCapability.isSideValid(direction) == SideConfig.NETWORK) return networkCapability;
        return null;
    }
}
