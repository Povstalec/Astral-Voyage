package net.povstalec.astralvoyage.common.datapack.space_objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.povstalec.astralvoyage.common.util.AxisRotation;
import net.povstalec.astralvoyage.common.util.SpaceCoords;

public class Spaceship implements INBTSerializable<CompoundTag>
{
	private static final String SPACE_COORDS = "space_coords";
    private static final String AXIS_ROTATION = "axis_rotation";
    
	private SpaceCoords spaceCoords;
	private SpaceCoords oldSpaceCoords;
	
	private AxisRotation axisRotation;
	private AxisRotation oldAxisRotation;
	
	public Spaceship(SpaceCoords spaceCoords, AxisRotation axisRotation)
	{
		this.spaceCoords = spaceCoords;
		this.oldSpaceCoords = this.spaceCoords.copy();
		
		this.axisRotation = axisRotation;
		this.oldAxisRotation = this.axisRotation.copy();
	}
	
	public Spaceship()
	{
		this(new SpaceCoords(), new AxisRotation());
	}
	
	public void setSpaceCoords(SpaceCoords spaceCoords)
	{
		this.spaceCoords = spaceCoords;
		this.oldSpaceCoords = this.spaceCoords.copy();
	}
	
	public void setAxisRotation(AxisRotation axisRotation)
	{
		this.axisRotation = axisRotation;
		this.oldAxisRotation = this.axisRotation.copy();
	}
	
	public void addSpaceCoords(SpaceCoords other)
	{
		this.oldSpaceCoords = this.spaceCoords.copy();
		this.spaceCoords = this.spaceCoords.add(other);
	}
	
	public void addAxisRotation(AxisRotation other)
	{
		this.oldAxisRotation = this.axisRotation.copy();
		this.axisRotation = this.axisRotation.add(other);
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.put(SPACE_COORDS, this.spaceCoords.serializeNBT());
		tag.put(AXIS_ROTATION, this.axisRotation.serializeNBT());
		
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag tag)
	{
		this.spaceCoords.deserializeNBT(tag.getCompound(SPACE_COORDS));
		this.oldSpaceCoords = this.spaceCoords.copy();
		
		this.axisRotation.deserializeNBT(tag.getCompound(AXIS_ROTATION));
		this.oldAxisRotation = this.axisRotation.copy();
	}
}
