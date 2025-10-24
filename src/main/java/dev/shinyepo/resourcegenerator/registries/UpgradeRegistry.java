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

    public static final Supplier<Upgrade> EXAMPLE_UPGRADE = UPGRADES.register("example_upgrade",
            () -> new Upgrade.Builder()
                    .setId("example_upgrade")
                    .setBaseBonus(1)
                    .setBaseCost(100)
                    .setCostMultiplier(5)
                    .setBonusMultiplier(1)
                    .build());
}
