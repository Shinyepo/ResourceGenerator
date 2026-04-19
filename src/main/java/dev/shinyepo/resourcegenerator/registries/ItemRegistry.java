package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.items.IdCard;
import dev.shinyepo.resourcegenerator.items.Inspector;
import dev.shinyepo.resourcegenerator.items.PipeWrench;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ResourceGenerator.MODID);

    public static final DeferredItem<BlockItem> CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem("controller", BlockRegistry.CONTROLLER);
    public static final DeferredItem<BlockItem> SOLAR_ITEM = ITEMS.registerSimpleBlockItem("solar_panel", BlockRegistry.SOLAR_PANEL);
    public static final DeferredItem<BlockItem> WATER_ABSORBER_ITEM = ITEMS.registerSimpleBlockItem("water_absorber", BlockRegistry.WATER_ABSORBER);
    public static final DeferredItem<BlockItem> CABLE_ITEM = ITEMS.registerSimpleBlockItem("cable", BlockRegistry.CABLE);
    public static final DeferredItem<BlockItem> ITEM_PIPE_ITEM = ITEMS.registerSimpleBlockItem("item_pipe", BlockRegistry.ITEM_PIPE);
    public static final DeferredItem<BlockItem> BASIC_CONSUMER_ITEM = ITEMS.registerSimpleBlockItem("basic_consumer", BlockRegistry.BASIC_CONSUMER);
    public static final DeferredItem<Item> ID_CARD = ITEMS.registerItem("id_card", IdCard::new, () -> new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> INSPECTOR = ITEMS.registerItem("inspector", Inspector::new, () -> new Item.Properties().stacksTo(1));
    public static final DeferredItem<BlockItem> OUTPUT_UPGRADE_ITEM = ITEMS.registerSimpleBlockItem("output_upgrade", BlockRegistry.OUTPUT_UPGRADE);
    public static final DeferredItem<BlockItem> RESOURCE_IMITATOR_ITEM = ITEMS.registerSimpleBlockItem("resource_imitator", BlockRegistry.RESOURCE_IMITATOR);
    public static final DeferredItem<BlockItem> SCULK_ABSORBER_ITEM = ITEMS.registerSimpleBlockItem("sculk_absorber", BlockRegistry.SCULK_ABSORBER);
    public static final DeferredItem<BlockItem> CONSUMER_OUTPUT_ITEM = ITEMS.registerSimpleBlockItem("consumer_output", BlockRegistry.CONSUMER_OUTPUT);
    public static final DeferredItem<BlockItem> CONDUIT_ABSORBER_ITEM = ITEMS.registerSimpleBlockItem("conduit_absorber", BlockRegistry.CONDUIT_ABSORBER);
    public static final DeferredItem<BlockItem> MARKET_ITEM = ITEMS.registerSimpleBlockItem("market", BlockRegistry.MARKET);
    public static final DeferredItem<Item> PIPE_WRENCH = ITEMS.registerItem("pipe_wrench", PipeWrench::new, () -> new Item.Properties().stacksTo(1));
}
