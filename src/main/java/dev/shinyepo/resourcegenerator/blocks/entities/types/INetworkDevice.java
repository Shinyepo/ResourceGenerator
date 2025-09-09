package dev.shinyepo.resourcegenerator.blocks.entities.types;

import java.util.UUID;

public interface INetworkDevice {
    UUID getNetworkId();

    void setNetworkId(UUID networkId);
}
