package dev.shinyepo.resourcegenerator.pipes;

import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.core.Direction;
import org.joml.Matrix4fc;

public class CustomModelState implements ModelState {
    public static final Codec<CustomModelState> CODEC = MapCodec.unitCodec(new CustomModelState());

    public CustomModelState() {
    }

    @Override
    public Transformation transformation() {
        return Transformation.IDENTITY;
    }

    @Override
    public Matrix4fc faceTransformation(Direction face) {
        return NO_TRANSFORM;
    }

    @Override
    public Matrix4fc inverseFaceTransformation(Direction face) {
        return NO_TRANSFORM;
    }
}
