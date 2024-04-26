package net.povstalec.astralvoyage.common.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.povstalec.astralvoyage.common.network.AVNetwork;
import net.povstalec.astralvoyage.common.network.packets.SpaceshipTestDataUpdateMessage;

public class SpaceshipCapability implements  ISpaceshipLevel{
    private final Level level;
    private String testString = "RenderingSyncing";

    public SpaceshipCapability(Level level) {
        this.level = level;
    }


    @Override
    public Level getLevel() {
        return null;
    }

    @Override
    public void tick() {
        if(this.getLevel().getGameTime() % 20 == 0){
            clientUpdate();
        }
    }

    @Override
    public void clientUpdate() {
        if(level != null && !level.isClientSide)
            AVNetwork.sendPacketToDimension(this.level.dimension(), new SpaceshipTestDataUpdateMessage(this.testString));

    }

    @Override
    public void setString(String string) {
        this.testString = string;
    }

    @Override
    public String getString() {
        return testString;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("test", testString);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.testString = nbt.getString("test");
    }
}
