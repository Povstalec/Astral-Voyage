package net.povstalec.astralvoyage.common.capability;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.povstalec.astralvoyage.common.data.SpaceObjects;
import net.povstalec.astralvoyage.common.datapack.ClientSpaceObject;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.network.AVNetwork;
import net.povstalec.astralvoyage.common.network.packets.RenderObjectUpdateMessage;
import net.povstalec.astralvoyage.common.network.packets.SpaceObjectUpdateMessage;
import net.povstalec.astralvoyage.common.util.Rotation;
import net.povstalec.astralvoyage.common.util.TextureLayerData;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpaceshipCapability implements INBTSerializable<CompoundTag>
{
	private static final String SOLAR_POS_X = "solar_pos_x";
    private static final String SOLAR_POS_Y = "solar_pos_y";
    private static final String SOLAR_POS_Z = "solar_pos_z";
	
	private static final String GALACTIC_POS_X = "galactic_pos_x";
	private static final String GALACTIC_POS_Y = "galactic_pos_y";
	private static final String GALACTIC_POS_Z = "galactic_pos_z";

    private Vector3f galacticPosition = new Vector3f(0, 0, 0);
    private Vector3f solarPosition = new Vector3f(0, 0, 0);
	private Vector3f oldGalacticPosition = new Vector3f(0, 0, 0);
    private Vector3f oldSolarPosition = new Vector3f(0, 0, 0);

	private Rotation rotation = new Rotation(0, 0, 0);
	private Rotation oldRotation = new Rotation(0 ,0 , 0);

    private Rotation targetRotation = new Rotation(0, 0, 0);

    private List<ClientSpaceObject> renderObjects = Lists.newArrayList();

    public SpaceshipCapability()
    {
    }
    
    public void tick(Level level)
    {
    	this.oldGalacticPosition.set(galacticPosition);
        this.oldSolarPosition.set(solarPosition);
    	this.oldRotation.set(rotation);

        if(!level.isClientSide())
        {
            handleRenderObjects(level);
        }

    	clientUpdate(level);
    }

    private void handleRenderObjects(Level level)
    {
        List<ClientSpaceObject> childObjects = new ArrayList<>();
        SpaceObjects.get(level).spaceObjects.forEach((objectId, object) ->
                childObjects.add(new ClientSpaceObject(SpaceObject.stringToSpaceObjectKey(objectId), object.getSize(),
                        Optional.of(object.getOrbitMap().isPresent() && object.getOrbitMap().get().getSecond().containsKey("orbit_start") ? object.getOrbitMap().get().getSecond().get("orbit_start") : 0D),
                        new Vector3f(object.getOrbitMap().isPresent() && object.getOrbitMap().get().getSecond().containsKey("distance") ? object.getOrbitMap().get().getSecond().get("distance").floatValue() : 0f, 0 ,0),
                        object.getGalacticPos(), TextureLayerData.toDataList(object.getTextureLayers()))));

        childObjects.removeIf(filter -> filter.getGalacticPos().isEmpty());

        List<ClientSpaceObject> objectsToAdd = new ArrayList<>(childObjects);
        childObjects.forEach(objects -> {
            SpaceObject.Serializable object = SpaceObjects.get(level.getServer()).spaceObjects.get(objects.getKey().location().toString());
            if (object.getGalacticPos().isPresent() && object.getGalacticPos().get().equals(this.getGalacticPosition(), 0.1f))
                object.getChildObjects().forEach(child -> {
                    SpaceObject.Serializable childObject = SpaceObjects.get(level.getServer()).spaceObjects.get(child.location().toString());
                    ClientSpaceObject clientChildObject = new ClientSpaceObject(child, childObject.getSize(),
                            Optional.of(childObject.getOrbitMap().isPresent() && childObject.getOrbitMap().get().getSecond().containsKey("orbit_start") ? childObject.getOrbitMap().get().getSecond().get("orbit_start") : 0D),
                            new Vector3f(childObject.getOrbitMap().isPresent() && childObject.getOrbitMap().get().getSecond().containsKey("distance") ? childObject.getOrbitMap().get().getSecond().get("distance").floatValue() : 147280000f, 0, 0),
                            Optional.empty(), TextureLayerData.toDataList(childObject.getTextureLayers()));
                    if (!objectsToAdd.contains(clientChildObject))
                        objectsToAdd.add(clientChildObject);
                });
        });

        this.renderObjects = childObjects;
    }

    private void clientUpdate(Level level)
    {
        if(level != null && !level.isClientSide)
        {
            AVNetwork.sendPacketToDimension(level.dimension(),
            		new SpaceObjectUpdateMessage(this.serializeNBT()));
            AVNetwork.sendPacketToDimension(level.dimension(),
                    new RenderObjectUpdateMessage(this.getRenderObjects()));
        }
    }
    
	public void setRotation(float xAxisRotation, float yAxisRotation, float zAxisRotation)
	{
		this.rotation.yaw = xAxisRotation;
		this.rotation.pitch = yAxisRotation;
		this.rotation.roll = zAxisRotation;
	}

    public void setTargetRotation(float xAxisRotation, float yAxisRotation, float zAxisRotation)
    {
        this.targetRotation.yaw = xAxisRotation;
        this.targetRotation.pitch = yAxisRotation;
        this.targetRotation.roll = zAxisRotation;
    }

    public void setSolarPosition(float solarX, float solarY, float solarZ)
    {
        this.solarPosition.x = solarX;
        this.solarPosition.y = solarY;
        this.solarPosition.z = solarZ;
    }

	public void setGalacticPostion(float galacticX, float galacticY, float galacticZ)
	{
		this.galacticPosition.x = galacticX;
		this.galacticPosition.y = galacticY;
		this.galacticPosition.z = galacticZ;
	}

    public void moveGalacticPosition(Vector3f vector)
    {
        this.galacticPosition.x += this.rotation.step()*vector.x;
        this.galacticPosition.y += this.rotation.step()*vector.y;
        this.galacticPosition.z += this.rotation.step()*vector.z;
    }

    public void moveSolarPosition(Vector3f vector)
    {
        this.solarPosition.x += this.rotation.step()*vector.x;
        this.solarPosition.y += this.rotation.step()*vector.y;
        this.solarPosition.z += this.rotation.step()*vector.z;
    }

    public void rotate(Rotation rotation)
    {
        this.rotation = rotation;
    }

    public List<ClientSpaceObject> getRenderObjects()
    {
        return renderObjects;
    }

    public Vector3f getSolarPosition()
    {
        return solarPosition;
    }

    public Vector3f getGalacticPosition()
	{
		return galacticPosition;
	}

	public Vector3f getOldGalacticPosition()
	{
		return oldGalacticPosition;
	}

	public Rotation getRotation()
	{
		return rotation;
	}

    public void setRenderObjects(List<ClientSpaceObject> renderObjects) {
        this.renderObjects = renderObjects;
    }

	public Rotation getOldRotation()
	{
		return oldRotation;
	}

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putFloat(SOLAR_POS_X, solarPosition.x);
        tag.putFloat(SOLAR_POS_Y, solarPosition.y);
        tag.putFloat(SOLAR_POS_Z, solarPosition.z);
        
        tag.putFloat(GALACTIC_POS_X, galacticPosition.x);
        tag.putFloat(GALACTIC_POS_Y, galacticPosition.y);
        tag.putFloat(GALACTIC_POS_Z, galacticPosition.z);
        
        tag.put("rotation", rotation.serialize());

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.solarPosition.x = nbt.getFloat(SOLAR_POS_X);
        this.solarPosition.y = nbt.getFloat(SOLAR_POS_Y);
        this.solarPosition.z = nbt.getFloat(SOLAR_POS_Z);
        
        this.galacticPosition.x = nbt.getFloat(GALACTIC_POS_X);
        this.galacticPosition.y = nbt.getFloat(GALACTIC_POS_Y);
        this.galacticPosition.z = nbt.getFloat(GALACTIC_POS_Z);
        
        this.rotation.set(Rotation.deserialize(nbt));
    }
}
