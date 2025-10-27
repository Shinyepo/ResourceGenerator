package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.networking.packets.BuyAccountUpgradeC2S;
import dev.shinyepo.resourcegenerator.networking.packets.SyncAccountUpgradesS2C;
import dev.shinyepo.resourcegenerator.networking.packets.SyncOwnerS2C;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketRegistry {
    public static void registerPayloadHandler(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(ResourceGenerator.MODID)
                .versioned("1.0")
                .optional();


        //TO CLIENT
        registrar.playToClient(SyncOwnerS2C.TYPE, SyncOwnerS2C.STREAM_CODEC, SyncOwnerS2C::handler);
        registrar.playToClient(SyncAccountUpgradesS2C.TYPE, SyncAccountUpgradesS2C.STREAM_CODEC, SyncAccountUpgradesS2C::handler);


        //TO SERVER
        registrar.playToServer(BuyAccountUpgradeC2S.TYPE, BuyAccountUpgradeC2S.STREAM_CODEC, BuyAccountUpgradeC2S::handler);
    }
}
