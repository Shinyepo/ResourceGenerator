package dev.shinyepo.resourcegenerator.items;

import dev.shinyepo.resourcegenerator.capabilities.INetworkCapability;
import dev.shinyepo.resourcegenerator.registries.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class Inspector extends Item {
    public Inspector(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown()) return InteractionResult.FAIL;
        if (!level.isClientSide()) {

        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) return super.useOn(context);
        BlockPos clickedPos = context.getClickedPos();
        ServerLevel level = (ServerLevel) context.getLevel();
        INetworkCapability targetCap = level.getCapability(CapabilityRegistry.NETWORK_CAPABILITY, clickedPos, null);
        if (targetCap != null) {
            context.getPlayer().displayClientMessage(Component.literal(targetCap.getNetworkId().toString()), false);
        }
        return super.useOn(context);
    }
}
