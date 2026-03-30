package dev.shinyepo.resourcegenerator.blocks.entities.types;

import net.minecraft.server.level.ServerLevel;

public interface ITickableEntity {
    void tick(ServerLevel level);
}
