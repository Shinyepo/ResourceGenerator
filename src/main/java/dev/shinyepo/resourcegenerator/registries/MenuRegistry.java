package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.menus.consumer.ConsumerContainer;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
import dev.shinyepo.resourcegenerator.menus.market.MarketContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, ResourceGenerator.MODID);

    public static final Supplier<MenuType<ControllerContainer>> CONTROLLER_MENU = MENUS.register("controller",
            () -> IMenuTypeExtension.create(
                    (windowId, inv, data) -> new ControllerContainer(windowId, inv.player, data.readBlockPos())));

    public static final Supplier<MenuType<ConsumerContainer>> CONSUMER_MENU = MENUS.register("consumer",
            () -> IMenuTypeExtension.create(
                    (windowId, inv, data) -> new ConsumerContainer(windowId, inv.player, data.readBlockPos())));

    public static final Supplier<MenuType<MarketContainer>> MARKET_MENU = MENUS.register("market",
            () -> IMenuTypeExtension.create(
                    (windowId, inv, data) -> new MarketContainer(windowId, inv.player, data.readBlockPos())));
}
