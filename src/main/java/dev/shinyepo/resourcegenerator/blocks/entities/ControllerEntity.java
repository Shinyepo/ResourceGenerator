package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.IDataEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.Receiver;
import dev.shinyepo.resourcegenerator.controllers.AccountController;
import dev.shinyepo.resourcegenerator.datacomponents.IdCardData;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import dev.shinyepo.resourcegenerator.registries.DataComponentRegistry;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class ControllerEntity extends Receiver implements IDataEntity {
    private final ItemStackHandler cardHandler;
    public static List<TagKey<Item>> validInputs = List.of(TagRegistry.ID_CARDS);
    private Long value = 0L;


    private final DataSlot dataSlot = new DataSlot() {
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

    private void assignAccount() {
        ItemStack card = cardHandler.getStackInSlot(0);
        IdCardData cardData = card.get(DataComponentRegistry.ID_CARD.get());
        if (cardData != null && cardData.userId() != null && networkId == null) {
            ServerLevel serverLevel = (ServerLevel) level;
            assert serverLevel != null;
            AccountController accountController = AccountController.getInstance(serverLevel);
            this.setAccountId(accountController.getOrCreateAccount(cardData.userId()));
            this.ownerName = cardData.username();
            setChanged();
        }
    }

    public void tick() {
        assert level != null;
        if (level.isClientSide()) return;
        if (level.getGameTime() % 5 != 0) return;
        if (!cardHandler.getStackInSlot(0).isEmpty()) assignAccount();
        if (level.getGameTime() % 20 != 0) return;
        UUID accountId = getAccountId();
        if (accountId != null) {
            AccountController controller = AccountController.getInstance((ServerLevel) level);
            value = controller.changeAccountBalance(getAccountId(), 20L);
        }
    }

    public DataSlot getDataSlot() {
        return dataSlot;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        cardHandler.serialize(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        cardHandler.deserialize(input);
    }
}
