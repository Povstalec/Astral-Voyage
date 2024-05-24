package net.povstalec.astralvoyage.common.capability;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.astralvoyage.common.datapack.ClientSpaceObject;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.network.packets.RenderObjectUpdateMessage;
import org.joml.Vector3f;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.povstalec.astralvoyage.common.network.AVNetwork;
import net.povstalec.astralvoyage.common.network.packets.SpaceObjectUpdateMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private static final String RENDER_OBJECTS = "render_objects";

	private Vector3f galacticPosition = new Vector3f(0, 0, 0);
    private Vector3f solarPosition = new Vector3f(0, 0, 0);
	private Vector3f rotation = new Vector3f(0, 0, 0);

    private List<ClientSpaceObject> renderObjects = Lists.newLinkedList();

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
            		new SpaceObjectUpdateMessage(solarPosition.x, solarPosition.y, solarPosition.z,
            				galacticPosition.x, galacticPosition.y, galacticPosition.z, 
            				rotation.x, rotation.y, rotation.z));
            AVNetwork.sendPacketToDimension(level.dimension(),
                    new RenderObjectUpdateMessage(renderObjects));
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

    public List<ClientSpaceObject> getRenderObjects()
    {
        return renderObjects;
    }

	public Vector3f getGalacticPosition()
	{
		return galacticPosition;
	}

	public Vector3f getRotation()
	{
		return rotation;
	}

    public void setRenderObjects(List<ClientSpaceObject> renderObjects) {
        this.renderObjects = renderObjects;
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

        ListTag renderObjects = new ListTag();
        this.renderObjects.forEach(object -> {
            renderObjects.add(object.serialize());
        });
        tag.put(RENDER_OBJECTS, renderObjects);
        
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

        nbt.getList(RENDER_OBJECTS, ListTag.TAG_LIST).forEach(tag -> {
            CompoundTag objectTag = ((CompoundTag) tag);
            CompoundTag solarPosTag = (CompoundTag) objectTag.get("solar_pos");

            Vector3f solarPos = new Vector3f(solarPosTag.getFloat("x"), solarPosTag.getFloat("y"), solarPosTag.getFloat("z"));
            ListTag layersTag = objectTag.getList("texture_layers", Tag.TAG_LIST);
            List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers = new ArrayList<>();
            layersTag.forEach(layertag -> {
                CompoundTag layerTag = (CompoundTag) layertag;
                CompoundTag textureSettingsTag = layerTag.getCompound("texture_settings");
                Pair<List<Integer>, Boolean> textureSettings = new Pair<>(Arrays.stream(textureSettingsTag.getIntArray("rgba")).boxed().collect(Collectors.toList()), textureSettingsTag.getBoolean("blend"));
                Pair<ResourceLocation, Pair<List<Integer>, Boolean>> layer = new Pair(ResourceLocation.tryParse(layerTag.getString("texture")), textureSettings);
                textureLayers.add(layer);
            });

            this.renderObjects.add(new ClientSpaceObject(SpaceObject.stringToSpaceObjectKey(objectTag.getString("key")),solarPos, textureLayers));
        });
    }
}