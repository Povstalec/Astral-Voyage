package net.povstalec.astralvoyage.common.data;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.joml.Vector3f;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.datapack.StarType;
import net.povstalec.astralvoyage.common.util.RandomTextureLayers;
import net.povstalec.astralvoyage.common.util.SpectralClass;
import net.povstalec.astralvoyage.common.util.StarProperties;
import net.povstalec.astralvoyage.common.util.TextureLayerData;

public class SpaceObjects extends SavedData
{
	private static final String FILE_NAME = AstralVoyage.MODID + "-space_objects";

	private static final String SPACE_OBJECTS = "space_objects";

	private static final int MAX_STARS = 500;
	private static final int MIN_STARS = 50;
	private static final int MIN_PLANETS_PER_STAR = 0;
	private static final int MAX_PLANETS_PER_STAR = 5;

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
		Random random = new Random();

		for (int i = 0; i <= random.nextInt(MIN_STARS, MAX_STARS); i++) {
			registerRandomSpaceObjects(random);
		}
		registerSpaceObjectFromDataPacks(server);

		this.setDirty();
	}

	private CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.put(SPACE_OBJECTS, serializeSpaceObjects());
		
		return tag;
	}

	private CompoundTag serializeSpaceObjects()
	{
		CompoundTag spaceObjectsTag = new CompoundTag();

		this.spaceObjects.forEach((objectID, spaceObject) -> spaceObjectsTag.put(objectID, spaceObject.serialize()));

		return spaceObjectsTag;
	}
	
	private void deserialize(CompoundTag tag)
	{
		deserializeSpaceObjects(tag);
	}

	private void deserializeSpaceObjects(CompoundTag tag)
	{
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

	private void registerRandomSpaceObjects(Random random)
	{
		StarType type = StarType.randomType(random);
		StarProperties properties = type.getStarProperties();
		SpectralClass spectralClass = null;
		for(SpectralClass spectral : SpectralClass.values()) {
			if (properties.getMass() >= spectral.getMassRange().getFirst() && properties.getMass() < spectral.getMassRange().getSecond())
				spectralClass = spectral;
		}

		List<TextureLayerData> layerList;
		if(spectralClass != null)
			layerList = spectralClass.getLayers();
		else layerList = RandomTextureLayers.Star.values()[random.nextInt(0, 7)].getTextureLayer();
		String id = AstralVoyage.MODID + ":star_" + UUID.randomUUID();
		SpaceObject.Serializable newObject = new SpaceObject.Serializable(null, id,
				spectralClass == null ?
						13000F : random.nextFloat(spectralClass.getRadiusRange().getFirst().floatValue(),
						spectralClass.getRadiusRange().getSecond().floatValue()), type,
				new Vector3f(
						((int) random.nextFloat(-1000f, 1000f)),
						((int) random.nextFloat(-1000f, 1000f)),
						((int) random.nextFloat(-1000f, 1000f))),
				null,
				new SpaceObject.Generation(
						(short) random.nextInt(MIN_PLANETS_PER_STAR, MAX_PLANETS_PER_STAR),
						new Pair<>(random.nextFloat(128000, 18900000),
								random.nextFloat(128900000, 1897500000000f))),
				layerList, null);
		saveSpaceObject(newObject);
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
		saveSpaceObject(object);
	}

	private boolean saveSpaceObject(SpaceObject.Serializable object)
	{
		String spaceObjectName = AstralVoyage.MODID + ":empty";
		if(object.getName() != null)
			spaceObjectName = object.getName();
		if(object.getKey() != null)
			spaceObjectName = object.getKey().location().toString();

		if(object.getOrbitMap() != null)
		{
			SpaceObject.Serializable parentObject = spaceObjects.get(object.getOrbitMap().getFirst().location().toString());
			if(parentObject != null)
				parentObject.addChild(SpaceObject.stringToSpaceObjectKey(spaceObjectName));
		}

		if(object.getGeneration() != null)
		{
			for(int i = 0; i<object.getGeneration().getOrbitingObjectCount(); i++)
			{
				Random random = new Random();
				RandomTextureLayers.Planet[] values = RandomTextureLayers.Planet.values();
				List<TextureLayerData> layerList = List.of(values[random.nextInt(0, 8)].getTextureLayer(), values[random.nextInt(8, 16)].getTextureLayer());
				String id = AstralVoyage.MODID + ":body_" + UUID.randomUUID();
				SpaceObject.Serializable newObject = new SpaceObject.Serializable(
						null, id, 13000F, null, null,
						new Pair<>(SpaceObject.stringToSpaceObjectKey(spaceObjectName),
						Map.of("distance", ((double) new Random().nextInt(object.getGeneration().getGenerationDistance().getFirst().intValue(),
						object.getGeneration().getGenerationDistance().getSecond().intValue())),
								"orbit_days", 0d, "orbit_start", 0d, "orbit_inclination", 0d, "rotation", 0d)),
						null, layerList, null);
				object.addChild(SpaceObject.stringToSpaceObjectKey(id));
				saveSpaceObject(newObject);
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
