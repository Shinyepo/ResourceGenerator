package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ResourceGenerator.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RESOURCE_GENERATOR_TAB = CREATIVE_TABS.register("resource_generator_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.resourcegenerator"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ItemRegistry.CONTROLLER_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ItemRegistry.ID_CARD.get());
                output.accept(ItemRegistry.INSPECTOR.get());
                output.accept(ItemRegistry.CONTROLLER_ITEM.get());
                output.accept(ItemRegistry.SOLAR_ITEM.get());
                output.accept(ItemRegistry.WATER_ABSORBER_ITEM.get());
                output.accept(ItemRegistry.PIPE_ITEM.get());
            }).build());
}
