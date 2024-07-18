package net.povstalec.astralvoyage.common.blocks;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.astralvoyage.common.entities.PilotSeatEntity;
import net.povstalec.astralvoyage.common.init.EntitiesInit;

public class PilotSeatBlock extends Block
{
    public static final DirectionProperty FACING = DirectionProperty.create("facing");

    public PilotSeatBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if(context.getPlayer().isShiftKeyDown())
        {
            return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
        }
        else return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pPlayer.isShiftKeyDown())
            return InteractionResult.PASS;

        List<PilotSeatEntity> seats = pLevel.getEntitiesOfClass(PilotSeatEntity.class, new AABB(pPos));
        if(!seats.isEmpty())
        {
            PilotSeatEntity seat = seats.get(0);
            List<Entity> passengers = seat.getPassengers();
            if(!passengers.isEmpty() && passengers.get(0) instanceof Player)
                return InteractionResult.PASS;
            if(!pLevel.isClientSide)
            {
                seat.ejectPassengers();
                pPlayer.startRiding(seat);
            }
        }
        if(pLevel.isClientSide())
            return InteractionResult.SUCCESS;

        sitDown(pLevel, pPos, pPlayer);
        return InteractionResult.SUCCESS;
    }

    public static void sitDown(Level world, BlockPos pos, Entity entity) {
        if (world.isClientSide)
            return;
        PilotSeatEntity seat = new PilotSeatEntity(EntitiesInit.PILOT_SEAT.get(), world);
        seat.setPos(pos.getX() + .5f, pos.getY(), pos.getZ() + .5f);
        world.addFreshEntity(seat);
        entity.startRiding(seat, true);
        if (entity instanceof Player player)
            player.setPose(Pose.SITTING);
    }
}
