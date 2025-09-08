package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.menus.controller.ControllerContainer;
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
}
