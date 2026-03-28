package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.patterns.Pattern;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public class DataPackRegistry {
    public static final ResourceKey<Registry<Pattern>> PATTERN_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "patterns"));

    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                // The registry key.
                PATTERN_REGISTRY_KEY,
                // The codec of the registry contents.
                Pattern.CODEC,
                // The network codec of the registry contents. Often identical to the normal codec.
                // May be a reduced variant of the normal codec that omits data that is not needed on the client.
                // May be null. If null, registry entries will not be synced to the client at all.
                // May be omitted, which is functionally identical to passing null (a method overload
                // with two parameters is called that passes null to the normal three parameter method).
                null
                // A consumer which configures the constructed registry via the RegistryBuilder.
                // May be omitted, which is functionally identical to passing builder -> {}.
        );
    }
}
