package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.ResourceImitatorEntity;
import dev.shinyepo.resourcegenerator.blocks.types.ConsumerStructureBlock;
import dev.shinyepo.resourcegenerator.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ResourceImitator extends ConsumerStructureBlock {

    public ResourceImitator(Properties properties) {
        super(properties);
        SHAPE = makeShape();

        setBlockEntity(ResourceImitatorEntity::new);
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
    protected InteractionResult useItemOn(ItemStack usedItemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        BlockEntity usedOnEntity = level.getBlockEntity(pos);
        if (usedOnEntity instanceof ResourceImitatorEntity resourceImitatorEntity) {
            if (usedItemStack.is(TagRegistry.CONSUMER_RESOURCES)) {
                ItemStack result = resourceImitatorEntity.setImitatedResource(new ItemStack(usedItemStack.getItem(), 1));
                if (result.isEmpty()) {
                    usedItemStack.shrink(1);
                } else if (!result.is(usedItemStack.getItem())) {
                    usedItemStack.shrink(1);
                    player.addItem(result);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(usedItemStack, state, level, pos, player, hand, hitResult);
    }
}
