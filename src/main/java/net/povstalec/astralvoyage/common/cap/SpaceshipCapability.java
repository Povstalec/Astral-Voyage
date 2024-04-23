package net.povstalec.astralvoyage.common.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.povstalec.astralvoyage.common.network.AVNetwork;
import net.povstalec.astralvoyage.common.network.packets.SpaceshipDataUpdateMessage;
import net.povstalec.astralvoyage.common.network.packets.spaceship.SpaceshipData;

public class SpaceshipCapability implements  ISpaceshipLevel{
    private final Level level;

    public SpaceshipCapability(Level level) {
        this.level = level;
    }


    @Override
    public Level getLevel() {
        return null;
    }

    @Override
    public void tick() {

    }

    @Override
    public void clientUpdate(SpaceshipData data) {
        if(level != null && !level.isClientSide)
            AVNetwork.sendPacketToDimension(this.level.dimension(), new SpaceshipDataUpdateMessage(data));

    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
