package net.povstalec.astralvoyage.common.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.network.AVNetwork;
import net.povstalec.astralvoyage.common.network.packets.SpaceshipTestDataUpdateMessage;

public class SpaceshipCapability implements ISpaceshipLevel{
    private String stellarLocationID = "";

    public SpaceshipCapability() {
    }

    @Override
    public void tick(Level level) {
        if(level.getGameTime() % 20 == 0){
        	
        	if((level.getGameTime() / 20) % 2 == 0)
        		clientUpdate(level, AstralVoyage.MODID + ":earth");
        	else
                clientUpdate(level, AstralVoyage.MODID + ":sol");
        		
        }
    }

    @Override
    public void clientUpdate(Level level, String id) {
        if(level != null && !level.isClientSide)
            AVNetwork.sendPacketToDimension(level.dimension(), new SpaceshipTestDataUpdateMessage(id));
    }

    @Override
    public void setStellarLocationID(String id) {
        this.stellarLocationID = id;
    }

    @Override
    public String getStellarLocationID() {
        return stellarLocationID;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("test", stellarLocationID);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.stellarLocationID = nbt.getString("test");
    }
}
