package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.blocks.entities.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ResourceGenerator.MODID);

    public static final Supplier<BlockEntityType<DummyExtensionEntity>> DUMMY_ENTITY = ENTITIES.register("dummy_entity",
            () -> new BlockEntityType<>(DummyExtensionEntity::new, BlockRegistry.DUMMY_EXTENSION.get()));

    public static final Supplier<BlockEntityType<ControllerEntity>> CONTROLLER_ENTITY = ENTITIES.register("controller_entity",
            () -> new BlockEntityType<>(ControllerEntity::new, BlockRegistry.CONTROLLER.get()));

    public static final Supplier<BlockEntityType<SolarPanelEntity>> SOLAR_ENTITY = ENTITIES.register("solar_entity",
            () -> new BlockEntityType<>(SolarPanelEntity::new, BlockRegistry.SOLAR_PANEL.get()));

    public static final Supplier<BlockEntityType<WaterAbsorberEntity>> WATER_ABSORBER_ENTITY = ENTITIES.register("water_absorber",
            () -> new BlockEntityType<>(WaterAbsorberEntity::new, BlockRegistry.WATER_ABSORBER.get()));

    public static final Supplier<BlockEntityType<PipeEntity>> PIPE_ENTITY = ENTITIES.register("pipe_entity",
            () -> new BlockEntityType<>(PipeEntity::new, BlockRegistry.PIPE.get()));

    public static final Supplier<BlockEntityType<BasicConsumerEntity>> BASIC_CONSUMER_ENTITY = ENTITIES.register("basic_consumer_entity",
            () -> new BlockEntityType<>(BasicConsumerEntity::new, BlockRegistry.BASIC_CONSUMER.get()));

    public static final Supplier<BlockEntityType<OutputUpgradeEntity>> OUTPUT_UPGRADE_ENTITY = ENTITIES.register("output_upgrade_entity",
            () -> new BlockEntityType<>(OutputUpgradeEntity::new, BlockRegistry.OUTPUT_UPGRADE.get()));

    public static final Supplier<BlockEntityType<ResourceImitatorEntity>> RESOURCE_IMITATOR_ENTITY = ENTITIES.register("resource_imitator_entity",
            () -> new BlockEntityType<>(ResourceImitatorEntity::new, BlockRegistry.RESOURCE_IMITATOR.get()));

    public static final Supplier<BlockEntityType<SculkAbsorberEntity>> SCULK_ABSORBER_ENTITY = ENTITIES.register("sculk_absorber_entity",
            () -> new BlockEntityType<>(SculkAbsorberEntity::new, BlockRegistry.SCULK_ABSORBER.get()));

    public static final Supplier<BlockEntityType<ConsumerOutputEntity>> CONSUMER_OUTPUT_ENTITY = ENTITIES.register("consumer_output_entity",
            () -> new BlockEntityType<>(ConsumerOutputEntity::new, BlockRegistry.CONSUMER_OUTPUT.get()));

    public static final Supplier<BlockEntityType<ConduitAbsorberEntity>> CONDUIT_ABSORBER_ENTITY = ENTITIES.register("conduit_absorber_entity",
            () -> new BlockEntityType<>(ConduitAbsorberEntity::new, BlockRegistry.CONDUIT_ABSORBER.get()));

}
