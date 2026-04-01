package dev.shinyepo.resourcegenerator.registries;

import com.mojang.serialization.MapCodec;
import dev.shinyepo.resourcegenerator.blocks.types.BasicBlock;
import dev.shinyepo.resourcegenerator.blocks.types.NetworkBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockTypeRegistry {
    public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_TYPE = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, "yourmodid");

    public static final Supplier<MapCodec<NetworkBlock>> NETWORK_BLOCK = BLOCK_TYPE.register(
            "network",
            () -> BlockBehaviour.simpleCodec(NetworkBlock::new)
    );

    public static final Supplier<MapCodec<BasicBlock>> BASIC_BLOCK = BLOCK_TYPE.register(
            "basic",
            () -> BlockBehaviour.simpleCodec(BasicBlock::new)
    );
}
