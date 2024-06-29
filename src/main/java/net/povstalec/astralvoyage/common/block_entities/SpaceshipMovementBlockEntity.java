package net.povstalec.astralvoyage.common.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.astralvoyage.common.blocks.SpaceshipMovementBlock;
import net.povstalec.astralvoyage.common.init.BlockEntityInit;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.util.TempMovementType;
import org.joml.Vector3f;

public class SpaceshipMovementBlockEntity extends BlockEntity {

    public SpaceshipMovementBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInit.MOVEMENT_BLOCK.get(), pos, state);
    }

    @Override
    public BlockEntityType<?> getType()
    {
        return BlockEntityInit.MOVEMENT_BLOCK.get();
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState state, T t)
    {
        Vector3f movement = state.getValue(SpaceshipMovementBlock.FACING).step();
        if(state.getValue(SpaceshipMovementBlock.POWERED))
            level.getCapability(CapabilitiesInit.SPACESHIP).ifPresent(cap -> {
                if (state.getValue(SpaceshipMovementBlock.MOVEMENT_TYPE).equals(TempMovementType.GALACTIC))
                    cap.moveGalacticPosition(movement.mul(0.001f));
                else cap.moveSolarPosition(movement.mul(5000f));
            });
    }
}
