package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.blocks.entities.types.NetworkDeviceEntity;
import dev.shinyepo.resourcegenerator.capabilities.INetworkCapability;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CapabilityRegistry {
    public static final BlockCapability<INetworkCapability, Direction> NETWORK_CAPABILITY = BlockCapability.create(
            ResourceLocation.fromNamespaceAndPath(ResourceGenerator.MODID, "network_capability"),
            INetworkCapability.class,
            Direction.class
    );

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(NETWORK_CAPABILITY, BlockEntityRegistry.CONTROLLER_ENTITY.get(), NetworkDeviceEntity::getNetworkCapability);
        event.registerBlockEntity(NETWORK_CAPABILITY, BlockEntityRegistry.PIPE_ENTITY.get(), NetworkDeviceEntity::getNetworkCapability);
        event.registerBlockEntity(NETWORK_CAPABILITY, BlockEntityRegistry.SOLAR_ENTITY.get(), NetworkDeviceEntity::getNetworkCapability);
        event.registerBlockEntity(NETWORK_CAPABILITY, BlockEntityRegistry.WATER_ABSORBER_ENTITY.get(), NetworkDeviceEntity::getNetworkCapability);
    }
}
