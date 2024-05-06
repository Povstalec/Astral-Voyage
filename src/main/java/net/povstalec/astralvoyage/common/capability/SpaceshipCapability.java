package net.povstalec.astralvoyage.common.capability;

import org.joml.Vector3f;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.povstalec.astralvoyage.common.network.AVNetwork;
import net.povstalec.astralvoyage.common.network.packets.SpaceObjectUpdateMessage;

public class SpaceshipCapability implements INBTSerializable<CompoundTag>
{
	private static final String LOCATION = "space_object";
	
	private static final String GALACTIC_POS_X = "galactic_pos_x";
	private static final String GALACTIC_POS_Y = "galactic_pos_y";
	private static final String GALACTIC_POS_Z = "galactic_pos_z";
	
	private static final String X_AXIS_ROTATION = "x_axis_rotation";
	private static final String Y_AXIS_ROTATION = "y_axis_rotation";
	private static final String Z_AXIS_ROTATION = "z_axis_rotation";
	
    private String spaceObjectString = "";

	private Vector3f galacticPosition = new Vector3f(0, 0, 0);
	private Vector3f rotation = new Vector3f(0, 0, 0);

    public SpaceshipCapability()
    {
    }
    
    public void tick(Level level)
    {
        //this.galacticPosition.x -= 0.01F;
    	clientUpdate(level);
    }
    
    private void clientUpdate(Level level)
    {
        if(level != null && !level.isClientSide)
        {
            AVNetwork.sendPacketToDimension(level.dimension(),
            		new SpaceObjectUpdateMessage(spaceObjectString, 
            				galacticPosition.x, galacticPosition.y, galacticPosition.z, 
            				rotation.x, rotation.y, rotation.z));
        }
    }
    
    public void setSpaceObject(String spaceObjectString)
    {
        this.spaceObjectString = spaceObjectString;
    }
    
    public String getSpaceObjectString()
    {
        return spaceObjectString;
    }
    
	public void setRotation(float xAxisRotation, float yAxisRotation, float zAxisRotation)
	{
		this.rotation.x = xAxisRotation;
		this.rotation.y = yAxisRotation;
		this.rotation.z = zAxisRotation;
	}
    
	public void setGalacticPostion(float galacticX, float galacticY, float galacticZ)
	{
		this.galacticPosition.x = galacticX;
		this.galacticPosition.y = galacticY;
		this.galacticPosition.z = galacticZ;
	}

	public Vector3f getGalacticPosition()
	{
		return galacticPosition;
	}

	public Vector3f getRotation()
	{
		return rotation;
	}

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putString(LOCATION, spaceObjectString);
        
        tag.putFloat(GALACTIC_POS_X, galacticPosition.x);
        tag.putFloat(GALACTIC_POS_Y, galacticPosition.y);
        tag.putFloat(GALACTIC_POS_Z, galacticPosition.z);
        
        tag.putFloat(X_AXIS_ROTATION, rotation.x);
        tag.putFloat(Y_AXIS_ROTATION, rotation.y);
        tag.putFloat(Z_AXIS_ROTATION, rotation.z);
        
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.spaceObjectString = nbt.getString(LOCATION);
        
        this.galacticPosition.x = nbt.getFloat(GALACTIC_POS_X);
        this.galacticPosition.y = nbt.getFloat(GALACTIC_POS_Y);
        this.galacticPosition.z = nbt.getFloat(GALACTIC_POS_Z);
        
        this.rotation.x = nbt.getFloat(X_AXIS_ROTATION);
        this.rotation.y = nbt.getFloat(Y_AXIS_ROTATION);
        this.rotation.z = nbt.getFloat(Z_AXIS_ROTATION);
        
    }
}
