package dev.shinyepo.resourcegenerator.blocks.entities.types;

import dev.shinyepo.resourcegenerator.controllers.AccountController;
import dev.shinyepo.resourcegenerator.controllers.DeviceNetworkController;
import dev.shinyepo.resourcegenerator.data.patterns.Pattern;
import dev.shinyepo.resourcegenerator.data.pricing.ResourcePriceDefinition;
import dev.shinyepo.resourcegenerator.data.sync.entity.ConsumerEntitySyncData;
import dev.shinyepo.resourcegenerator.networking.CustomMessages;
import dev.shinyepo.resourcegenerator.networking.packets.SyncConsumerEntityDataS2TCC;
import dev.shinyepo.resourcegenerator.properties.CustomProperties;
import dev.shinyepo.resourcegenerator.registries.PriceDefinitionRegistry;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import dev.shinyepo.resourcegenerator.util.ItemStacksHandlerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import static dev.shinyepo.resourcegenerator.datagen.patterns.CustomPatternProvider.*;

public class Consumer extends NetworkDeviceEntity implements IVerboseDataEntity {
    protected ConsumerEntitySyncData syncData = new ConsumerEntitySyncData(this::syncDataToAllClients);
    protected ItemStack product = new ItemStack(Items.IRON_INGOT);
    protected long price = 0;
    private final ItemStacksResourceHandler outputHandler;
    protected boolean isPatternValid = false;
    protected boolean isProductValid = false;
    protected Pattern pattern;


    public Consumer(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        outputHandler = ItemStacksHandlerUtil.createOutputOnlyHandler(1, this::setChanged);
    }

    @Override
    public void tick(ServerLevel level) {
        if (level.getGameTime() % 20 == 0) {
            syncData.flushSync();

            if (!isProductValid || !isPatternValid || !canProduce()) return;
            if (networkCapability.getNetworkId() == null) return;
            startProduction();
        }
    }

    private void verifyPattern(ServerLevel level) {
        pattern.verifyPattern(level, getBlockPos(), this::invalidatePattern, this::validatePattern);
    }

    private void invalidatePattern() {
        if (isPatternValid) {
            isPatternValid = false;
            isProductValid = false;
            syncData.setPatternValid(isPatternValid);
            syncData.setProductValid(isProductValid);
            syncData.setProduct(ItemStack.EMPTY);
            syncData.setPrice(0L);
            pattern.clearUpgrades();

            level.setBlock(getBlockPos(), getBlockState().setValue(CustomProperties.OPERATIONAL, false), Block.UPDATE_ALL);
        }
    }


    private void validatePattern(Item resource) {
        if (isPatternValid) return;
        ItemStack resourceStack = new ItemStack(resource);
        if (!resourceStack.isEmpty()) {
            if (resourceStack.is(TagRegistry.CONSUMER_RESOURCES)) {
                ResourcePriceDefinition priceData = PriceDefinitionRegistry.getPriceData(resource);
                if (priceData != null) {
                    price = priceData.getPrice(pattern.getUpgrades());
                    syncData.setPrice(price);
                    isProductValid = true;
                }
            } else {
                isProductValid = false;
            }
        } else {
            isProductValid = false;
        }
        product = resourceStack;
        syncData.setProduct(product);

        isPatternValid = true;
        syncData.setPatternValid(isPatternValid);
        syncData.setProductValid(isProductValid);

        level.setBlock(getBlockPos(), getBlockState().setValue(CustomProperties.OPERATIONAL, true), Block.UPDATE_ALL);
    }

    public void forceVerifyPattern() {
        assert level != null;
        verifyPattern((ServerLevel) level);
    }

    public void shouldReVerifyPattern(int tier) {
        if (pattern.getTier() == tier) {
            forceVerifyPattern();
        }
    }

    private boolean canProduce() {
        return outputHandler.getAmountAsInt(0) < 64 && (outputHandler.getResource(0).isEmpty() || outputHandler.getResource(0).is(product.getItem()));
    }

    private void startProduction() {
        assert level != null;

        DeviceNetworkController controller = DeviceNetworkController.getInstance((ServerLevel) level);
        BlockPos receiverPos = controller.getReceiverFromNetwork(networkCapability.getNetworkId());

        if (receiverPos == null) return;
        if (level.getBlockEntity(receiverPos) instanceof Receiver receiver) {

            UUID accountId = receiver.getAccountId();
            AccountController accController = AccountController.getInstance((ServerLevel) level);

            long balance = accController.getAccountBalance(accountId);
            if (balance < price) return;
            accController.changeAccountBalance(accountId, -price);
            generateProduct();
        }
    }

    private void generateProduct() {
        var alreadyInSlot = outputHandler.getResource(0);
        if (alreadyInSlot.isEmpty() || alreadyInSlot.is(product.getItem())) {
            if (outputHandler.getAmountAsInt(0) > 64) return;
            var toInput = Math.min(64, outputHandler.getAmountAsInt(0) + 1);
            outputHandler.set(0, ItemResource.of(product), toInput);
        }
    }

    // GUI STUFF
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
        syncData.setPatternTier(pattern.getTier());
        verifyPattern((ServerLevel) level);
        syncData.flushSync();
    }

    public void setSyncData(ConsumerEntitySyncData syncData) {
        this.syncData = syncData;
    }

    public ItemStacksResourceHandler getOutputHandler() {
        return outputHandler;
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

    public void syncDataToClient(ServerPlayer player) {
        if (this.level == null || this.level.isClientSide()) return;
        CustomMessages.sendToPlayer(new SyncConsumerEntityDataS2TCC(worldPosition, syncData), player);
    }

    //TODO: Figure out why PacketDistributor#sendToPlayersTrackingChunk doesnt work
    public void syncDataToAllClients() {
        if (this.level == null || this.level.isClientSide()) return;
        CustomMessages.sendToAllPlayers(new SyncConsumerEntityDataS2TCC(worldPosition, syncData));
    }

    public ConsumerEntitySyncData getSyncData() {
        return syncData;
    }

    public ResourceHandler<ItemResource> getItemCapability(@Nullable Direction direction) {
        if (direction == Direction.DOWN) return outputHandler;
        return null;
    }
}
