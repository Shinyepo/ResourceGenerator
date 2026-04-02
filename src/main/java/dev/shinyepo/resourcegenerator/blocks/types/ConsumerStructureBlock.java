package dev.shinyepo.resourcegenerator.blocks.types;

import dev.shinyepo.resourcegenerator.blocks.entities.types.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class ConsumerStructureBlock extends BasicBlock {

    public ConsumerStructureBlock(Properties properties) {
        super(properties);
    }


    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, by, itemStack);
        if (!level.isClientSide()) {
            for (int i = 0; i < 3; i++) {
                boolean foundConsumer = scanForConsumers((ServerLevel) level, pos, 3 + (i * 2), i + 1);
                if (foundConsumer) break;
            }
        }
    }

    private boolean scanForConsumers(ServerLevel level, BlockPos placementPos, int size, int y) {
        int half = (size - 1) / 2;
        for (int x = half; x >= -half; x--) {
            for (int z = -half; z <= half; z++) {
                BlockPos offsetPos = placementPos.offset(x, y, z);
                BlockEntity foundBlockEntity = level.getBlockEntity(offsetPos);
                if (foundBlockEntity instanceof Consumer consumer) {
                    consumer.shouldReVerifyPattern(y);
                    return true;
                }
            }
        }
        return false;
    }
}
