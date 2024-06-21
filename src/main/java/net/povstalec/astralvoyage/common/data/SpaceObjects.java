package net.povstalec.astralvoyage.common.data;

import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.util.RandomTextureLayers;
import org.apache.commons.compress.utils.Lists;
import org.joml.Vector3f;

import java.util.*;

import static net.povstalec.astralvoyage.common.datapack.SpaceObject.stringToSpaceObjectKey;

public class SpaceObjects extends SavedData
{
	private static final String FILE_NAME = AstralVoyage.MODID + "-space_objects";

	private static final String SPACE_OBJECTS = "space_objects";

	public HashMap<String, SpaceObject.Serializable> spaceObjects = new HashMap<>();
	
	private MinecraftServer server;


	public final void updateData(MinecraftServer server){
		SpaceObjects.get(server).eraseData(server);
		SpaceObjects.get(server).generateData(server);
	}

	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================

	public void eraseData(MinecraftServer server)
	{
		this.spaceObjects.clear();
		this.setDirty();
	}

	public void generateData(MinecraftServer server)
	{
		for (int i = 0; i <= 30; i++) {
			registerRandomSpaceObjects();
		}
		registerSpaceObjectFromDataPacks(server);
	}

	private CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.put(SPACE_OBJECTS, serializeSpaceObjects());
		
		return tag;
	}

	private CompoundTag serializeSpaceObjects(){
		CompoundTag spaceObjectsTag = new CompoundTag();

		this.spaceObjects.forEach((objectID, spaceObject) -> spaceObjectsTag.put(objectID, spaceObject.serialize()));

		return spaceObjectsTag;
	}
	
	private void deserialize(CompoundTag tag)
	{
		deserializeSpaceObjects(tag);
	}

	private void deserializeSpaceObjects(CompoundTag tag){
		final RegistryAccess registries = server.registryAccess();
		final Registry<SpaceObject> objectRegistry = registries.registryOrThrow(SpaceObject.REGISTRY_KEY);

		tag.getAllKeys().forEach(objectString -> {
			SpaceObject.Serializable spaceObject = SpaceObject.Serializable.deserialize(server, objectRegistry, tag.getCompound(objectString));
			this.spaceObjects.put(objectString, spaceObject);
		});

	}
	
	//============================================================================================
	//********************************************Data********************************************
	//============================================================================================

	private void registerRandomSpaceObjects()
	{
		Random random = new Random();
		RandomTextureLayers.Star layer = RandomTextureLayers.Star.values()[random.nextInt(0, 7)];
		List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> layerList = List.of(layer.getTextureLayer().getFirst().getLayer(), layer.getTextureLayer().getSecond().getLayer());
		String id = AstralVoyage.MODID + ":star_" + UUID.randomUUID();
		SpaceObject.Serializable newObject = new SpaceObject.Serializable(Optional.of(stringToSpaceObjectKey(id)),
				Optional.empty(), id, 13000, Optional.of(new Vector3f(((int) random.nextFloat(2f, 1000f)), ((int) random.nextFloat(2f, 1000f)), ((int) random.nextFloat(2f, 1000f)))), Optional.empty(),
				Optional.of(new SpaceObject.Generation((short) random.nextInt(0, 11), new Pair<>(random.nextFloat(128000, 18900000), random.nextFloat(128900000, 1897500000000f)))),
				layerList);
		saveSpaceObject(newObject.getKey(), newObject);
	}

	private void registerSpaceObjectFromDataPacks(MinecraftServer server)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<SpaceObject> objectRegistry = registries.registryOrThrow(SpaceObject.REGISTRY_KEY);

		Set<Map.Entry<ResourceKey<SpaceObject>, SpaceObject>> objectSet = objectRegistry.entrySet();

		//Goes through all datapack Space Objects
		objectSet.stream().sorted(Comparator.comparing(obj -> obj.getValue().getParent().isPresent())).forEach((object ->
				addSpaceObjectFromDataPack(server, object.getKey(), object.getValue())));
		AstralVoyage.LOGGER.info("Datapack Space Objects registered");
	}

	private void addSpaceObjectFromDataPack(MinecraftServer server, ResourceKey<SpaceObject> spaceObjectKey, SpaceObject spaceObject)
	{
		SpaceObject.Serializable object = new SpaceObject.Serializable(spaceObjectKey, spaceObject);
		saveSpaceObject(spaceObjectKey, object);
	}

	private boolean saveSpaceObject(ResourceKey<SpaceObject> spaceObjectKey, SpaceObject.Serializable object)
	{
		String spaceObjectName = object.getKey().location().toString();
		if(object.getOrbitMap().isPresent())
		{
			SpaceObject.Serializable parentObject = spaceObjects.get(object.getOrbitMap().get().getFirst().location().toString());
			if(parentObject != null)
				parentObject.addChild(object.getKey());
		}
		if(object.getGeneration().isPresent())
		{
			for(int i = 0; i<object.getGeneration().get().getOrbitingObjectCount(); i++)
			{
				Random random = new Random();
				RandomTextureLayers.Planet[] values = RandomTextureLayers.Planet.values();
				List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> layerList = List.of(values[random.nextInt(0, 8)].getTextureLayer(), values[random.nextInt(8, 16)].getTextureLayer());
				String id = AstralVoyage.MODID + ":body_" + UUID.randomUUID();
				SpaceObject.Serializable newObject = new SpaceObject.Serializable(Optional.of(stringToSpaceObjectKey(id)),Optional.empty(),
						id, 13000, Optional.empty(),
						Optional.of(new Pair<>(object.getKey(), Map.of("distance", ((double) new Random().nextInt(object.getGeneration().get().getGenerationDistance().getFirst().intValue(), object.getGeneration().get().getGenerationDistance().getSecond().intValue()))))),
						Optional.empty(), layerList);
				object.addChild(newObject.getKey());
				saveSpaceObject(newObject.getKey(), newObject);
			}
		}
		this.spaceObjects.put(spaceObjectName, object);

		return true;
	}

	public SpaceObjects(MinecraftServer server)
	{
		this.server = server;
	}
	
	public static SpaceObjects create(MinecraftServer server)
	{
		return new SpaceObjects(server);
	}
	
	public static SpaceObjects load(MinecraftServer server, CompoundTag tag)
	{
		SpaceObjects data = create(server);

		data.server = server;
		data.deserialize(tag);
		
		return data;
	}
	
	public CompoundTag save(CompoundTag tag)
	{
		tag = serialize();
		
		return tag;
	}
	
	@Nonnull
	public static SpaceObjects get(Level level)
	{
		if(level.isClientSide())
			throw new RuntimeException("Don't access this client-side!");
		
		return SpaceObjects.get(level.getServer());
	}
	
	@Nonnull
	public static SpaceObjects get(MinecraftServer server)
	{
		DimensionDataStorage storage = server.overworld().getDataStorage();
		
		return storage.computeIfAbsent((tag) -> load(server, tag), () -> create(server), FILE_NAME);
	}
}
