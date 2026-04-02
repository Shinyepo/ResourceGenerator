package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class TagRegistry {
    public static final TagKey<Item> ID_CARDS = TagKey.create(Registries.ITEM, fromNamespaceAndPath("c", "id_cards"));
    public static final TagKey<Item> CONSUMER_RESOURCES = TagKey.create(Registries.ITEM, fromNamespaceAndPath("c", "consumer_resources"));

    //Consumer allowed blocks
    public static final TagKey<Block> UPGRADE_BLOCKS = TagKey.create(Registries.BLOCK, fromNamespaceAndPath(ResourceGenerator.MODID, "upgrade_blocks"));
    public static final TagKey<Block> CONSUMER_STRUCTURE_BLOCKS = TagKey.create(Registries.BLOCK, fromNamespaceAndPath(ResourceGenerator.MODID, "consumer_structure"));
    public static final TagKey<Block> RESOURCE_BLOCKS = TagKey.create(Registries.BLOCK, fromNamespaceAndPath(ResourceGenerator.MODID, "resource_blocks"));

}
