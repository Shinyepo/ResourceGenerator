package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.data.market.MarketOffer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

public class MarketOfferRegistry {
    public static final ResourceKey<Registry<MarketOffer>> MARKET_OFFER_REGISTRY_KEY = ResourceKey.createRegistryKey(fromNamespaceAndPath(ResourceGenerator.MODID, "market_offers"));
    public static final Registry<MarketOffer> MARKET_OFFER_REGISTRY = new RegistryBuilder<>(MARKET_OFFER_REGISTRY_KEY)
            .sync(true)
            .create();

    public static final DeferredRegister<MarketOffer> OFFERS = DeferredRegister.create(MARKET_OFFER_REGISTRY, ResourceGenerator.MODID);

    private static final Supplier<MarketOffer> IRON_ORE = register(new MarketOffer.Builder()
            .setItem(Items.IRON_ORE)
            .setBasePrice(20L)
            .setMinMultiplier(1F)
            .setMaxMultiplier(1F)
            .build());

    public static DeferredHolder<MarketOffer, MarketOffer> register(MarketOffer offer) {
        return OFFERS.register(getIdentifier(offer.getItem()), () -> offer);
    }

    public static List<MarketOffer> getMarketOffers() {
        return MARKET_OFFER_REGISTRY.stream().toList();
    }

    public static MarketOffer getMarketOffer(Item item) {
        Identifier itemId = fromNamespaceAndPath(ResourceGenerator.MODID, getIdentifier(item));
        return MARKET_OFFER_REGISTRY.getValue(itemId);
    }

    private static String getIdentifier(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }
}
