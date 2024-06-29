package net.povstalec.astralvoyage.common.block_entities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.astralvoyage.common.blocks.SpaceshipHeadingBlock;
import net.povstalec.astralvoyage.common.capability.SpaceshipCapability;
import net.povstalec.astralvoyage.common.init.BlockEntityInit;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.util.Rotation;

public class SpaceshipHeadingBlockEntity extends BlockEntity {
    public Rotation target = new Rotation(0, 0, 0);
    public int duration = 0;
    public int maxDuration = 0;

    public SpaceshipHeadingBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.HEADING_BLOCK.get(), pos, state);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState state, T t)
    {
        SpaceshipHeadingBlockEntity entity = (SpaceshipHeadingBlockEntity) t;
        LazyOptional<SpaceshipCapability> cap = level.getCapability(CapabilitiesInit.SPACESHIP);
        if(cap.isPresent() && cap.resolve().isPresent())
        {
            SpaceshipCapability capability = cap.resolve().get();

            if(state.getValue(SpaceshipHeadingBlock.POWERED) && !capability.getRotation().equals(entity.getTarget()))
            {
                entity.calculateDuration((int) (entity.getTarget().asVector().length()/capability.getRotation().asVector().length()));

                float xAxisRotation = Mth.lerp((float) entity.getDuration()/entity.getMaxDuration(), capability.getRotation().yaw, entity.getTarget().yaw);
                float yAxisRotation = Mth.lerp((float) entity.getDuration()/entity.getMaxDuration(), capability.getRotation().pitch, entity.getTarget().pitch);
                float zAxisRotation = Mth.lerp((float) entity.getDuration()/entity.getMaxDuration(), capability.getRotation().roll, entity.getTarget().roll);

                entity.setDuration(entity.getDuration()+1);

                capability.setRotation(xAxisRotation, yAxisRotation, zAxisRotation);
            }
        }
    }

    public void calculateDuration(int factor)
    {
        if(this.getDuration() == 0)
        {
            this.setMaxDuration(120*factor);
        }

    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("targetRotation", this.target.serialize());
        tag.putInt("duration", this.duration);
        tag.putInt("maxDuration", this.maxDuration);
    }

    @Override
    public void load(CompoundTag tag) {
        this.target = Rotation.deserialize(tag);
        this.duration = tag.getInt("duration");
        this.maxDuration = tag.getInt("maxDuration");
        super.load(tag);
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Rotation getTarget() {
        return target;
    }

    public void setTarget(Rotation target) {
        this.target = target;
        this.setChanged();
    }
}
