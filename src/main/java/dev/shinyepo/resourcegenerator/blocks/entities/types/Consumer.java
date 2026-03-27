package dev.shinyepo.resourcegenerator.blocks.entities.types;

import dev.shinyepo.resourcegenerator.configs.ConsumerConfig;
import dev.shinyepo.resourcegenerator.controllers.AccountController;
import dev.shinyepo.resourcegenerator.controllers.DeviceNetworkController;
import dev.shinyepo.resourcegenerator.util.ItemStacksHandlerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.UUID;

public class Consumer extends NetworkDeviceEntity {
    protected ItemStack product = new ItemStack(Items.IRON_INGOT);
    private ConsumerConfig config;
    private final ItemStacksResourceHandler outputHandler;

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
                    long result = accController.changeAccountBalance(accountId, -1L);
                    if (result >= 0 && balance != result) {
                        generateProduct();
                    }
                }
            }
        }
    }

    private boolean canProduce() {
        return outputHandler.getAmountAsInt(0) < 64;
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
