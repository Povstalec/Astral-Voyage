package net.povstalec.astralvoyage.common.datapack;

import java.util.List;
import java.util.Optional;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.client.render.level.SpaceshipDimensionSpecialEffects;

public class StellarLocation
{
	public static final ResourceLocation STELLAR_LOCATION_LOCATION = new ResourceLocation(AstralVoyage.MODID, "stellar_location");
	public static final ResourceKey<Registry<StellarLocation>> REGISTRY_KEY = ResourceKey.createRegistryKey(STELLAR_LOCATION_LOCATION);
	public static final Codec<ResourceKey<StellarLocation>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);

	private static final Codec<Pair<List<Integer>, Boolean>> TEXTURE_SETTINGS = Codec.pair(Codec.INT.listOf().fieldOf("rgba").codec(), Codec.BOOL.fieldOf("blends").codec());
	private static final Codec<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> TEXTURE_LAYER = Codec.pair(ResourceLocation.CODEC.fieldOf("texture").codec(), TEXTURE_SETTINGS.fieldOf("texture_settings").codec());
	
	public static final Codec<StellarLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// Dimension this Stellar Location is tied to
			Level.RESOURCE_KEY_CODEC.optionalFieldOf("dimension").forGetter(StellarLocation::getDimension),
			// Translation name of the Stellar Location
			Codec.STRING.fieldOf("name").forGetter(StellarLocation::getTranslationName),
			// Parent Stellar Location, probably used for orbits and stuff in the future
			RESOURCE_KEY_CODEC.optionalFieldOf("parent").forGetter(StellarLocation::getParent),
			// Textures and colors
			TEXTURE_LAYER.listOf().fieldOf("texture_layers").forGetter(StellarLocation::getTextureLayers),
			// Special Effects used for the Stellar Location, defaults to generic Spaceship special effects if none are specified
			ResourceLocation.CODEC.fieldOf("effects").orElse(SpaceshipDimensionSpecialEffects.SPACESHIP_EFFECTS).forGetter(StellarLocation::getSpecialEffects)
			).apply(instance, StellarLocation::new));

	private final Optional<ResourceKey<Level>> dimension;
	private final String translationName;
	private final Optional<ResourceKey<StellarLocation>> parent;
	private final List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers;
	private final ResourceLocation effectsLocation;
	
	public StellarLocation(Optional<ResourceKey<Level>> dimension, String translationName,
			Optional<ResourceKey<StellarLocation>> parent, 
			List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers, ResourceLocation effectsLocation)
	{
		this.dimension = dimension;
		this.translationName = translationName;
		this.parent = parent;
		this.textureLayers = textureLayers;
		this.effectsLocation = effectsLocation;
	}
	
	public Optional<ResourceKey<Level>> getDimension()
	{
		return this.dimension;
	}
	
	public String getTranslationName()
	{
		return this.translationName;
	}
	
	public Optional<ResourceKey<StellarLocation>> getParent()
	{
		return this.parent;
	}
	
	public List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> getTextureLayers()
	{
		return this.textureLayers;
	}
	
	public ResourceLocation getSpecialEffects()
	{
		return this.effectsLocation;
	}
}
