package dev.shinyepo.resourcegenerator.registries.custom;

import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class SpawnerAbsorberRegistry {
    private static final Set<BlockPos> absorbers = new HashSet<>();

    public static void addAbsorber(BlockPos pos) {
        absorbers.add(pos);
    }

    public static void removeAbsorber(BlockPos pos) {
        absorbers.remove(pos);
    }

    public static Set<BlockPos> getAbsorbers() {
        return absorbers;
    }
}
