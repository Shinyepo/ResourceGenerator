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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.ContainerData;
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

public class ControllerEntity extends Receiver implements IDataEntity {
    private final ItemStackHandler cardHandler;
    public static List<TagKey<Item>> validInputs = List.of(TagRegistry.ID_CARDS);

    private final ContainerData dataSlot = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> Math.toIntExact(value);
                case 1 -> Math.toIntExact(value - prevValue);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int pValue) {
            switch (index) {
                case 0 -> value = (long) pValue;
                case 1 -> prevValue = (long) pValue;
            }
        }

        @Override
        public int getCount() {
            return 2;
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

    public ContainerData getDataSlot() {
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
