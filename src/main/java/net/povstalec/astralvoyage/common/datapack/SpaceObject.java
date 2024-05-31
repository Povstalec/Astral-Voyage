package net.povstalec.astralvoyage.common.datapack;

import java.util.*;

import net.povstalec.astralvoyage.common.data.SpaceObjects;
import org.joml.Vector3f;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.network.packets.TextureLayerData;

public class SpaceObject
{
	public static final ResourceLocation SPACE_OBJECT_LOCATION = new ResourceLocation(AstralVoyage.MODID, "space_object");
	public static final ResourceKey<Registry<SpaceObject>> REGISTRY_KEY = ResourceKey.createRegistryKey(SPACE_OBJECT_LOCATION);
	public static final Codec<ResourceKey<SpaceObject>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);

	private static final Codec<Vector3f> GALACTIC_POS = Codec.FLOAT.listOf().comapFlatMap(f -> Util.fixedSize(f, 3).map(vec -> new Vector3f(vec.get(0), vec.get(1), vec.get(2))), (element) -> List.of(element.get(0), element.get(1), element.get(2))).stable();
	private static final Codec<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> PARENT = Codec.pair(RESOURCE_KEY_CODEC.fieldOf("parent_object").codec(), Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("orbit").codec());
	private static final Codec<ResourceKey<SpaceObject>> CHILD_OBJECTS = RESOURCE_KEY_CODEC.fieldOf("child_object").codec();
	private static final Codec<Pair<List<Integer>, Boolean>> TEXTURE_SETTINGS = Codec.pair(Codec.INT.listOf().fieldOf("rgba").codec(), Codec.BOOL.fieldOf("blends").codec());
	private static final Codec<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> TEXTURE_LAYER = Codec.pair(ResourceLocation.CODEC.fieldOf("texture").codec(), TEXTURE_SETTINGS.fieldOf("texture_settings").codec());

	public static final Codec<SpaceObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// Dimension this Stellar Location is tied to
			Level.RESOURCE_KEY_CODEC.optionalFieldOf("dimension").forGetter(SpaceObject::getDimension),
			// Translation name of the Stellar Location
			Codec.STRING.fieldOf("name").forGetter(SpaceObject::getTranslationName),
			Codec.FLOAT.fieldOf("size").forGetter(SpaceObject::getSize),
			GALACTIC_POS.optionalFieldOf("galactic_position").forGetter(SpaceObject::getGalacticPos),
			CHILD_OBJECTS.listOf().fieldOf("children").forGetter(SpaceObject::getChildObjects),
			// Optional info for generating random objects
			SpaceObject.Generation.CODEC.optionalFieldOf("generation").forGetter(SpaceObject::getGeneration),
			// Parent Stellar Location, probably used for orbits and stuff in the future
			PARENT.optionalFieldOf("parent").forGetter(SpaceObject::getParentOrbitMap),
			// Textures and colors
			TEXTURE_LAYER.listOf().fieldOf("texture_layers").forGetter(SpaceObject::getTextureLayers)
			).apply(instance, SpaceObject::new));
	
	private static final String DISTANCE = "distance";
	private static final String ORBIT_DAYS = "orbit_days";
	private static final String ORBIT_START = "orbit_start";
	private static final String ORBIT_INCLINATION = "orbit_inclination";
	private static final String ROTATION = "rotation";
	
	private final Optional<ResourceKey<Level>> dimension;
	private final String translationName;
	private final float size;
	private final Optional<Vector3f> galactic_position;
	private final List<ResourceKey<SpaceObject>> childObjects;
	private final Optional<SpaceObject.Generation> generation;
	private final Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> parentOrbitMap;
	private final List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers;

	private Optional<ResourceKey<SpaceObject>> parent;
	// Orbital characteristics
	private Optional<Double> distance = Optional.empty(); // R
	private Optional<Double> orbitDays = Optional.empty(); // How many days it takes for the planet to complete one orbit
	private Optional<Double> orbitStart = Optional.empty(); // Phi
	private Optional<Double> orbitInclination = Optional.empty(); // Tetha
	private Optional<Double> rotation = Optional.empty();
	
	public SpaceObject(Optional<ResourceKey<Level>> dimension, String translationName, 
		float size, Optional<Vector3f> galactic_position, List<ResourceKey<SpaceObject>> childObjects, Optional<SpaceObject.Generation> generation,
		Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> parentOrbitMap, 
		List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers)
	{
		this.dimension = dimension;
		this.translationName = translationName;
		this.size = size;
		this.galactic_position = galactic_position;
		this.childObjects = childObjects;
		this.generation = generation;
		this.parentOrbitMap = parentOrbitMap;
		this.textureLayers = textureLayers;
		
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

	public List<ResourceKey<SpaceObject>> getChildObjects()
	{
		return this.childObjects;
	}

	public Optional<SpaceObject.Generation> getGeneration()
	{
		return this.generation;
	}
	
	private Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> getParentOrbitMap()
	{
		return this.parentOrbitMap;
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
		private static final String CHILD_OBJECTS = "child_objects";
		private static final String TEXTURE_LAYERS = "texture_layers";

		private final Optional<ResourceKey<SpaceObject>> objectKey;
		private final Optional<ResourceKey<Level>> dimension;
		private final Optional<String> name;
		private final Optional<Float> size;
		private final Optional<Vector3f> galactic_position;
		private final Optional<ResourceKey<SpaceObject>> parent;
		private final Optional<SpaceObject.Generation> generation;
		private final List<ResourceKey<SpaceObject>> childObjects;
		private final List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers;

		public Serializable(ResourceKey<SpaceObject> objectKey, SpaceObject object)
		{
			this.objectKey = Optional.of(objectKey);
			this.dimension = object.getDimension();
			this.name = Optional.of(object.getTranslationName());
			this.size = Optional.of(object.getSize());
			this.galactic_position = object.getGalacticPos();
			this.parent = object.getParent();
			this.generation = object.getGeneration();
			this.childObjects = object.getChildObjects();
			this.textureLayers = object.getTextureLayers();
		}

		public Serializable(ResourceKey<Level> dimension, String name, float size, Optional<Vector3f> galactic_position, Optional<ResourceKey<SpaceObject>> parent, Optional<SpaceObject.Generation> generation, List<ResourceKey<SpaceObject>> childObjects, List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers)
		{
			this.objectKey = Optional.empty();
			this.dimension = Optional.ofNullable(dimension);
			this.name = Optional.of(name);
			this.size = Optional.of(size);
			this.galactic_position = galactic_position;
			this.parent = parent;
			this.generation = generation;
			this.childObjects = childObjects;
			this.textureLayers = textureLayers;
		}

		public String getName()
		{
			return this.name.get();
		}
		
		public float getSize()
		{
			return this.size.get();
		}

		public Optional<Vector3f> getGalacticPos()
		{
			return galactic_position;
		}

		public ResourceKey<Level> getDimension()
		{
			return this.dimension.get();
		}

		public Optional<ResourceKey<SpaceObject>> getParent()
		{
			return this.parent;
		}

		public List<ResourceKey<SpaceObject>> getChildObjects()
		{
			return this.childObjects;
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

				if(this.generation.isPresent())
				{
					CompoundTag generation = new CompoundTag();
					generation.putShort("count", this.generation.get().getOrbitingObjectCount());
					generation.putFloat("distance_min", this.generation.get().getGenerationDistance().getFirst());
					generation.putFloat("distance_max", this.generation.get().getGenerationDistance().getSecond());
					objectTag.put(GENERATION, generation);
				}
			}
			else
			{
				objectTag.putString(DIMENSION, this.dimension.get().location().toString());
				objectTag.putString(NAME, this.name.get());
				objectTag.putFloat(SIZE, this.size.get());

				if(galactic_position.isPresent())
				{
					CompoundTag galPos = new CompoundTag();
					galPos.putFloat("x", galactic_position.get().x);
					galPos.putFloat("y", galactic_position.get().y);
					galPos.putFloat("z", galactic_position.get().z);
					objectTag.put("galactic_position", galPos);
				}
				if(!childObjects.isEmpty())
				{
					ListTag childObjects = new ListTag();
					this.childObjects.forEach(child -> childObjects.add(StringTag.valueOf(child.location().toString())));
					objectTag.put(CHILD_OBJECTS, childObjects);
				}
				this.parent.ifPresent(spaceObjectResourceKey -> objectTag.putString(PARENT, spaceObjectResourceKey.location().toString()));
				ListTag textureLayers = new ListTag();

				this.textureLayers.forEach(textureLayer -> {
					textureLayers.add(TextureLayerData.serialize(new TextureLayerData(textureLayer)));
				});
				objectTag.put(TEXTURE_LAYERS, textureLayers);
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
					SpaceObject parent = server.registryAccess().registryOrThrow(REGISTRY_KEY).get(stringToSpaceObjectKey(objectTag.getString(PARENT)));
					if(parent != null && !parent.getChildObjects().contains(objectKey))
						parent.childObjects.add(objectKey);
				}

				return new SpaceObject.Serializable(objectKey, object);
			}
			else
			{
				String name = objectTag.getString(NAME);
				ResourceKey<Level> dimension = stringToDimension(objectTag.getString(DIMENSION));
				float size = objectTag.getFloat(SIZE);

				Optional<Vector3f> galactic_position = Optional.empty();
				if(objectTag.contains("galactic_position")) {
					CompoundTag galPos = objectTag.getCompound("galactic_position");
					galactic_position = Optional.of(new Vector3f(galPos.getFloat("x"), galPos.getFloat("y"), galPos.getFloat("y")));
				}

				Optional<Generation> generation = Optional.empty();
				if(objectTag.contains(GENERATION))
				{
					CompoundTag generationTag = objectTag.getCompound(GENERATION);
					generation = Optional.of(new Generation(generationTag.getShort("count"), new Pair(generationTag.getFloat("distance_min"), generationTag.getFloat("distance_max"))));
				}

				Optional<ResourceKey<SpaceObject>> parent = Optional.empty();
				if(objectTag.contains(PARENT))
					parent = Optional.ofNullable(stringToSpaceObjectKey(objectTag.getString(PARENT)));

				List<ResourceKey<SpaceObject>> childObjects = new ArrayList<>();
				if(objectTag.contains(CHILD_OBJECTS)){
					ListTag childObjectsTag = objectTag.getList(CHILD_OBJECTS, Tag.TAG_STRING);
					childObjectsTag.forEach(childObject -> childObjects.add(stringToSpaceObjectKey(childObject.getAsString())));
				}

				ListTag layersTag = objectTag.getList(TEXTURE_LAYERS, Tag.TAG_LIST);
				List<TextureLayerData> textureLayers = new ArrayList<>();
				layersTag.forEach(layertag -> textureLayers.add(TextureLayerData.deserialize((CompoundTag) layertag)));

				return new SpaceObject.Serializable(dimension, name, size, galactic_position, parent, generation, childObjects, TextureLayerData.toPairList(textureLayers));
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
		private static final Codec<Pair<Float, Float>> DISTANCE = Codec.pair(Codec.FLOAT.fieldOf("min").codec(), Codec.FLOAT.fieldOf("max").codec());
		
		public static final Codec<SpaceObject.Generation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				// Dimension this Stellar Location is tied to
				Codec.SHORT.fieldOf("orbiting_object_count").forGetter(SpaceObject.Generation::getOrbitingObjectCount),
				// Translation name of the Stellar Location
				DISTANCE.fieldOf("distance").forGetter(SpaceObject.Generation::getGenerationDistance)
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
	}

}
