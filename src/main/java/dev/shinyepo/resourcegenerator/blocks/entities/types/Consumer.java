package dev.shinyepo.resourcegenerator.blocks.entities.types;

import dev.shinyepo.resourcegenerator.configs.ConsumerConfig;
import dev.shinyepo.resourcegenerator.controllers.AccountController;
import dev.shinyepo.resourcegenerator.controllers.DeviceNetworkController;
import dev.shinyepo.resourcegenerator.data.patterns.Pattern;
import dev.shinyepo.resourcegenerator.util.ItemStacksHandlerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.UUID;

import static dev.shinyepo.resourcegenerator.datagen.patterns.CustomPatternProvider.*;

public class Consumer extends NetworkDeviceEntity implements IDataEntity {
    protected ItemStack product = new ItemStack(Items.IRON_INGOT);
    protected long price = 0;
    private ConsumerConfig config;
    private final ItemStacksResourceHandler outputHandler;
    protected boolean patternValid = false;
    protected Pattern pattern;

    private final ContainerData dataSlot = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> pattern.tier;
                case 1 -> patternValid ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int pValue) {
            switch (index) {
                case 0 -> pattern.setTier(pValue);
                case 1 -> patternValid = pValue == 1;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };


    public Consumer(BlockEntityType<?> type, ConsumerConfig config, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.config = config;

        outputHandler = ItemStacksHandlerUtil.createOutputOnlyHandler(1, this::setChanged);
    }

    public ItemStacksResourceHandler getOutputHandler() {
        return outputHandler;
    }

    @Override
    public void tick(ServerLevel level) {
        if (level.getGameTime() % 20 == 0 && canProduce()) {
            if (networkCapability.getNetworkId() != null) {
                DeviceNetworkController controller = DeviceNetworkController.getInstance(level);
                BlockPos receiverPos = controller.getReceiverFromNetwork(networkCapability.getNetworkId());
                if (receiverPos == null) return;
                if (level.getBlockEntity(receiverPos) instanceof Receiver receiver) {
                    UUID accountId = receiver.getAccountId();
                    AccountController accController = AccountController.getInstance(level);
                    long balance = accController.getAccountBalance(accountId);
                    long result = accController.changeAccountBalance(accountId, -price);
                    if (result >= 0 && balance != result) {
                        generateProduct();
                    }
                }
            }
        }
    }

    public void cyclePattern() {
        if (level.isClientSide()) return;
        if (pattern == null) return;
        if (pattern.getTier() == 1) {
            pattern = level.registryAccess().get(TIER_2_PATTERN).get().value();
        } else if (pattern.getTier() == 2) {
            pattern = level.registryAccess().get(TIER_3_PATTERN).get().value();
        } else if (pattern.getTier() == 3) {
            pattern = level.registryAccess().get(TIER_1_PATTERN).get().value();
        }
        setChanged();
    }

    private boolean canProduce() {
        return outputHandler.getAmountAsInt(0) < 64 && (outputHandler.getResource(0).isEmpty() || outputHandler.getResource(0).is(product.getItem()));
    }

    private void generateProduct() {
        var alreadyInSlot = outputHandler.getResource(0);
        if (alreadyInSlot.isEmpty() || alreadyInSlot.is(product.getItem())) {
            if (outputHandler.getAmountAsInt(0) > 64) return;
            var toInput = Math.min(64, outputHandler.getAmountAsInt(0) + config.getProduces());
            outputHandler.set(0, ItemResource.of(product), toInput);
            setChanged();
        }
    }

    public ContainerData getDataSlot() {
        return dataSlot;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (outputHandler != null)
            outputHandler.serialize(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        if (outputHandler != null)
            outputHandler.deserialize(input);
    }
}
