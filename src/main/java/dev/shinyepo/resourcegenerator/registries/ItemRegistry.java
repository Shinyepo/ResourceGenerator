package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ResourceGenerator.MODID);

    public static final DeferredItem<BlockItem> CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem("controller", BlockRegistry.CONTROLLER);
    public static final DeferredItem<BlockItem> SOLAR_ITEM = ITEMS.registerSimpleBlockItem("solar_panel", BlockRegistry.SOLAR_PANEL);
    public static final DeferredItem<BlockItem> PIPE_ITEM = ITEMS.registerSimpleBlockItem("pipe", BlockRegistry.PIPE);
}
