package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.blocks.entities.ControllerEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.DummyExtensionEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.PipeEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.SolarPanelEntity;
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

    public static final Supplier<BlockEntityType<PipeEntity>> PIPE_ENTITY = ENTITIES.register("pipe_entity",
            () -> new BlockEntityType<>(PipeEntity::new, BlockRegistry.PIPE.get()));

}
