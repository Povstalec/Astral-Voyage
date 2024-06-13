package net.povstalec.astralvoyage.common.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.astralvoyage.common.blocks.SpaceshipMovementBlock;
import net.povstalec.astralvoyage.common.init.BlockEntityInit;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import org.joml.Vector3f;

public class SpaceshipMovementBlockEntity extends BlockEntity {
    public boolean movementType = false;

    public SpaceshipMovementBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInit.MOVEMENT_BLOCK.get(), pos, state);
    }

    @Override
    public BlockEntityType<?> getType()
    {
        return BlockEntityInit.MOVEMENT_BLOCK.get();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putBoolean("movementType", movementType);
    }

    @Override
    public void load(CompoundTag tag) {
        this.movementType = tag.getBoolean("movementType");
        super.load(tag);
    }

    /**
     * @return movementType, false = solar, true = galactic
     */
    public boolean getMovementType()
    {
        return movementType;
    }

    /**
     * @param type movementType, false = solar, true = galactic;
     */
    public void setMovementType(boolean type)
    {
       this.movementType = type;
       this.setChanged();
    }


    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState state, T t)
    {
        SpaceshipMovementBlockEntity entity = (SpaceshipMovementBlockEntity) t;
        Vector3f movement = state.getValue(SpaceshipMovementBlock.FACING).step();
        if(state.getValue(SpaceshipMovementBlock.POWERED))
            level.getCapability(CapabilitiesInit.SPACESHIP).ifPresent(cap -> {
                if (entity.getMovementType())
                    cap.moveGalacticPosition(movement.mul(0.001f));
                else cap.moveSolarPosition(movement.mul(1));
            });
    }
}
