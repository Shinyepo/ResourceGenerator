package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.items.IdCard;
import dev.shinyepo.resourcegenerator.items.Inspector;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ResourceGenerator.MODID);

    public static final DeferredItem<BlockItem> CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem("controller", BlockRegistry.CONTROLLER);
    public static final DeferredItem<BlockItem> SOLAR_ITEM = ITEMS.registerSimpleBlockItem("solar_panel", BlockRegistry.SOLAR_PANEL);
    public static final DeferredItem<BlockItem> WATER_ABSORBER_ITEM = ITEMS.registerSimpleBlockItem("water_absorber", BlockRegistry.WATER_ABSORBER);
    public static final DeferredItem<BlockItem> PIPE_ITEM = ITEMS.registerSimpleBlockItem("pipe", BlockRegistry.PIPE);
    public static final DeferredItem<Item> ID_CARD = ITEMS.registerItem("id_card", IdCard::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> INSPECTOR = ITEMS.registerItem("inspector", Inspector::new, new Item.Properties().stacksTo(1));
}
