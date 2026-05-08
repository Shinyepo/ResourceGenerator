package dev.shinyepo.resourcegenerator.events;

import dev.shinyepo.resourcegenerator.blocks.entities.SpawnerAbsorberEntity;
import dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry;
import dev.shinyepo.resourcegenerator.registries.custom.SpawnerAbsorberRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class LivingEntityEvent {

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.level();

        if (level.isClientSide()) return;
        if (!(entity instanceof Enemy)) return;

        var deathPos = entity.position();
        double radius = 9.0F;

        SpawnerAbsorberRegistry
                .getAbsorbers()
                .forEach(absorberPos -> {
                    if (absorberPos.distToCenterSqr(deathPos.x, deathPos.y, deathPos.z)
                            <= radius) {
                        var blockEntity = level.getBlockEntity(absorberPos, BlockEntityRegistry.SPAWNER_ABSORBER_ENTITY.get());
                        blockEntity.ifPresent(SpawnerAbsorberEntity::handleEntityDeath);
                    }
                });
    }
}
