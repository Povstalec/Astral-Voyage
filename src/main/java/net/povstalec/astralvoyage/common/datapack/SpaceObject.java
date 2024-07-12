package net.povstalec.astralvoyage.common.datapack;

import java.util.*;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.povstalec.astralvoyage.common.data.SpaceObjects;
import org.joml.Vector3f;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.util.TextureLayerData;
public class SpaceObject
{
	public static final ResourceLocation SPACE_OBJECT_LOCATION = new ResourceLocation(AstralVoyage.MODID, "space_object");
	public static final ResourceKey<Registry<SpaceObject>> REGISTRY_KEY = ResourceKey.createRegistryKey(SPACE_OBJECT_LOCATION);
	public static final Codec<ResourceKey<SpaceObject>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);

	private static final Codec<Vector3f> GALACTIC_POS = Codec.FLOAT.listOf().comapFlatMap(f -> Util.fixedSize(f, 3).map(vec -> new Vector3f(vec.get(0), vec.get(1), vec.get(2))), (element) -> List.of(element.get(0), element.get(1), element.get(2))).stable();
	private static final Codec<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> PARENT = Codec.pair(RESOURCE_KEY_CODEC.fieldOf("parent_object").codec(), Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("orbit").codec());
	private static final Codec<Pair<List<Integer>, Boolean>> TEXTURE_SETTINGS = Codec.pair(Codec.INT.listOf().fieldOf("rgba").codec(), Codec.BOOL.fieldOf("blends").codec());
	private static final Codec<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> TEXTURE_LAYER = Codec.pair(ResourceLocation.CODEC.fieldOf("texture").codec(), TEXTURE_SETTINGS.fieldOf("texture_settings").codec());
    private static final Codec<Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>>> SURFACE_CODEC = Codec.pair(ResourceKey.codec(Registries.NOISE_SETTINGS).fieldOf("noise_settings").codec(), Codec.list(ResourceKey.codec(Registries.BIOME)).fieldOf("biomes").codec());

	public static final Codec<SpaceObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// Dimension this Stellar Location is tied to
			Level.RESOURCE_KEY_CODEC.optionalFieldOf("dimension").forGetter(SpaceObject::getDimension),
			// Translation name of the Stellar Location
			Codec.STRING.fieldOf("name").forGetter(SpaceObject::getTranslationName),
			Codec.FLOAT.fieldOf("size").forGetter(SpaceObject::getSize),
			GALACTIC_POS.optionalFieldOf("galactic_position").forGetter(SpaceObject::getGalacticPos),
			// Optional info for generating random objects
			SpaceObject.Generation.CODEC.optionalFieldOf("generation").forGetter(SpaceObject::getGeneration),
			// Parent Stellar Location, probably used for orbits and stuff in the future
			PARENT.optionalFieldOf("parent").forGetter(SpaceObject::getParentOrbitMap),
			// Textures and colors
			TEXTURE_LAYER.listOf().fieldOf("texture_layers").forGetter(SpaceObject::getTextureLayers),
            //Surface Settings
            SURFACE_CODEC.optionalFieldOf("surface").forGetter(SpaceObject::getSurface)
    ).apply(instance, SpaceObject::new));
	
	private static final String DISTANCE = "distance";
	private static final String ORBIT_DAYS = "orbit_days";
	private static final String ORBIT_START = "orbit_start";
	private static final String ORBIT_INCLINATION = "orbit_inclination";
	private static final String ROTATION = "rotation";
    public static final String SURFACE = "surface";
	
	private final Optional<ResourceKey<Level>> dimension;
	private final String translationName;
	private final float size;
	private final Optional<Vector3f> galactic_position;
	private final Optional<SpaceObject.Generation> generation;
	private final Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> parentOrbitMap;
	private final List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers;
    private final Optional<Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>>> surface;

	private Optional<ResourceKey<SpaceObject>> parent;
	// Orbital characteristics
	private Optional<Double> distance = Optional.empty(); // R
	private Optional<Double> orbitDays = Optional.empty(); // How many days it takes for the planet to complete one orbit
	private Optional<Double> orbitStart = Optional.empty(); // Phi
	private Optional<Double> orbitInclination = Optional.empty(); // Tetha
	private Optional<Double> rotation = Optional.empty();
	
	public SpaceObject(Optional<ResourceKey<Level>> dimension, String translationName, 
		float size, Optional<Vector3f> galactic_position, Optional<SpaceObject.Generation> generation,
		Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> parentOrbitMap, 
		List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers,
        Optional<Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>>> surface)
	{
		this.dimension = dimension;
		this.translationName = translationName;
		this.size = size;
		this.galactic_position = galactic_position;
		this.generation = generation;
		this.parentOrbitMap = parentOrbitMap;
		this.textureLayers = textureLayers;
        this.surface = surface;
		
		if(parentOrbitMap.isPresent())
		{
			this.parent = Optional.of(parentOrbitMap.get().getFirst());
			setupOrbit(parentOrbitMap.get().getSecond());
		}
		else
			this.parent = Optional.empty();
	}
	
	public Optional<ResourceKey<Level>> getDimension()
	{
		return this.dimension;
	}
	
	public String getTranslationName()
	{
		return this.translationName;
	}
	
	public float getSize()
	{
		return this.size;
	}

	public Optional<Vector3f> getGalacticPos(){
		return this.galactic_position;
	}

	public Optional<SpaceObject.Generation> getGeneration()
	{
		return this.generation;
	}
	
	private Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> getParentOrbitMap()
	{
		return this.parentOrbitMap;
	}

	public Optional<Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>>> getSurface() {
		return surface;
	}

	public Optional<ResourceKey<SpaceObject>> getParent()
	{
		return this.parent;
	}

	public void setParent(Optional<ResourceKey<SpaceObject>> parent)
	{
		this.parent = parent;
	}
	
	public List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> getTextureLayers()
	{
		return this.textureLayers;
	}

	private void setupOrbit(Map<String, Double> orbitMap)
	{
		if(orbitMap.containsKey(DISTANCE))
			this.distance = Optional.of(orbitMap.get(DISTANCE));
		
		if(orbitMap.containsKey(ORBIT_DAYS))
			this.orbitDays = Optional.of(orbitMap.get(ORBIT_DAYS));
		
		if(orbitMap.containsKey(ORBIT_START))
			this.orbitStart = Optional.of(orbitMap.get(ORBIT_START));
		
		if(orbitMap.containsKey(ORBIT_INCLINATION))
			this.orbitInclination = Optional.of(orbitMap.get(ORBIT_INCLINATION));
		
		if(orbitMap.containsKey(ROTATION))
			this.rotation = Optional.of(orbitMap.get(ROTATION));
	}
	
	public Optional<Double> getDistance()
	{
		return distance;
	}
	
	public Optional<Double> getAngularVelocity()
	{
		return orbitDays;
	}
	
	public Optional<Double> getOrbitOffset()
	{
		return orbitStart;
	}
	
	public Optional<Double> getOrbitInclination()
	{
		return orbitInclination;
	}
	
	public Optional<Double> getRotation()
	{
		return rotation;
	}

	public static class Serializable
	{
		private static final String OBJECT_KEY = "object_key";

		private static final String DIMENSION = "dimension";
		private static final String NAME = "name";
		private static final String SIZE = "size";
		private static final String PARENT = "parent";
		private static final String GENERATION = "generation";
		private static final String TEXTURE_LAYERS = "texture_layers";

		private final Optional<ResourceKey<SpaceObject>> objectKey;
		private final Optional<ResourceKey<Level>> dimension;
		private final Optional<String> name;
		private final Optional<Float> size;
		private final Optional<Vector3f> galactic_position;
		private final Optional<SpaceObject.Generation> generation;
        private final Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> parentOrbitMap;
		private List<ResourceKey<SpaceObject>> childObjects = new ArrayList<>();
		private final List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers;
		private final Optional<Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>>> surface;

		public Serializable(ResourceKey<SpaceObject> objectKey, SpaceObject object)
		{
			this.objectKey = Optional.of(objectKey);
			this.dimension = object.getDimension();
			this.name = Optional.of(object.getTranslationName());
			this.size = Optional.of(object.getSize());
			this.galactic_position = object.getGalacticPos();
            this.parentOrbitMap = object.getParentOrbitMap();
			this.generation = object.getGeneration();
			this.textureLayers = object.getTextureLayers();
			this.surface = object.getSurface();
		}

		public Serializable(Optional<ResourceKey<Level>> dimension, Optional<String> name, Optional<Float> size, Optional<Vector3f> galactic_position, Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> parentOrbitMap, Optional<SpaceObject.Generation> generation, List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers, Optional<Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>>> surface)
		{
			this.objectKey = Optional.empty();
			this.dimension = dimension;
			this.name = name;
			this.size = size;
			this.galactic_position = galactic_position;
			this.parentOrbitMap = parentOrbitMap;
			this.generation = generation;
			this.textureLayers = textureLayers;
			this.surface = surface;
		}

		public Optional<String> getName()
		{
			return this.name;
		}

		public Optional<ResourceKey<SpaceObject>> getKey()
		{
			return this.objectKey;
		}
		
		public Optional<Float> getSize()
		{
			return this.size;
		}

		public Optional<Vector3f> getGalacticPos()
		{
			return galactic_position;
		}

		public Optional<ResourceKey<Level>> getDimension()
		{
			return this.dimension;
		}

		public Optional<Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>>> getSurface()
		{
			return surface;
		}

		public Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> getOrbitMap()
        {
            return this.parentOrbitMap;
        }

		public List<ResourceKey<SpaceObject>> getChildObjects()
		{
			return this.childObjects;
		}

		public void addChild(ResourceKey<SpaceObject> child)
		{
			this.childObjects.add(child);
		}

		public void removeChild(ResourceKey<SpaceObject> child)
		{
			this.childObjects.remove(child);
		}

		public Optional<Generation> getGeneration()
		{
			return generation;
		}

		public List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> getTextureLayers()
		{
			return this.textureLayers;
		}

		public CompoundTag serialize()
		{
			CompoundTag objectTag = new CompoundTag();

			if(this.objectKey.isPresent()) {
				objectTag.putString(OBJECT_KEY, this.objectKey.get().location().toString());
			}
			else
			{
				if(this.getDimension().isPresent())
					objectTag.putString(DIMENSION, this.getDimension().get().location().toString());

				if(this.getName().isPresent())
					objectTag.putString(NAME, this.getName().get());

				if(this.getSize().isPresent())
					objectTag.putFloat(SIZE, this.getSize().get());

				if(this.generation.isPresent())
				{
					objectTag.put(GENERATION, generation.get().serialize());
				}

				if(this.getOrbitMap().isPresent())
				{
					CompoundTag orbitMap = new CompoundTag();
					orbitMap.putDouble(DISTANCE, this.getOrbitMap().get().getSecond().get(DISTANCE));
					orbitMap.putDouble(ORBIT_DAYS, this.getOrbitMap().get().getSecond().get(ORBIT_DAYS));
					orbitMap.putDouble(ORBIT_START, this.getOrbitMap().get().getSecond().get(ORBIT_START));
					orbitMap.putDouble(ORBIT_INCLINATION, this.getOrbitMap().get().getSecond().get(ORBIT_INCLINATION));
					orbitMap.putDouble(ROTATION, this.getOrbitMap().get().getSecond().get(ROTATION));

					objectTag.put("orbit", orbitMap);
				}

				if(this.getGalacticPos().isPresent())
				{
					CompoundTag galPos = new CompoundTag();
					galPos.putFloat("x", this.getGalacticPos().get().x);
					galPos.putFloat("y", this.getGalacticPos().get().y);
					galPos.putFloat("z", this.getGalacticPos().get().z);
					objectTag.put("galactic_position", galPos);
				}
				this.parentOrbitMap.ifPresent(spaceObjectResourceKey -> objectTag.putString(PARENT, spaceObjectResourceKey.getFirst().location().toString()));
				ListTag textureLayers = new ListTag();

				this.textureLayers.forEach(textureLayer -> textureLayers.add(TextureLayerData.serialize(new TextureLayerData(textureLayer))));
				objectTag.put(TEXTURE_LAYERS, textureLayers);
			}

			if (this.getSurface().isPresent()) {
				CompoundTag surfaceTag = new CompoundTag();
				this.getSurface().ifPresent(
						surface -> {
							surfaceTag.putString("noise_settings", surface.getFirst().location().toString());
							ListTag biomeList = new ListTag();
							surface.getSecond().forEach(biome -> biomeList.add(StringTag.valueOf(biome.location().toString())));
							surfaceTag.put("biomes", biomeList);
						});
				objectTag.put(SURFACE, surfaceTag);
			}

			return objectTag;
		}
		
		public static SpaceObject.Serializable deserialize(MinecraftServer server, Registry<SpaceObject> objectRegistry, CompoundTag objectTag)
		{
			if(objectTag.contains(OBJECT_KEY))
			{
				ResourceKey<SpaceObject> objectKey = stringToSpaceObjectKey(objectTag.getString(OBJECT_KEY));
				SpaceObject object = objectRegistry.get(objectKey);

				if(objectTag.contains(PARENT))
				{
					Serializable parent = SpaceObjects.get(server).spaceObjects.get(objectTag.getString(PARENT));
					if(parent != null && !parent.getChildObjects().contains(objectKey))
						parent.childObjects.add(objectKey);
				}

				return new SpaceObject.Serializable(objectKey, object);
			}
			else
			{
				Optional<String> name = Optional.empty();
				if(objectTag.contains(NAME))
					name = Optional.of(objectTag.getString(NAME));

				Optional<ResourceKey<Level>> dimension = Optional.empty();
				if(objectTag.contains(DIMENSION))
					dimension = Optional.of(stringToDimension(objectTag.getString(DIMENSION)));

				Optional<Float> size = Optional.empty();
				if(objectTag.contains(SIZE))
					size = Optional.of(objectTag.getFloat(SIZE));

				Optional<Map<String, Double>> orbitMap = Optional.empty();
				if(objectTag.contains("orbit"))
				{
					Map<String, Double> orbit = new HashMap<>(Map.of());
					CompoundTag tag = objectTag.getCompound("orbit");
					orbit.put(DISTANCE, tag.getDouble(DISTANCE));
					orbit.put(ORBIT_DAYS, tag.getDouble(ORBIT_DAYS));
					orbit.put(ORBIT_START, tag.getDouble(ORBIT_START));
					orbit.put(ORBIT_INCLINATION, tag.getDouble(ORBIT_INCLINATION));
					orbit.put(ROTATION, tag.getDouble(ROTATION));

					orbitMap = Optional.of(orbit);
				}

				Optional<Vector3f> galactic_position = Optional.empty();
				if(objectTag.contains("galactic_position")) {
					CompoundTag galPos = objectTag.getCompound("galactic_position");
					galactic_position = Optional.of(new Vector3f(galPos.getFloat("x"), galPos.getFloat("y"), galPos.getFloat("y")));
				}

				Optional<Generation> generation = Optional.empty();
				if(objectTag.contains(GENERATION))
				{
					CompoundTag generationTag = objectTag.getCompound(GENERATION);
					generation = Optional.of(Generation.deserialize(generationTag));
				}

				Optional<ResourceKey<SpaceObject>> parent = Optional.empty();
				if(objectTag.contains(PARENT))
					parent = Optional.ofNullable(stringToSpaceObjectKey(objectTag.getString(PARENT)));
				Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> parentOrbitMap = Optional.empty();
				if(parent.isPresent() && orbitMap.isPresent())
					parentOrbitMap = Optional.of(new Pair<>(parent.get(), orbitMap.get()));

				ListTag layersTag = objectTag.getList(TEXTURE_LAYERS, Tag.TAG_LIST);
				List<TextureLayerData> textureLayers = new ArrayList<>();
				layersTag.forEach(layertag -> textureLayers.add(TextureLayerData.deserialize((CompoundTag) layertag)));

				Optional<Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>>> surface = Optional.empty();
				if(objectTag.contains(SURFACE)) {
					CompoundTag surfaceTag = objectTag.getCompound(SURFACE);
					ResourceKey<NoiseGeneratorSettings> key = ResourceKey.create(Registries.NOISE_SETTINGS, ResourceLocation.tryParse(surfaceTag.getString("noise_settings")));

					List<ResourceKey<Biome>> biomeList = new ArrayList<>();
					ListTag biomesTag = surfaceTag.getList("biomes", Tag.TAG_STRING);
					biomesTag.forEach(
							biome -> {
								StringTag biomeTag = ((StringTag) biome);
								biomeList.add(ResourceKey.create(Registries.BIOME, ResourceLocation.tryParse(biomeTag.getAsString())));
							});

					surface = Optional.of(new Pair<>(key, biomeList));
				}
				return new SpaceObject.Serializable(dimension, name, size, galactic_position, parentOrbitMap, generation, TextureLayerData.toPairList(textureLayers), surface);
			}
		}
	}

	public static ResourceKey<SpaceObject> stringToSpaceObjectKey(String solarSystemString) {
		String[] split = solarSystemString.split(":");

		if (split.length > 1)
			return ResourceKey.create(SpaceObject.REGISTRY_KEY, new ResourceLocation(split[0], split[1]));

		return null;
	}

	public static ResourceKey<Level> stringToDimension(String dimensionString)
	{
		String[] split = dimensionString.split(":");

		if(split.length > 1)
			return ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "dimension")), new ResourceLocation(split[0], split[1]));

		return null;
	}
	
	
	
	public static final class Generation
	{
		public static final String ORBITING_OBJECT_COUNT = "orbiting_object_count";
		public static final String DISTANCE = "distance";
		public static final String DISTANCE_MIN = "min";
		public static final String DISTANCE_MAX = "max";

		private static final Codec<Pair<Float, Float>> DISTANCE_CODEC = Codec.pair(Codec.FLOAT.fieldOf(DISTANCE_MIN).codec(), Codec.FLOAT.fieldOf(DISTANCE_MAX).codec());

		public static final Codec<SpaceObject.Generation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				// Dimension this Stellar Location is tied to
				Codec.SHORT.fieldOf(ORBITING_OBJECT_COUNT).forGetter(SpaceObject.Generation::getOrbitingObjectCount),
				// Translation name of the Stellar Location
				DISTANCE_CODEC.fieldOf(DISTANCE).forGetter(SpaceObject.Generation::getGenerationDistance)
				// Surface dimension generation settings
		).apply(instance, SpaceObject.Generation::new));
		
		private short orbitingObjectCount;
		private Pair<Float, Float> generationDistance;
		
		public Generation(short orbitingObjectCount, Pair<Float, Float> generationDistance)
		{
			this.orbitingObjectCount = orbitingObjectCount;
			this.generationDistance = generationDistance;
		}
		
		public short getOrbitingObjectCount()
		{
			return this.orbitingObjectCount;
		}
		
		public Pair<Float, Float> getGenerationDistance()
		{
			return this.generationDistance;
		}

		public CompoundTag serialize()
		{
			CompoundTag tag = new CompoundTag();
			tag.putShort(ORBITING_OBJECT_COUNT, this.getOrbitingObjectCount());

			CompoundTag distanceRange = new CompoundTag();
			distanceRange.putFloat(DISTANCE_MIN, this.getGenerationDistance().getFirst());
			distanceRange.putFloat(DISTANCE_MAX, this.getGenerationDistance().getSecond());
			tag.put(DISTANCE, distanceRange);

			return tag;
		}

		public static Generation deserialize(CompoundTag tag)
		{
			short orbitingObjectCount = tag.getShort(ORBITING_OBJECT_COUNT);

			CompoundTag distanceTag = tag.getCompound(DISTANCE);
			float min = distanceTag.getFloat(DISTANCE_MIN);
			float max = distanceTag.getFloat(DISTANCE_MAX);
			Pair<Float, Float> distance = new Pair<>(min, max);

			return new Generation(orbitingObjectCount, distance);
		}
	}

}
