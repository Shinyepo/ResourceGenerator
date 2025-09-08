package dev.shinyepo.resourcegenerator.blocks.entities;

import com.mojang.serialization.Codec;
import dev.shinyepo.resourcegenerator.datacomponents.IdCardData;
import dev.shinyepo.resourcegenerator.networking.CustomMessages;
import dev.shinyepo.resourcegenerator.networking.packets.SyncOwnerS2C;
import dev.shinyepo.resourcegenerator.persistence.NetworkSavedData;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import dev.shinyepo.resourcegenerator.registries.DataComponentRegistry;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class ControllerEntity extends BlockEntity {
    private final ItemStackHandler cardHandler;
    public static List<TagKey<Item>> validInputs = List.of(TagRegistry.ID_CARDS);
    private String ownerName = "";
    private UUID networkId;
    private Long value = 0L;


    private DataSlot dataSlot = new DataSlot() {
        @Override
        public int get() {
            return Math.toIntExact(value);
        }

        @Override
        public void set(int i) {
            value = (long) i;
        }
    };

    public ControllerEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.CONTROLLER_ENTITY.get(), pos, blockState);
        cardHandler = createInputItemHandler(1);
    }

    @Nonnull
    public ItemStackHandler createInputItemHandler(int slots) {
        return new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if (!validInputs.isEmpty()) {
                    return stack.getTags().anyMatch((tag) -> validInputs.contains(tag));
                }
                return true;
            }
        };
    }

    public IItemHandler getCardHandler() {
        return cardHandler;
    }

    private void assignNetwork() {
        ItemStack card = cardHandler.getStackInSlot(0);
        IdCardData cardData = card.get(DataComponentRegistry.ID_CARD.get());
        if (cardData != null && cardData.userId() != null && networkId == null) {
            ServerLevel serverLevel = (ServerLevel) level;
            assert serverLevel != null;
            NetworkSavedData savedData = NetworkSavedData.getOrCreate(serverLevel);
            this.networkId = savedData.getOrCreateNetwork(cardData.userId());
            this.ownerName = cardData.username();
            setChanged();
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (!ownerName.isEmpty())
            CustomMessages.sendToAllPlayers(new SyncOwnerS2C(ownerName, this.getBlockPos()));
    }

    public void tick() {
        assert level != null;
        if (level.isClientSide()) return;
        if (level.getGameTime() % 5 != 0) return;
        if (!cardHandler.getStackInSlot(0).isEmpty()) assignNetwork();
        if (level.getGameTime() % 20 != 0) return;
        if (networkId != null) {
            NetworkSavedData savedData = NetworkSavedData.getOrCreate((ServerLevel) level);
            value = savedData.changeNetworksValue(networkId, 20L);
        }
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwner(String ownerName) {
        this.ownerName = ownerName;

    }

    public DataSlot getDataSlot() {
        return dataSlot;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        cardHandler.serialize(output);
        if (networkId != null) {
            output.store("networkId", UUIDUtil.CODEC, networkId);
        }
        output.store("ownerName", Codec.STRING, ownerName);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        cardHandler.deserialize(input);
        networkId = input.read("networkId", UUIDUtil.CODEC).orElse(null);
        ownerName = input.read("ownerName", Codec.STRING).orElse("");
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
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
}
