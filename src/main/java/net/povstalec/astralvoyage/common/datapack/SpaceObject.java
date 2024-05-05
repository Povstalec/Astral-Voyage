package net.povstalec.astralvoyage.common.datapack;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.astralvoyage.AstralVoyage;

public class SpaceObject
{
	public static final ResourceLocation SPACE_OBJECT_LOCATION = new ResourceLocation(AstralVoyage.MODID, "space_object");
	public static final ResourceKey<Registry<SpaceObject>> REGISTRY_KEY = ResourceKey.createRegistryKey(SPACE_OBJECT_LOCATION);
	public static final Codec<ResourceKey<SpaceObject>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);

	private static final Codec<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> PARENT = Codec.pair(RESOURCE_KEY_CODEC.fieldOf("parent_object").codec(), Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("orbit").codec());
	
	private static final Codec<Pair<List<Integer>, Boolean>> TEXTURE_SETTINGS = Codec.pair(Codec.INT.listOf().fieldOf("rgba").codec(), Codec.BOOL.fieldOf("blends").codec());
	private static final Codec<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> TEXTURE_LAYER = Codec.pair(ResourceLocation.CODEC.fieldOf("texture").codec(), TEXTURE_SETTINGS.fieldOf("texture_settings").codec());
	
	public static final Codec<SpaceObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// Dimension this Stellar Location is tied to
			Level.RESOURCE_KEY_CODEC.optionalFieldOf("dimension").forGetter(SpaceObject::getDimension),
			// Translation name of the Stellar Location
			Codec.STRING.fieldOf("name").forGetter(SpaceObject::getTranslationName),
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
	private final Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> parentOrbitMap;
	private final List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers;

	private final Optional<ResourceKey<SpaceObject>> parent;
	// Orbital characteristics
	private Optional<Double> distance = Optional.empty(); // R
	private Optional<Double> orbitDays = Optional.empty(); // How many days it takes for the planet to complete one orbit
	private Optional<Double> orbitStart = Optional.empty(); // Phi
	private Optional<Double> orbitInclination = Optional.empty(); // Tetha
	private Optional<Double> rotation = Optional.empty();
	
	public SpaceObject(Optional<ResourceKey<Level>> dimension, String translationName,
			Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> parentOrbitMap, 
			List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers)
	{
		this.dimension = dimension;
		this.translationName = translationName;
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
	
	private Optional<Pair<ResourceKey<SpaceObject>, Map<String, Double>>> getParentOrbitMap()
	{
		return this.parentOrbitMap;
	}
	
	public Optional<ResourceKey<SpaceObject>> getParent()
	{
		return this.parent;
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
}
