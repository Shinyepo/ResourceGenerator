package dev.shinyepo.resourcegenerator;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ResourceGenerator.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ResourceGenerator.MODID, value = Dist.CLIENT)
public class ResourceGeneratorClient {
    public ResourceGeneratorClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
