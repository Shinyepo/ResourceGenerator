package dev.shinyepo.resourcegenerator.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class TagRegistry {
    public static final TagKey<Item> ID_CARDS = TagKey.create(Registries.ITEM, fromNamespaceAndPath("c", "id_cards"));

}
