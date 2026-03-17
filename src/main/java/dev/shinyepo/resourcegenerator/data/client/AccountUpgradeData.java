package dev.shinyepo.resourcegenerator.data.client;

import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class AccountUpgradeData {
    private static Map<Identifier, Integer> upgrades = new HashMap<>();

    public static Map<Identifier, Integer> get() {
        return upgrades;
    }

    public static void set(Map<Identifier, Integer> upgrades) {
        AccountUpgradeData.upgrades = upgrades;
    }
}
