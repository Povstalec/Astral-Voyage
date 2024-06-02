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
import org.apache.commons.compress.utils.Lists;

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

	private void registerSpaceObjectFromDataPacks(MinecraftServer server)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<SpaceObject> objectRegistry = registries.registryOrThrow(SpaceObject.REGISTRY_KEY);

		Set<Map.Entry<ResourceKey<SpaceObject>, SpaceObject>> objectSet = objectRegistry.entrySet();

		//Goes through all datapack Space Objects
		objectSet.forEach((object) ->
				addSpaceObjectFromDataPack(server, object.getKey(), object.getValue()));
		AstralVoyage.LOGGER.info("Datapack Space Objects registered");
	}

	private void addSpaceObjectFromDataPack(MinecraftServer server, ResourceKey<SpaceObject> spaceObjectKey, SpaceObject spaceObject)
	{
		if(spaceObject.getGeneration().isPresent())
		{
			SpaceObject newObject = new SpaceObject(Optional.empty(), AstralVoyage.MODID+":body_"+UUID.randomUUID(), 13000, Optional.empty(), new ArrayList<>(), Optional.empty(), Optional.of(new Pair<>(spaceObjectKey, Map.of("distance", ((double) new Random().nextInt(spaceObject.getGeneration().get().getGenerationDistance().getFirst().intValue(), spaceObject.getGeneration().get().getGenerationDistance().getSecond().intValue()))))), Collections.singletonList(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/planets/earth"), new Pair<>(List.of(255, 255, 255, 255), false))));
			//spaceObject.childObjects.add(stringToSpaceObjectKey(newObject.getTranslationName()));
			//spaceObject.childObjects = Collections.singletonList(stringToSpaceObjectKey(newObject.getTranslationName()));
			saveSpaceObject(new SpaceObject.Serializable(stringToSpaceObjectKey(newObject.getTranslationName()), newObject));
		}

		SpaceObject.Serializable object = new SpaceObject.Serializable(spaceObjectKey, spaceObject);
		if(object.getGeneration().isPresent())
		{
			String id = AstralVoyage.MODID + ":body_" + UUID.randomUUID();
			SpaceObject.Serializable newObject = new SpaceObject.Serializable(Optional.of(stringToSpaceObjectKey(id)),Optional.empty(),
					id, 13000, Optional.empty(),
					Optional.of(new Pair<>(spaceObjectKey, Map.of("distance", ((double) new Random().nextInt(spaceObject.getGeneration().get().getGenerationDistance().getFirst().intValue(), spaceObject.getGeneration().get().getGenerationDistance().getSecond().intValue()))))),
					Optional.empty(), new ArrayList<>(), Collections.singletonList(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/planets/earth"), new Pair<>(List.of(255, 255, 255, 255), false))));
			object.getChildObjects().add(newObject.getKey());
			saveSpaceObject(newObject);
		}
		saveSpaceObject(object);
	}

	private boolean saveSpaceObject(SpaceObject.Serializable spaceObject)
	{
		String spaceObjectName = spaceObject.getKey().location().toString();

		this.spaceObjects.put(spaceObjectName, spaceObject);

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
