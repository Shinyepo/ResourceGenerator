package dev.shinyepo.resourcegenerator.pipes.builders;

import dev.shinyepo.resourcegenerator.pipes.CustomBlockStateModel;
import dev.shinyepo.resourcegenerator.pipes.CustomModelPart;
import dev.shinyepo.resourcegenerator.pipes.CustomModelState;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.neoforged.neoforge.client.model.generators.blockstate.UnbakedMutator;
import org.jspecify.annotations.NonNull;

public class CustomBlockModelBuilder extends CustomBlockStateModelBuilder {
    public CustomModelPart.Unbaked model;

    public CustomBlockModelBuilder() {
    }

    public @NonNull CustomBlockModelBuilder withModelLocation(@NonNull Identifier modelLocation) {
        this.model = new CustomModelPart.Unbaked(modelLocation, new CustomModelState());
        return this;
    }

    @Override
    public @NonNull CustomBlockModelBuilder with(@NonNull VariantMutator variantMutator) {
        return this;
    }

    @Override
    public @NonNull CustomBlockModelBuilder with(@NonNull UnbakedMutator unbakedMutator) {
        var result = new CustomBlockModelBuilder();

        if (this.model != null) {
            result.model = unbakedMutator.apply(new CustomBlockStateModel.Unbaked(this.model)).model();
        }
        return result;
    }

    @Override
    public CustomBlockStateModel.Unbaked toUnbaked() {
        if (this.model == null) {
            throw new IllegalStateException("Missing model location!");
        }
        return new CustomBlockStateModel.Unbaked(this.model);
    }
}
