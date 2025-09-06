package dev.shinyepo.resourcegenerator;

import com.mojang.logging.LogUtils;
import dev.shinyepo.resourcegenerator.network.NetworkController;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;

import static dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry.ENTITIES;
import static dev.shinyepo.resourcegenerator.registries.BlockRegistry.BLOCKS;
import static dev.shinyepo.resourcegenerator.registries.CreativeTabRegistry.CREATIVE_TABS;
import static dev.shinyepo.resourcegenerator.registries.ItemRegistry.ITEMS;

@Mod(ResourceGenerator.MODID)
public class ResourceGenerator {
    public static final String MODID = "resourcegenerator";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ResourceGenerator(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        ENTITIES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        NetworkController.init();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        NetworkController.safeShutdown();
    }
}
