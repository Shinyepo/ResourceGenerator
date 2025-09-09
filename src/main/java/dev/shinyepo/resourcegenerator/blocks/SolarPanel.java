package dev.shinyepo.resourcegenerator.blocks;

import dev.shinyepo.resourcegenerator.blocks.entities.SolarPanelEntity;
import dev.shinyepo.resourcegenerator.blocks.entities.types.INetworkDevice;
import dev.shinyepo.resourcegenerator.controllers.DeviceNetworkController;
import dev.shinyepo.resourcegenerator.properties.CustomProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SolarPanel extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 3, 16),
            Block.box(4, 3, 3, 5, 7, 4),
            Block.box(4, 3, 12, 5, 7, 13),
            Block.box(11, 3, 12, 12, 7, 13),
            Block.box(11, 3, 3, 12, 7, 4),
            Block.box(5.7, 6, 5.7, 10.3, 7, 10.3),
            Block.box(0, 7, 0, 16, 8, 16));
    private static final BooleanProperty OPERATIONAL = CustomProperties.OPERATIONAL;

    public SolarPanel(Properties properties) {
        super(properties);

        registerDefaultState(getStateDefinition().any()
                .setValue(OPERATIONAL, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPERATIONAL);
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SolarPanelEntity(blockPos, blockState);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;
        INetworkDevice networkDevice = (INetworkDevice) level.getBlockEntity(pos);
        assert networkDevice != null;

        BlockEntity below = level.getBlockEntity(pos.below());
        DeviceNetworkController controller = DeviceNetworkController.getInstance((ServerLevel) level);
        if (below instanceof INetworkDevice belowDevice) {
            UUID networkId = controller.addDeviceToNetwork(belowDevice.getNetworkId(), (INetworkDevice) level.getBlockEntity(pos), pos);
            networkDevice.setNetworkId(networkId);
        } else {
            UUID networkId = controller.createNewNetwork(level.dimension(), networkDevice, pos);
            networkDevice.setNetworkId(networkId);
        }
    }
}
