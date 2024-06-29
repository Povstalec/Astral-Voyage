package net.povstalec.astralvoyage.common.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.astralvoyage.common.block_entities.SpaceshipMovementBlockEntity;
import net.povstalec.astralvoyage.common.init.BlockEntityInit;
import net.povstalec.astralvoyage.common.init.BlockInit;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.util.DimensionHelper;
import net.povstalec.astralvoyage.common.util.TempMovementType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class SpaceshipMovementBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final EnumProperty<TempMovementType> MOVEMENT_TYPE = EnumProperty.create("movement_type", TempMovementType.class);

    public SpaceshipMovementBlock(Properties prop) {
        super(prop);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if(!level.isClientSide)
        {
            state.cycle(MOVEMENT_TYPE);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(POWERED, false).setValue(MOVEMENT_TYPE, TempMovementType.SOLAR);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING).add(POWERED).add(MOVEMENT_TYPE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpaceshipMovementBlockEntity(pos, state);
    }


    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return direction == state.getValue(FACING);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockNeighbour, BlockPos neighbourPos, boolean bool) {
        if(!level.isClientSide())
        {
            boolean powered = state.getValue(POWERED);
            if(powered != level.hasNeighborSignal(pos))
                if(powered)
                    level.scheduleTick(pos, this, 4);
                else
                    level.setBlock(pos, state.cycle(POWERED), 2);
        }

    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(state.getValue(POWERED) && !level.hasNeighborSignal(pos))
            level.setBlock(pos, state.cycle(POWERED), 2);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == BlockEntityInit.MOVEMENT_BLOCK.get() ? SpaceshipMovementBlockEntity::tick : null;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> components, TooltipFlag flags) {
        components.add(Component.translatable("tooltip.astralvoyage.movementBlock").withStyle(ChatFormatting.DARK_AQUA));

        super.appendHoverText(stack, getter, components, flags);
    }
}
