package dev.shinyepo.resourcegenerator.data.client;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class AccountUpgradeData {
    private static Map<ResourceLocation, Integer> upgrades = new HashMap<>();

    public static Map<ResourceLocation, Integer> get() {
        return upgrades;
    }

    public static void set(Map<ResourceLocation, Integer> upgrades) {
        AccountUpgradeData.upgrades = upgrades;
    }
}
