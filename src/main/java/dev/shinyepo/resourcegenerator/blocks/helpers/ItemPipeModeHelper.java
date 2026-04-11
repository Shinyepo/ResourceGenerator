package dev.shinyepo.resourcegenerator.blocks.helpers;

import dev.shinyepo.resourcegenerator.blocks.ItemPipe;
import dev.shinyepo.resourcegenerator.pipes.helpers.ItemPipeConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import static dev.shinyepo.resourcegenerator.properties.CustomProperties.*;

public class ItemPipeModeHelper {

    public static @NonNull InteractionResult cycleMode(ServerLevel level, BlockState state, BlockPos pos, BlockHitResult hit) {
        Direction clickedDir = calcDirection(hit.getLocation(), pos);
        if (clickedDir == null) return InteractionResult.PASS;
        BlockPos rel = pos.relative(clickedDir);
        BlockState sideBlock = level.getBlockState(rel);
        if (sideBlock.getBlock() instanceof ItemPipe) return InteractionResult.PASS;
        EnumProperty<ItemPipeConnection> prop = ItemPipeModeHelper.getProp(clickedDir);
        assert prop != null;
        ItemPipeConnection currCon = state.getValue(prop);
        ItemPipeConnection val = ItemPipeConnection.NONE;

        if (currCon == ItemPipeConnection.EXTRACT) {
            val = ItemPipeConnection.INSERT;
        } else if (currCon == ItemPipeConnection.INSERT) {
            val = ItemPipeConnection.EXTRACT;
        }
        level.setBlock(pos, state.setValue(prop, val), 3);
        //TODO: Notify network of side config change
        return InteractionResult.SUCCESS_SERVER;
    }

    public static Direction calcDirection(Vec3 loc, BlockPos pos) {
        Vec3 rel = loc.subtract(pos.getX(), pos.getY(), pos.getZ());
        double x = rel.x;
        double y = rel.y;
        double z = rel.z;

        if (x > .3 && y < .3) {
            return Direction.DOWN;
        } else if (x > .3 && y > .7) {
            return Direction.UP;
        } else if ((y > .3 && y < .7) && x < .3) {
            return Direction.WEST;
        } else if ((y > .3 && y < .7) && x > .7) {
            return Direction.EAST;
        } else if ((y > .3 && y < .7) && z < .3) {
            return Direction.NORTH;
        } else if ((y > .3 && y < .7) && z > .7) {
            return Direction.SOUTH;
        }
        return null;
    }

    public static EnumProperty<ItemPipeConnection> getProp(Direction facing) {
        switch (facing) {
            case Direction.NORTH -> {
                return NORTH;
            }
            case Direction.SOUTH -> {
                return SOUTH;
            }
            case Direction.WEST -> {
                return WEST;
            }
            case Direction.EAST -> {
                return EAST;
            }
            case Direction.DOWN -> {
                return DOWN;
            }
            case Direction.UP -> {
                return UP;
            }
        }
        return null;
    }
}
