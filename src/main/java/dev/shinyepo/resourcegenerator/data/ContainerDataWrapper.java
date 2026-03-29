package dev.shinyepo.resourcegenerator.data;

import net.minecraft.world.inventory.ContainerData;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class ContainerDataWrapper implements ContainerData {
    public record Entry(IntSupplier getter, IntConsumer setter) {
    }

    private final Entry[] entries;

    public ContainerDataWrapper(Entry... entries) {
        this.entries = entries;
    }


    @Override
    public int get(int idx) {
        if (indexOutsideBounds(idx)) return 0;
        return entries[idx].getter.getAsInt();
    }

    @Override
    public void set(int idx, int value) {
        if (indexOutsideBounds(idx)) return;
        entries[idx].setter.accept(value);
    }

    @Override
    public int getCount() {
        return entries.length;
    }

    private boolean indexOutsideBounds(int index) {
        return index < 0 || index >= entries.length;
    }
}
