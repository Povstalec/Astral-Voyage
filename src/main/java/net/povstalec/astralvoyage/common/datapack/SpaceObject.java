package net.povstalec.astralvoyage.common.datapack;

import java.util.*;
import java.util.function.Function;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.astralvoyage.common.data.SpaceObjects;
import net.povstalec.astralvoyage.common.init.SpaceObjectTypeInit;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpaceObject
{
	public static final ResourceLocation SPACE_OBJECT_LOCATION = new ResourceLocation(AstralVoyage.MODID, "space_object");
	public static final ResourceKey<Registry<SpaceObject>> REGISTRY_KEY = ResourceKey.createRegistryKey(SPACE_OBJECT_LOCATION);
	public static final Codec<ResourceKey<SpaceObject>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);

	private static final Codec<Vector3f> GALACTIC_POS = Codec.FLOAT.listOf().comapFlatMap(f -> Util.fixedSize(f, 3).map(vec -> new Vector3f(vec.get(0), vec.get(1), vec.get(2))), (element) -> List.of(element.get(0), element.get(1), element.get(2))).stable();
	private static final Codec<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> PARENT = Codec.pair(RESOURCE_KEY_CODEC.fieldOf("parent_object").codec(), Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("orbit").codec());
	private static final Codec<Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>>> SURFACE_CODEC = Codec.pair(ResourceKey.codec(Registries.NOISE_SETTINGS).fieldOf("noise_settings").codec(), Codec.list(ResourceKey.codec(Registries.BIOME)).fieldOf("biomes").codec());

	public static final Codec<SpaceObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// Dimension this Stellar Location is tied to
			Level.RESOURCE_KEY_CODEC.optionalFieldOf("dimension").forGetter(SpaceObject::getDimension),
			// Translation name of the Stellar Location
			Codec.STRING.fieldOf("name").forGetter(SpaceObject::getTranslationName),
			Codec.FLOAT.fieldOf("size").forGetter(SpaceObject::getSize),
            //Space Object Type for extra data.
            SpaceObjectTypeInit.OBJECT_TYPE_DISPATCHER.dispatchedCodec().optionalFieldOf("object_type").forGetter(SpaceObject::getType),
            //Position in the galaxy
			GALACTIC_POS.optionalFieldOf("galactic_position").forGetter(SpaceObject::getGalacticPos),
			// Optional info for generating random objects
			SpaceObject.Generation.CODEC.optionalFieldOf("generation").forGetter(SpaceObject::getGeneration),
			// Parent Stellar Location, probably used for orbits and stuff in the future
			PARENT.optionalFieldOf("parent").forGetter(SpaceObject::getParentOrbitMap),
			// Textures and colors
			TextureLayerData.CODEC.listOf().fieldOf("texture_layers").forGetter(SpaceObject::getTextureLayers),
            //Surface Settings
            SURFACE_CODEC.optionalFieldOf("surface").forGetter(SpaceObject::getSurface)
    ).apply(instance, SpaceObject::new));
	
	private static final String DISTANCE = "distance";
	private static final String ORBIT_DAYS = "orbit_days";
	private static final String ORBIT_START = "orbit_start";
	private static final String ORBIT_INCLINATION = "orbit_inclination";
	private static final String ROTATION = "rotation";
    public static final String SURFACE = "surface";

    @Nullable private final ResourceKey<Level> dimension;
	private final String translationName;
	private final float size;
    @Nullable private final SpaceObjectType type;
    @Nullable private final Vector3f galactic_position;
    @Nullable private final SpaceObject.Generation generation;
    @Nullable private final Pair<ResourceKey<SpaceObject>, Map<String, Double>> parentOrbitMap;
	private final List<TextureLayerData> textureLayers;
    @Nullable private final Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>> surface;

	@Nullable private ResourceKey<SpaceObject> parent;
	// Orbital characteristics
	@Nullable private Double distance = null; // R
	@Nullable private Double orbitDays = null; // How many days it takes for the planet to complete one orbit
	@Nullable private Double orbitStart = null; // Phi
	@Nullable private Double orbitInclination = null; // Tetha
	@Nullable private Double rotation = null;
	
	public SpaceObject(Optional<ResourceKey<Level>> dimension, String translationName,
                       float size,  Optional<SpaceObjectType> type, Optional<Vector3f> galactic_position, Optional<SpaceObject.Generation> generation,
                       Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> parentOrbitMap,
                       List<TextureLayerData> textureLayers,
                       Optional<Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>>> surface)
	{
		this.dimension = dimension.orElse(null);
		this.translationName = translationName;
		this.size = size;
        this.type = type.orElse(null);
        this.galactic_position = galactic_position.orElse(null);
		this.generation = generation.orElse(null);
		this.parentOrbitMap = parentOrbitMap.orElse(null);
		this.textureLayers = textureLayers;
        this.surface = surface.orElse(null);
		
		if(parentOrbitMap.isPresent())
		{
			this.parent = parentOrbitMap.get().getFirst();
			setupOrbit(parentOrbitMap.get().getSecond());
		}
		else
			this.parent = null;
	}

	public Optional<ResourceKey<Level>> getDimension()
	{
		return Optional.ofNullable(this.dimension);
	}
	
	public String getTranslationName()
	{
		return this.translationName;
	}

    @Nullable
    public Optional<SpaceObjectType> getType() {
        if(this.type != null)
            return Optional.of(this.type);
        else return Optional.empty();
    }

    public float getSize()
	{
		return this.size;
	}

	public Optional<Vector3f> getGalacticPos(){
		return Optional.ofNullable(this.galactic_position);
	}

	public Optional<SpaceObject.Generation> getGeneration()
	{
		return Optional.ofNullable(this.generation);
	}
	
	private Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> getParentOrbitMap()
	{
		return Optional.ofNullable(this.parentOrbitMap);
	}

	public Optional<Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>>> getSurface() {
		return Optional.ofNullable(surface);
	}

	public Optional<ResourceKey<SpaceObject>> getParent()
	{
		return Optional.ofNullable(this.parent);
	}

	public void setParent(@Nullable ResourceKey<SpaceObject> parent)
	{
		this.parent = parent;
	}
	
	public List<TextureLayerData> getTextureLayers()
	{
		return this.textureLayers;
	}

	private void setupOrbit(Map<String, Double> orbitMap)
	{
		if(orbitMap.containsKey(DISTANCE))
			this.distance = orbitMap.get(DISTANCE);
		
		if(orbitMap.containsKey(ORBIT_DAYS))
			this.orbitDays = orbitMap.get(ORBIT_DAYS);
		
		if(orbitMap.containsKey(ORBIT_START))
			this.orbitStart = orbitMap.get(ORBIT_START);
		
		if(orbitMap.containsKey(ORBIT_INCLINATION))
			this.orbitInclination = orbitMap.get(ORBIT_INCLINATION);
		
		if(orbitMap.containsKey(ROTATION))
			this.rotation = orbitMap.get(ROTATION);
	}

    @Nullable
    public Double getDistance()
	{
		return distance;
	}

    @Nullable
	public Double getAngularVelocity()
	{
		return orbitDays;
	}

    @Nullable
	public Double getOrbitOffset()
	{
        return orbitStart;
	}

	@Nullable
	public Double getOrbitInclination()
	{
        return orbitInclination;
	}

	@Nullable
	public Double getRotation()
	{
		return rotation;
	}

	public static class Serializable
	{
		private static final String OBJECT_KEY = "object_key";

		private static final String DIMENSION = "dimension";
		private static final String NAME = "name";
		private static final String SIZE = "size";
        private static final String TYPE_ID = "type_key";
        private static final String TYPE = "type";
		private static final String PARENT = "parent";
		private static final String GENERATION = "generation";
		private static final String TEXTURE_LAYERS = "texture_layers";

		@Nullable private final ResourceKey<SpaceObject> objectKey;
		@Nullable private final ResourceKey<Level> dimension;
		@Nullable private final String name;
		@Nullable private final Float size;
        @Nullable private final SpaceObjectType type;
		@Nullable private final Vector3f galactic_position;
		@Nullable private final SpaceObject.Generation generation;
        @Nullable private final Pair<ResourceKey<SpaceObject>, Map<String, Double>> parentOrbitMap;
		private List<ResourceKey<SpaceObject>> childObjects = new ArrayList<>();
		private final List<TextureLayerData> textureLayers;
		@Nullable private final Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>> surface;

		public Serializable(@Nonnull ResourceKey<SpaceObject> objectKey, SpaceObject object)
		{
			this.objectKey = objectKey;
			this.dimension = object.getDimension().orElse(null);
			this.name = object.getTranslationName();
			this.size = object.getSize();
            this.type = object.getType().orElse(null);
			this.galactic_position = object.getGalacticPos().orElse(null);
            this.parentOrbitMap = object.getParentOrbitMap().orElse(null);
			this.generation = object.getGeneration().orElse(null);
			this.textureLayers = object.getTextureLayers();
			this.surface = object.getSurface().orElse(null);
		}

		public Serializable(@Nullable ResourceKey<Level> dimension, @Nullable String name, @Nullable Float size,
                            @Nullable SpaceObjectType type,
                            @Nullable Vector3f galactic_position,
                            @Nullable Pair<ResourceKey<SpaceObject>, Map<String, Double>> parentOrbitMap,
                            @Nullable SpaceObject.Generation generation,
                            List<TextureLayerData> textureLayers,
                            @Nullable Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>> surface)
		{
			this.objectKey = null;
			this.dimension = dimension;
			this.name = name;
			this.size = size;
            this.type = type;
			this.galactic_position = galactic_position;
			this.parentOrbitMap = parentOrbitMap;
			this.generation = generation;
			this.textureLayers = textureLayers;
			this.surface = surface;
		}

        @Nullable
		public String getName()
		{
			return this.name;
		}

        @Nullable
		public ResourceKey<SpaceObject> getKey()
		{
			return this.objectKey;
		}

        @Nullable
		public Float getSize()
		{
			return this.size;
		}

        @Nullable
        public SpaceObjectType getType() {
            return type;
        }

        @Nullable
		public Vector3f getGalacticPos()
		{
			return galactic_position;
		}

        @Nullable
		public ResourceKey<Level> getDimension()
		{
			return this.dimension;
		}

        @Nullable
		public Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>> getSurface()
		{
			return surface;
		}

        @Nullable
		public Pair<ResourceKey<SpaceObject>, Map<String, Double>> getOrbitMap()
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

        @Nullable
		public Generation getGeneration()
		{
			return generation;
		}

		public List<TextureLayerData> getTextureLayers()
		{
			return this.textureLayers;
		}

		public CompoundTag serialize()
		{
			CompoundTag objectTag = new CompoundTag();

			if(this.objectKey != null)
			{
				objectTag.putString(OBJECT_KEY, this.objectKey.location().toString());

				if(this.type != null)
					objectTag.put(TYPE, type.serializeNBT());
			}
			else
			{
				if(this.getDimension() != null)
					objectTag.putString(DIMENSION, this.getDimension().location().toString());

				if(this.getName() != null)
					objectTag.putString(NAME, this.getName());

				if(this.getSize() != null)
					objectTag.putFloat(SIZE, this.getSize());

                if(this.getType() != null) {
                    objectTag.putString(TYPE_ID, SpaceObjectTypeInit.OBJECT_TYPE_DISPATCHER.registryGetter().get().getKey(this.getType().getType()).getPath());
                    objectTag.put(TYPE, this.getType().serializeNBT());
                }

				if(this.generation != null)
				{
					objectTag.put(GENERATION, generation.serialize());
				}

				if(this.getOrbitMap() != null)
				{
					CompoundTag orbitMap = new CompoundTag();
					orbitMap.putDouble(DISTANCE, this.getOrbitMap().getSecond().get(DISTANCE));
					orbitMap.putDouble(ORBIT_DAYS, this.getOrbitMap().getSecond().get(ORBIT_DAYS));
					orbitMap.putDouble(ORBIT_START, this.getOrbitMap().getSecond().get(ORBIT_START));
					orbitMap.putDouble(ORBIT_INCLINATION, this.getOrbitMap().getSecond().get(ORBIT_INCLINATION));
					orbitMap.putDouble(ROTATION, this.getOrbitMap().getSecond().get(ROTATION));

					objectTag.put("orbit", orbitMap);
				}

				if(this.getGalacticPos() != null)
				{
					CompoundTag galPos = new CompoundTag();
					galPos.putFloat("x", this.getGalacticPos().x);
					galPos.putFloat("y", this.getGalacticPos().y);
					galPos.putFloat("z", this.getGalacticPos().z);
					objectTag.put("galactic_position", galPos);
				}
                if(this.parentOrbitMap != null)
                    objectTag.putString(PARENT, this.parentOrbitMap.getFirst().location().toString());

				ListTag textureLayers = new ListTag();

				this.textureLayers.forEach(textureLayer -> textureLayers.add(textureLayer.serialize()));
				objectTag.put(TEXTURE_LAYERS, textureLayers);
			}

			if (this.getSurface() != null) {
				CompoundTag surfaceTag = new CompoundTag();
                surfaceTag.putString("noise_settings", this.getSurface().getFirst().location().toString());
                ListTag biomeList = new ListTag();
                this.getSurface().getSecond().forEach(biome -> biomeList.add(StringTag.valueOf(biome.location().toString())));
                surfaceTag.put("biomes", biomeList);
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
				String name = null;
				if(objectTag.contains(NAME))
					name = objectTag.getString(NAME);

				ResourceKey<Level> dimension = null;
				if(objectTag.contains(DIMENSION))
					dimension = stringToDimension(objectTag.getString(DIMENSION));

				Float size = null;
				if(objectTag.contains(SIZE))
					size = objectTag.getFloat(SIZE);

				SpaceObjectType type = null;
                if(objectTag.contains(TYPE_ID)) {
                    type = SpaceObjectTypeInit.TYPE_SET.get(objectTag.getString(TYPE_ID)).deserializeNBT(objectTag);
                }

				Map<String, Double> orbitMap = null;
				if(objectTag.contains("orbit"))
				{
					Map<String, Double> orbit = new HashMap<>(Map.of());
					CompoundTag tag = objectTag.getCompound("orbit");
					orbit.put(DISTANCE, tag.getDouble(DISTANCE));
					orbit.put(ORBIT_DAYS, tag.getDouble(ORBIT_DAYS));
					orbit.put(ORBIT_START, tag.getDouble(ORBIT_START));
					orbit.put(ORBIT_INCLINATION, tag.getDouble(ORBIT_INCLINATION));
					orbit.put(ROTATION, tag.getDouble(ROTATION));

					orbitMap = orbit;
				}

				Vector3f galactic_position = null;
				if(objectTag.contains("galactic_position")) {
					CompoundTag galPos = objectTag.getCompound("galactic_position");
					galactic_position = new Vector3f(galPos.getFloat("x"), galPos.getFloat("y"), galPos.getFloat("y"));
				}

				Generation generation = null;
				if(objectTag.contains(GENERATION))
				{
					CompoundTag generationTag = objectTag.getCompound(GENERATION);
					generation = Generation.deserialize(generationTag);
				}

				ResourceKey<SpaceObject> parent = null;
				if(objectTag.contains(PARENT))
					parent = stringToSpaceObjectKey(objectTag.getString(PARENT));
				Pair<ResourceKey<SpaceObject>, Map<String, Double>> parentOrbitMap = null;
				if(parent != null && orbitMap != null)
					parentOrbitMap = new Pair<>(parent, orbitMap);

				ListTag layersTag = objectTag.getList(TEXTURE_LAYERS, Tag.TAG_LIST);
				List<TextureLayerData> textureLayers = new ArrayList<>();
				layersTag.forEach(layertag -> textureLayers.add(TextureLayerData.deserialize((CompoundTag) layertag)));

				Pair<ResourceKey<NoiseGeneratorSettings>, List<ResourceKey<Biome>>> surface = null;
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

					surface = new Pair<>(key, biomeList);
				}
				return new SpaceObject.Serializable(dimension, name, size, type, galactic_position, parentOrbitMap, generation, textureLayers, surface);
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
