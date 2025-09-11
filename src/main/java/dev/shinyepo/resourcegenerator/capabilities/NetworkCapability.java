package dev.shinyepo.resourcegenerator.capabilities;

import dev.shinyepo.resourcegenerator.configs.SideConfig;
import net.minecraft.core.Direction;

import java.util.UUID;

public class NetworkCapability implements INetworkCapability {
    private UUID networkId;
    private final SideConfig[] sides = new SideConfig[]{SideConfig.NONE, SideConfig.NONE, SideConfig.NONE, SideConfig.NONE, SideConfig.NONE, SideConfig.NONE};

    @Override
    public UUID getNetworkId() {
        return networkId;
    }

    @Override
    public void setNetworkId(UUID networkId) {
        this.networkId = networkId;
    }

    @Override
    public SideConfig isSideValid(Direction direction) {
        return sides[direction.ordinal()];
    }

    @Override
    public SideConfig[] getSides() {
        return sides;
    }

    @Override
    public void configureSide(Direction direction) {
        sides[direction.ordinal()] = SideConfig.NETWORK;
    }

}
