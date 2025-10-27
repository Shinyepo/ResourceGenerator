package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.IDataEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.Receiver;
import dev.shinyepo.resourcegenerator.controllers.AccountController;
import dev.shinyepo.resourcegenerator.datacomponents.IdCardData;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import dev.shinyepo.resourcegenerator.registries.DataComponentRegistry;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import dev.shinyepo.resourcegenerator.registries.UpgradeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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
import java.util.Map;
import java.util.UUID;

public class ControllerEntity extends Receiver implements IDataEntity {
    private final ItemStackHandler cardHandler;
    public static List<TagKey<Item>> validInputs = List.of(TagRegistry.ID_CARDS);

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
        configureSides(Direction.DOWN, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH);
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
        if (cardData != null && cardData.userId() != null && getAccountId() == null) {
            ServerLevel serverLevel = (ServerLevel) level;
            assert serverLevel != null;
            AccountController accountController = AccountController.getInstance(serverLevel);
            this.setAccountId(accountController.getOrCreateAccount(cardData.userId()));
            this.ownerName = cardData.username();
            setChanged();
        }
    }

    public Map<ResourceLocation, Integer> getUpgrades() {
        UUID accountId = getAccountId();
        if (getAccountId() != null) {
            ServerLevel serverLevel = (ServerLevel) level;
            AccountController accountController = AccountController.getInstance(serverLevel);
            return accountController.getUpgrades(accountId);
        }
        return null;
    }

    @Override
    public void tick(ServerLevel level) {
        super.tick(level);
        if (level.getGameTime() % 5 != 0) return;
        if (!cardHandler.getStackInSlot(0).isEmpty()) assignAccount();
        if (getAccountId() != null) {
            AccountController controller = AccountController.getInstance(level);
            var upgrades = controller.getUpgrades(getAccountId());
            if (upgrades != null && upgrades.isEmpty())
                AccountController.getInstance(level).buyUpgrade(getAccountId(), UpgradeRegistry.MAX_ABSORBERS.get().id(), 2);
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
