package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.Upgrade;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class UpgradeRegistry {
    public static final ResourceKey<Registry<Upgrade>> UPGRADE_REGISTRY_KEY = ResourceKey.createRegistryKey(fromNamespaceAndPath(ResourceGenerator.MODID, "upgrades"));
    public static final Registry<Upgrade> UPGRADE_REGISTRY = new RegistryBuilder<>(UPGRADE_REGISTRY_KEY)
            .sync(true)
            .create();

    public static final DeferredRegister<Upgrade> UPGRADES = DeferredRegister.create(UPGRADE_REGISTRY, ResourceGenerator.MODID);

    public static final Supplier<Upgrade> MAX_ABSORBERS = UPGRADES.register("max_absorbers",
            () -> new Upgrade.Builder()
                    .setId("max_absorbers")
                    .setMaxTier(5)
                    .setBaseBonus(1)
                    .setBaseCost(500)
                    .setCostMultiplier(5)
                    .setBonusMultiplier(1)
                    .build());

    public static final Supplier<Upgrade> ABSORPTION_AMOUNT = UPGRADES.register("absorption_amount",
            () -> new Upgrade.Builder()
                    .setId("absorption_amount")
                    .setMaxTier(8)
                    .setBaseBonus(1)
                    .setBaseCost(100)
                    .setCostMultiplier(5)
                    .setBonusMultiplier(2)
                    .build());

    public static final Supplier<Upgrade> ABSORPTION_SPEED = UPGRADES.register("absorption_speed",
            () -> new Upgrade.Builder()
                    .setId("absorption_speed")
                    .setMaxTier(8)
                    .setBaseBonus(1)
                    .setBaseCost(500000)
                    .setCostMultiplier(5)
                    .setBonusMultiplier(3)
                    .build());


    //TEST
    public static final Supplier<Upgrade> ABSORPTION_SPEED1 = UPGRADES.register("absorption_speed1",
            () -> new Upgrade.Builder()
                    .setId("absorption_speed1")
                    .setMaxTier(8)
                    .setBaseBonus(1)
                    .setBaseCost(100)
                    .setCostMultiplier(5)
                    .setBonusMultiplier(1)
                    .build());
    public static final Supplier<Upgrade> ABSORPTION_SPEED2 = UPGRADES.register("absorption_speed2",
            () -> new Upgrade.Builder()
                    .setId("absorption_speed2")
                    .setMaxTier(8)
                    .setBaseBonus(1)
                    .setBaseCost(100)
                    .setCostMultiplier(5)
                    .setBonusMultiplier(1)
                    .build());
    public static final Supplier<Upgrade> ABSORPTION_SPEED3 = UPGRADES.register("absorption_speed3",
            () -> new Upgrade.Builder()
                    .setId("absorption_speed3")
                    .setMaxTier(8)
                    .setBaseBonus(1)
                    .setBaseCost(100)
                    .setCostMultiplier(5)
                    .setBonusMultiplier(1)
                    .build());
    public static final Supplier<Upgrade> ABSORPTION_SPEED4 = UPGRADES.register("absorption_speed4",
            () -> new Upgrade.Builder()
                    .setId("absorption_speed4")
                    .setMaxTier(8)
                    .setBaseBonus(1)
                    .setBaseCost(100)
                    .setCostMultiplier(5)
                    .setBonusMultiplier(1)
                    .build());
    public static final Supplier<Upgrade> ABSORPTION_SPEED5 = UPGRADES.register("absorption_speed5",
            () -> new Upgrade.Builder()
                    .setId("absorption_speed5")
                    .setMaxTier(8)
                    .setBaseBonus(1)
                    .setBaseCost(100)
                    .setCostMultiplier(5)
                    .setBonusMultiplier(1)
                    .build());
    public static final Supplier<Upgrade> ABSORPTION_SPEED6 = UPGRADES.register("absorption_speed6",
            () -> new Upgrade.Builder()
                    .setId("absorption_speed6")
                    .setMaxTier(8)
                    .setBaseBonus(1)
                    .setBaseCost(100)
                    .setCostMultiplier(5)
                    .setBonusMultiplier(1)
                    .build());


}
