package net.povstalec.astralvoyage.common.entities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.povstalec.astralvoyage.common.blocks.PilotSeatBlock;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;

public class PilotSeatEntity extends Entity
{
    public PilotSeatEntity(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    @Override
    public void tick() {
        if(this.getFirstPassenger() instanceof Player player && this.getFirstPassenger() != null)
            this.rotateHeading(player);
        super.tick();
    }

    public void rotateHeading(Player player)
    {
        if(this.level().isClientSide())
            return;

        Direction direction = this.level().getBlockState(this.getOnPos().above()).getValue(PilotSeatBlock.FACING);
        Vec2 vec2 = player.getRotationVector();
        float rotX = vec2.x;
        float rotY = vec2.y + 360 - direction.toYRot();
        
        if(rotX * rotX + rotY * rotY < 200)
            return;

        this.level().getCapability(CapabilitiesInit.SPACESHIP).ifPresent(cap ->
        {
            cap.rotate(0, rotationSpeed(rotY), 0); //TODO Handle pitch and roll
        });
    }

    public float rotationSpeed(float rot)
    {
        return rot / 250;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {}
}
