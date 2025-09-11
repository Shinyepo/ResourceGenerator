package dev.shinyepo.resourcegenerator.capabilities;

import dev.shinyepo.resourcegenerator.configs.SideConfig;
import net.minecraft.core.Direction;

import java.util.UUID;

public interface INetworkCapability {
    UUID getNetworkId();

    void setNetworkId(UUID networkId);

    SideConfig isSideValid(Direction direction);

    SideConfig[] getSides();

    void configureSide(Direction direction);
}
