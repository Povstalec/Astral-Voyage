package net.povstalec.astralvoyage.common.capability;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.data.SpaceObjects;
import net.povstalec.astralvoyage.common.datapack.ClientSpaceObject;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.network.AVNetwork;
import net.povstalec.astralvoyage.common.network.packets.RenderObjectUpdateMessage;
import net.povstalec.astralvoyage.common.network.packets.SpaceObjectUpdateMessage;
import net.povstalec.astralvoyage.common.util.TextureLayerData;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SpaceshipCapability implements INBTSerializable<CompoundTag>
{
	private static final String SOLAR_POS_X = "solar_pos_x";
    private static final String SOLAR_POS_Y = "solar_pos_y";
    private static final String SOLAR_POS_Z = "solar_pos_z";
	
	private static final String GALACTIC_POS_X = "galactic_pos_x";
	private static final String GALACTIC_POS_Y = "galactic_pos_y";
	private static final String GALACTIC_POS_Z = "galactic_pos_z";
	
	private static final String X_AXIS_ROTATION = "x_axis_rotation";
	private static final String Y_AXIS_ROTATION = "y_axis_rotation";
	private static final String Z_AXIS_ROTATION = "z_axis_rotation";

    private Vector3f galacticPosition = new Vector3f(0, 0, 0);
    private Vector3f solarPosition = new Vector3f(147280000, 0, 0);
	private Vector3f oldGalacticPosition = new Vector3f(0, 0, 0);
    private Vector3f oldSolarPosition = new Vector3f(0, 0, 0);

	private Vector3f rotation = new Vector3f(0, 0, 0);
	private Vector3f oldRotation = new Vector3f(0, 0, 0);

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
        MinecraftServer server = level.getServer();
        List<ClientSpaceObject> renderObjects = new ArrayList<>();
        List<Map.Entry<String, SpaceObject.Serializable>> listLocal = SpaceObjects.get(server).spaceObjects.entrySet().stream().filter(entry -> entry.getValue().getGalacticPos() != null && entry.getValue().getGalacticPos().equals(this.getGalacticPosition(), 0.1f)).toList();
        listLocal.forEach(
        entry -> {
            String objectId = entry.getKey();
            SpaceObject.Serializable object = entry.getValue();

            renderObjects.add(serializeableToClient(objectId, object));

            object.getChildObjects().forEach(
                    childObject -> renderObjects.add(serializeableToClient(childObject.location().toString(),
                            SpaceObjects.get(server).spaceObjects.get(childObject.location().toString()))));
        });
        List<Map.Entry<String, SpaceObject.Serializable>> listGlobal = SpaceObjects.get(server).spaceObjects.entrySet().stream().filter(entry -> entry.getValue().getGalacticPos() != null && !entry.getValue().getGalacticPos().equals(this.getGalacticPosition(), 0.1f)).toList();
        listGlobal.forEach(
        entry -> {
            String objectId = entry.getKey();
            SpaceObject.Serializable object = entry.getValue();

            renderObjects.add(serializeableToClient(objectId, object));
        });

        List<ResourceKey<Level>> shipLevels = new ArrayList<>();
        server.getAllLevels().forEach(
                serverLevel -> {
                    if(serverLevel.getCapability(CapabilitiesInit.SPACESHIP).isPresent())
                        shipLevels.add(serverLevel.dimension());
                });

        shipLevels.forEach(
                shipLevel -> {
                    server.getLevel(shipLevel).getCapability(CapabilitiesInit.SPACESHIP).ifPresent(
                            shipCap -> {
                                if(shipLevel != level.dimension())
                                    renderObjects.add(spaceshipToClient(shipCap, server.getLevel(shipLevel)));
                            });
                });


        this.renderObjects = renderObjects;
    }

    public static ClientSpaceObject serializeableToClient(String objectId, SpaceObject.Serializable object)
    {
        return new ClientSpaceObject(SpaceObject.stringToSpaceObjectKey(objectId), object.getSize() == null ? 1300 : object.getSize(),
                object.getOrbitMap() != null && object.getOrbitMap().getSecond().containsKey("orbit_start") ? object.getOrbitMap().getSecond().get("orbit_start") : 0D,
                new Vector3f(object.getOrbitMap() != null && object.getOrbitMap().getSecond().containsKey("distance") ? object.getOrbitMap().getSecond().get("distance").floatValue() : 0f, 0 ,0),
                object.getGalacticPos(), object.getTextureLayers());
    }

    public static ClientSpaceObject spaceshipToClient(SpaceshipCapability capability, Level level)
    {
        return new ClientSpaceObject(SpaceObject.stringToSpaceObjectKey(level.dimension().location().toString()),
                1, null, capability.getSolarPosition(),
                capability.getGalacticPosition(),
                List.of(new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/spaceship_glow.png"),
                        new Pair<>(List.of(255, 255, 255, 255), false)))));
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
		this.rotation.x = xAxisRotation;
		this.rotation.y = yAxisRotation;
		this.rotation.z = zAxisRotation;
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
        this.galacticPosition.x += vector.x;
        this.galacticPosition.y += vector.y;
        this.galacticPosition.z += vector.z;
    }

    public void moveSolarPosition(Vector3f vector)
    {
        this.solarPosition.x += vector.x;
        this.solarPosition.y += vector.y;
        this.solarPosition.z += vector.z;
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

	public Vector3f getRotation()
	{
		return rotation;
	}

    public void setRenderObjects(List<ClientSpaceObject> renderObjects) {
        this.renderObjects = renderObjects;
    }

	public Vector3f getOldRotation()
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
        
        tag.putFloat(X_AXIS_ROTATION, rotation.x);
        tag.putFloat(Y_AXIS_ROTATION, rotation.y);
        tag.putFloat(Z_AXIS_ROTATION, rotation.z);

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
        
        this.rotation.x = nbt.getFloat(X_AXIS_ROTATION);
        this.rotation.y = nbt.getFloat(Y_AXIS_ROTATION);
        this.rotation.z = nbt.getFloat(Z_AXIS_ROTATION);
    }
}
