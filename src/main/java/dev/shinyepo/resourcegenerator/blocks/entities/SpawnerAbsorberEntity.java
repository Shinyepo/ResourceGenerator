package dev.shinyepo.resourcegenerator.blocks.entities;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Producer;
import dev.shinyepo.resourcegenerator.configs.ProducerConfig;
import dev.shinyepo.resourcegenerator.controllers.DeviceNetworkController;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import dev.shinyepo.resourcegenerator.registries.custom.SpawnerAbsorberRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class SpawnerAbsorberEntity extends Producer {
    private int diedBetweenTicks = 0;

    public SpawnerAbsorberEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.SPAWNER_ABSORBER_ENTITY.get(), ProducerConfig.SPAWNER_ABSORBER, pos, blockState);

        configureSides(Direction.DOWN);
    }

    @Override
    public void tick(ServerLevel level) {
        if (level.getGameTime() % 20 == 0) {
            if (diedBetweenTicks < 1) return;

            UUID networkId = this.networkCapability.getNetworkId();
            if (networkId == null) return;

            Long nexToAdd = this.config.getProduces() * diedBetweenTicks;

            DeviceNetworkController controller = DeviceNetworkController.getInstance(level);
            controller.increaseNetworksBalance(networkId, nexToAdd);

            diedBetweenTicks = 0;
        }
    }


    public void handleEntityDeath() {
        diedBetweenTicks++;

    }

    @Override
    public void onLoad() {
        super.onLoad();
        Level level = this.getLevel();
        if (level instanceof ServerLevel) {
            SpawnerAbsorberRegistry.addAbsorber(this.getBlockPos());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        Level level = this.getLevel();
        if (level instanceof ServerLevel) {
            SpawnerAbsorberRegistry.removeAbsorber(this.getBlockPos());
        }
    }
}
