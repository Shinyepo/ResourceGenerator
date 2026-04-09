package dev.shinyepo.resourcegenerator.pipes.builders;

import com.mojang.serialization.MapCodec;
import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.pipes.CustomBlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.client.model.block.CustomBlockModelDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public record CustomBlockDefinition(CustomBlockStateModel.Unbaked model) implements CustomBlockModelDefinition {
    public static final MapCodec<CustomBlockDefinition> CODEC = CustomBlockStateModel.Unbaked.CODEC.xmap(
            CustomBlockDefinition::new, CustomBlockDefinition::model
    );
    public static final Identifier ID = Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, "custom_definition_loader");

    // This method maps all possible states to some unbaked root
    // As the root will generally share block states models, they are typically operated using a `ModelBaker.SharedOperationKey` to cache the loading model
    @Override
    public Map<BlockState, BlockStateModel.UnbakedRoot> instantiate(StateDefinition<Block, BlockState> states, Supplier<String> sourceSupplier) {
        Map<BlockState, BlockStateModel.UnbakedRoot> result = new HashMap<>();

        // Handle for all possible states
        var unbakedRoot = this.model.asRoot();
        states.getPossibleStates().forEach(state -> result.put(state, unbakedRoot));

        return result;
    }

    @Override
    public MapCodec<? extends CustomBlockModelDefinition> codec() {
        return CODEC;
    }
}
