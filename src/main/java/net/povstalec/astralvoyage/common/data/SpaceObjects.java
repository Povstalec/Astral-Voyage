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
			System.out.print(spaceObject.getName()+spaceObject.getGeneration().isPresent());
			if(spaceObject.getGeneration().isPresent())
			{
				SpaceObject newObject = new SpaceObject(Optional.empty(), AstralVoyage.MODID+":body_"+UUID.randomUUID(), 13000, Optional.empty(), new ArrayList<>(), Optional.empty(), Optional.of(new Pair(stringToSpaceObjectKey(objectString), Map.of("distance", ((double) new Random().nextInt(spaceObject.getGeneration().get().getGenerationDistance().getFirst().intValue(), spaceObject.getGeneration().get().getGenerationDistance().getSecond().intValue()))))), new ArrayList<>((Collection<? extends Pair<ResourceLocation, Pair<List<Integer>, Boolean>>>) new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures:planets/earth"), new Pair<Object, Boolean>(new int[]{255, 255, 255, 255}, false))));
				this.spaceObjects.put(newObject.getTranslationName(), new SpaceObject.Serializable(stringToSpaceObjectKey(newObject.getTranslationName()), newObject));
			}
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
		SpaceObject.Serializable object = new SpaceObject.Serializable(spaceObjectKey, spaceObject);
		saveSpaceObject(object);
	}

	private boolean saveSpaceObject(SpaceObject.Serializable spaceObject)
	{
		String spaceObjectName = spaceObject.getName();

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
