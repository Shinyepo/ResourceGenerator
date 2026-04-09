package dev.shinyepo.resourcegenerator.pipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.TriState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record CustomModelPart(QuadCollection quads, TriState ambientOcclusion,
                              Material.Baked particleMaterial) implements BlockStateModelPart {
    @Override
    public @NonNull List<BakedQuad> getQuads(@Nullable Direction direction) {
        return this.quads.getQuads(direction);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return ambientOcclusion == TriState.TRUE;
    }

    @Override
    public Material.@NonNull Baked particleMaterial() {
        return this.particleMaterial;
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return this.quads.materialFlags();
    }

    public record Unbaked(Identifier model, CustomModelState state) implements BlockStateModelPart.Unbaked {
        public static final MapCodec<CustomModelPart.Unbaked> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Identifier.CODEC.fieldOf("model").forGetter(CustomModelPart.Unbaked::model),
                        CustomModelState.CODEC.fieldOf("state").forGetter(CustomModelPart.Unbaked::state)
                ).apply(instance, CustomModelPart.Unbaked::new));

        @Override
        public CustomModelPart bake(ModelBaker baker) {
            ResolvedModel resolvedModel = baker.getModel(model);

            TextureSlots slots = resolvedModel.getTopTextureSlots();
            TriState ao = resolvedModel.getTopAmbientOcclusion() ? TriState.TRUE : TriState.FALSE;
            Material.Baked particleMaterial = resolvedModel.resolveParticleMaterial(slots, baker);
            QuadCollection quads = resolvedModel.bakeTopGeometry(slots, baker, this.state);

            return new CustomModelPart(quads, ao, particleMaterial);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(model);
        }
    }
}
