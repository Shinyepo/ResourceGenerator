package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.blocks.*;
import dev.shinyepo.resourcegenerator.properties.CustomProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ResourceGenerator.MODID);

    //Dummy block for multi-blocks
    public static final DeferredBlock<Block> DUMMY_EXTENSION = BLOCKS.register("dummy_extension", DummyExtension::new);

    public static final DeferredBlock<Block> CONTROLLER = BLOCKS.registerBlock("controller", Controller::new, BlockBehaviour.Properties::of);
    public static final DeferredBlock<Block> SOLAR_PANEL = BLOCKS.registerBlock("solar_panel", SolarPanel::new, BlockBehaviour.Properties::of);
    public static final DeferredBlock<Block> WATER_ABSORBER = BLOCKS.registerBlock("water_absorber", WaterAbsorber::new, BlockBehaviour.Properties::of);
    public static final DeferredBlock<Block> PIPE = BLOCKS.registerBlock("pipe", Pipe::new, BlockBehaviour.Properties::of);


    public static final DeferredBlock<Block> BASIC_CONSUMER = BLOCKS.registerBlock("basic_consumer", BasicConsumer::new, BlockBehaviour.Properties::of);

    public static final DeferredBlock<Block> OUTPUT_UPGRADE = BLOCKS.registerBlock("output_upgrade", OutputUpgradeBlock::new, BlockBehaviour.Properties::of);
    public static final DeferredBlock<Block> RESOURCE_IMITATOR = BLOCKS.registerBlock("resource_imitator", ResourceImitator::new, BlockBehaviour.Properties::of);
    public static final DeferredBlock<Block> CONSUMER_OUTPUT = BLOCKS.registerBlock("consumer_output", ConsumerOutput::new, BlockBehaviour.Properties::of);
    public static final DeferredBlock<Block> SCULK_ABSORBER = BLOCKS.registerBlock("sculk_absorber", SculkAbsorber::new, BlockBehaviour.Properties::of);
    public static final DeferredBlock<Block> CONDUIT_ABSORBER = BLOCKS.registerBlock("conduit_absorber", ConduitAbsorber::new, () ->
            BlockBehaviour.Properties.of().lightLevel(state -> state.getValue(CustomProperties.OPERATIONAL) ? 15 : 0));
}
