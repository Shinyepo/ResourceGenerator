package dev.shinyepo.resourcegenerator.blocks.entities.types;

import dev.shinyepo.resourcegenerator.configs.ProducerConfig;
import dev.shinyepo.resourcegenerator.controllers.DeviceNetworkController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class Producer extends NetworkDeviceEntity {
    private final ProducerConfig config;

    public Producer(BlockEntityType<?> type, ProducerConfig config, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.config = config;
    }

    @Override
    public void tick(ServerLevel level) {
        if (level.getGameTime() % 20 == 0) {
            UUID networkId = networkCapability.getNetworkId();
            if (networkId != null) {
                DeviceNetworkController controller = DeviceNetworkController.getInstance(level);
                controller.increaseNetworksBalance(networkId, config.getProduces());
            }
        }
    }
}
