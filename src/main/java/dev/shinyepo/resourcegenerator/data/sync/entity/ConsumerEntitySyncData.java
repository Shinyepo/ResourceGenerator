package dev.shinyepo.resourcegenerator.data.sync.entity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class ConsumerEntitySyncData {
    private ItemStack product = ItemStack.EMPTY;
    private long price = 0;
    private int patternTier = 1;
    private boolean isPatternValid = false;
    private boolean isProductValid = false;

    private Runnable setChanged;
    private boolean isDirty = false;

    public static final StreamCodec<RegistryFriendlyByteBuf, ConsumerEntitySyncData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ConsumerEntitySyncData::isPatternValid,
            ByteBufCodecs.BOOL,
            ConsumerEntitySyncData::isProductValid,
            ByteBufCodecs.INT,
            ConsumerEntitySyncData::getPatternTier,
            ByteBufCodecs.LONG,
            ConsumerEntitySyncData::getPrice,
            ItemStack.OPTIONAL_STREAM_CODEC,
            ConsumerEntitySyncData::getProduct,
            ConsumerEntitySyncData::new);

    public Boolean isProductValid() {
        return isProductValid;
    }

    public ConsumerEntitySyncData(Runnable setChanged) {
        this.setChanged = setChanged;
    }

    private ConsumerEntitySyncData(boolean isPatternValid, boolean isProductValid, int patternTier, long price, ItemStack product) {
        this.isPatternValid = isPatternValid;
        this.isProductValid = isProductValid;
        this.patternTier = patternTier;
        this.price = price;
        this.product = product;
    }

    public ItemStack getProduct() {
        return product;
    }

    public void setProduct(ItemStack product) {
        this.product = product;
        isDirty = true;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
        isDirty = true;
    }

    public int getPatternTier() {
        return patternTier;
    }

    public void setPatternTier(int patternTier) {
        this.patternTier = patternTier;
        isDirty = true;
    }

    public boolean isPatternValid() {
        return isPatternValid;
    }

    public void setPatternValid(boolean patternValid) {
        isPatternValid = patternValid;
        isDirty = true;
    }

    public void flushSync() {
        if (isDirty) {
            isDirty = false;
            setChanged.run();
        }
    }
}
