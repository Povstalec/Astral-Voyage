package net.povstalec.astralvoyage.common.datapack;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.povstalec.astralvoyage.common.init.SpaceObjectTypeInit;
import net.povstalec.astralvoyage.common.util.*;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class PlanetType implements SpaceObjectType
{
    public static final Codec<PlanetType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.either(StringRepresentable.fromEnum(PlanetClass::values),
                    PlanetProperties.CODEC).fieldOf("planet_data").forGetter(PlanetType::getClassOrProperties),
            AtmosphereParameter.CODEC.optionalFieldOf("atmosphere").forGetter(PlanetType::getAtmosphere),
            DiskParameter.PlanetaryRing.CODEC.optionalFieldOf("planetary_ring").forGetter(PlanetType::getPlanetaryRing)
    ).apply(instance, PlanetType::new));

    public static final String PLANET_PROPERTIES = "planet_properties";
    public static final String PLANETARY_RING = "planetary_ring";
    public static final String ATMOSPHERE = "atmosphere";

    public PlanetProperties planetProperties;
    @Nullable DiskParameter.PlanetaryRing disk;
    @Nullable AtmosphereParameter atmosphere;
    public PlanetType(Either<PlanetClass, PlanetProperties> planetClassProperties, Optional<AtmosphereParameter> atmosphere, Optional<DiskParameter.PlanetaryRing> disk)
    {
        planetClassProperties.mapBoth(
                planetary -> this.planetProperties = planetary.classToProperties(),
                properties -> this.planetProperties = properties
        );

        this.disk = disk.orElse(null);
        this.atmosphere = atmosphere.orElse(null);
    }

    @Override
    public Codec<? extends SpaceObjectType> getType()
    {
        return SpaceObjectTypeInit.PLANET.get();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();

        if(this.planetProperties != null)
            tag.put(PLANET_PROPERTIES, this.planetProperties.serializeNBT());

        if(this.disk != null)
            tag.put(PLANET_PROPERTIES, this.disk.serializeNBT());

        if(this.atmosphere != null)
            tag.put(ATMOSPHERE, this.atmosphere.serializeNBT());
        return tag;
    }

    @Override
    public SpaceObjectType deserializeNBT(CompoundTag tag)
    {
        PlanetType type = new PlanetType(Either.left(PlanetClass.GAIA), Optional.empty(), Optional.empty());

        if(tag.contains(PLANET_PROPERTIES))
            type.setPlanetProperties(PlanetProperties.deserializeNBT(tag.getCompound(PLANET_PROPERTIES)));

        if(tag.contains(PLANETARY_RING))
            type.setPlanetaryRing(DiskParameter.PlanetaryRing.deserializeNBT(tag.getCompound(PLANETARY_RING)));

        if(tag.contains(ATMOSPHERE))
            type.setAtmosphere(AtmosphereParameter.deserializeNBT(tag.getCompound(ATMOSPHERE)));

        return type;
    }

    public Either<PlanetClass, PlanetProperties> getClassOrProperties()
    {
        if(this.planetProperties != null)
            return Either.right(this.planetProperties);

        else return null;
    }

    public PlanetProperties getPlanetProperties()
    {
        return planetProperties;
    }

    public Optional<DiskParameter.PlanetaryRing> getPlanetaryRing()
    {
        return Optional.ofNullable(this.disk);
    }

    public Optional<AtmosphereParameter> getAtmosphere() {
        return Optional.ofNullable(atmosphere);
    }

    public void setPlanetProperties(@Nullable PlanetProperties planetProperties)
    {
        this.planetProperties = planetProperties;
    }

    public void setPlanetaryRing(@Nullable DiskParameter.PlanetaryRing disk)
    {
        this.disk = disk;
    }


    public void setAtmosphere(@Nullable AtmosphereParameter atmosphere)
    {
        this.atmosphere = atmosphere;
    }



    /*public static PlanetType randomType(Random random)
    {
        PlanetClass spectralClass = PlanetClass.randomClass(random);
        PlanetProperties properties = spectralClass.classToProperties();
        return new PlanetType(Either.right(properties), Optional.empty());
    }*/
}
