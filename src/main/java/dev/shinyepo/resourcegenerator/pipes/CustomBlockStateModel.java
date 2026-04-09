package dev.shinyepo.resourcegenerator.pipes;

import com.mojang.serialization.MapCodec;
import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.pipes.builders.ItemPipeGeometryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class CustomBlockStateModel implements DynamicBlockStateModel {
    private final CustomModelPart model;
    private final ItemPipeGeometryBuilder geometryBuilder = new ItemPipeGeometryBuilder();

    public CustomBlockStateModel(CustomModelPart model) {
        this.model = model;
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        var quads = geometryBuilder.getOrBuild(state);
        var part = new CustomModelPart(quads, TriState.TRUE, particleMaterial());
        if (part.quads() != null) {
            parts.add(part);
        }
    }

    @Override
    public @NonNull Object createGeometryKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
        return new ItemPipeGeometryBuilder.GeometryKey(state);
    }

    @Override
    public Material.@NonNull Baked particleMaterial() {
        var particleSprite = Minecraft.getInstance().getAtlasManager().get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "block/pipe/normal")));
        return new Material.Baked(particleSprite, false);
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return this.model.materialFlags();
    }

    public record Unbaked(CustomModelPart.Unbaked model) implements CustomUnbakedBlockStateModel {
        public static final MapCodec<CustomBlockStateModel.Unbaked> CODEC = CustomModelPart.Unbaked.CODEC.xmap(
                CustomBlockStateModel.Unbaked::new,
                CustomBlockStateModel.Unbaked::model
        );

        public static final Identifier ID = Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "custom_model_loader");

        @Override
        public @NonNull MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
            return CODEC;
        }

        @Override
        public @NonNull BlockStateModel bake(@NonNull ModelBaker baker) {
            return new CustomBlockStateModel(this.model.bake(baker));
        }

        @Override
        public void resolveDependencies(@NonNull Resolver resolver) {
            this.model.resolveDependencies(resolver);
        }
    }
}
