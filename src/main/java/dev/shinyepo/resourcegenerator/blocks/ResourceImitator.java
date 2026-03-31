package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.ResourceImitatorEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.Consumer;
import dev.shinyepo.resourcegenerator.blocks.types.BasicBlock;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class ResourceImitator extends BasicBlock {

    public ResourceImitator(Properties properties) {
        super(ResourceImitatorEntity::new, properties);
        SHAPE = makeShape();
    }

    public VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 0.09375, 0.09375, 0.09375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.90625, 0, 0, 1, 0.09375, 0.09375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.90625, 0, 0.90625, 1, 0.09375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.90625, 0.09375, 0.09375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.09375, 0, 0.9375, 0.90625, 0.0625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.09375, 0, 0, 0.90625, 0.0625, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.09375, 0.0625, 0.0625, 0.90625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0, 0.09375, 1, 0.0625, 0.90625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.09375, 0.9375, 0, 0.90625, 1, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.90625, 0, 0.09375, 1, 0.09375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.9375, 0.09375, 0.0625, 1, 0.90625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.9375, 0.09375, 1, 1, 0.90625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.90625, 0.90625, 0, 1, 1, 0.09375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.90625, 0.90625, 0.90625, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.90625, 0.90625, 0.09375, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.09375, 0.9375, 0.9375, 0.90625, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.09375, 0.9375, 0.0625, 0.90625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.09375, 0.9375, 1, 0.90625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.09375, 0, 1, 0.90625, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.09375, 0, 0.0625, 0.90625, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375), BooleanOp.OR);

        return shape;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        BlockEntity targetBlockEntity = level.getBlockEntity(pos);
        if (targetBlockEntity instanceof ResourceImitatorEntity resourceImitatorEntity) {
            if (itemStack.is(TagRegistry.CONSUMER_RESOURCES)) {
                //TODO: remove item from players inventory
                resourceImitatorEntity.setImitatedResource(new ItemStack(itemStack.getItem()));
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
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
