package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.ResourceImitatorEntity;
import dev.shinyepo.resourcegenerator.blocks.types.BasicBlock;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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
}
