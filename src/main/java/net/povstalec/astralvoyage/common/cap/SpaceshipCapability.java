package net.povstalec.astralvoyage.common.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.povstalec.astralvoyage.common.network.AVNetwork;
import net.povstalec.astralvoyage.common.network.packets.SpaceshipTestDataUpdateMessage;

public class SpaceshipCapability implements INBTSerializable<CompoundTag>{
	private static final String EFFECTS = "effects";
	private static final String X_AXIS_ROTATION = "x_axis_rotation";
	private static final String Y_AXIS_ROTATION = "y_axis_rotation";
	private static final String Z_AXIS_ROTATION = "z_axis_rotation";
	
    private String effects = "";
    
    private float xAxisRotation = 0;
    private float yAxisRotation = 0;
    private float zAxisRotation = 0;

    public SpaceshipCapability() {
    }
    
    public void tick(Level level) {
        
    }
    
    public void clientUpdate(Level level) {
        if(level != null && !level.isClientSide)
        {
            AVNetwork.sendPacketToDimension(level.dimension(),
            		new SpaceshipTestDataUpdateMessage(effects, xAxisRotation, yAxisRotation, zAxisRotation));
        	//System.out.println("Sending update");
        }
    }
    
    public void setEffects(String effects) {
        this.effects = effects;
    }
    
    public String getEffects() {
        return effects;
    }
    
	public void setRotation(float xAxisRotation, float yAxisRotation, float zAxisRotation)
	{
		this.xAxisRotation = xAxisRotation;
		this.yAxisRotation = yAxisRotation;
		this.zAxisRotation = zAxisRotation;
	}

	public float getXAxisRotation()
	{
		return xAxisRotation;
	}

	public float getYAxisRotation()
	{
		return yAxisRotation;
	}

	public float getZAxisRotation()
	{
		return zAxisRotation;
	}

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString(EFFECTS, effects);
        tag.putFloat(X_AXIS_ROTATION, xAxisRotation);
        tag.putFloat(Y_AXIS_ROTATION, yAxisRotation);
        tag.putFloat(Z_AXIS_ROTATION, zAxisRotation);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.effects = nbt.getString(EFFECTS);
        this.xAxisRotation = nbt.getFloat(X_AXIS_ROTATION);
        this.yAxisRotation = nbt.getFloat(Y_AXIS_ROTATION);
        this.zAxisRotation = nbt.getFloat(Z_AXIS_ROTATION);
    }
}
