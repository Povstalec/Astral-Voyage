package net.povstalec.astralvoyage.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;

public class StarProperties {

    public static final Codec<StarProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("surface_temperature").forGetter(StarProperties::getSurfaceTemperature),
            Codec.DOUBLE.fieldOf("mass").forGetter(StarProperties::getMass),
            Codec.DOUBLE.fieldOf("luminosity").forGetter(StarProperties::getLuminosity)
    ).apply(instance, StarProperties::new));

    public static final String SURFACE_TEMPERATURE = "surface_temperature";
    public static final String MASS = "mass";
    public static final String LUMINOSITY = "luminosity";

    public double surfaceTemperature;
    public double mass;
    public double luminosity;

    public StarProperties(double surfaceTemperature, double mass, double luminosity)
    {
        this.surfaceTemperature = surfaceTemperature;
        this.mass = mass;
        this.luminosity = luminosity;
    }

    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putDouble(SURFACE_TEMPERATURE, this.surfaceTemperature);
        tag.putDouble(MASS, this.mass);
        tag.putDouble(LUMINOSITY, this.luminosity);
        return tag;
    }

    public static StarProperties deserializeNBT(CompoundTag tag)
    {
        double surfaceTemperature = tag.getDouble(SURFACE_TEMPERATURE);
        double mass = tag.getDouble(MASS);
        double luminosity = tag.getDouble(LUMINOSITY);

        return new StarProperties(surfaceTemperature, mass, luminosity);
    }

    public double getSurfaceTemperature()
    {
        return surfaceTemperature;
    }

    public double getMass()
    {
        return mass;
    }

    public double getLuminosity()
    {
        return luminosity;
    }

    public void setSurfaceTemperature(double surfaceTemperature)
    {
        this.surfaceTemperature = surfaceTemperature;
    }

    public void setMass(double mass)
    {
        this.mass = mass;
    }

    public void setLuminosity(double luminosity)
    {
        this.luminosity = luminosity;
    }
}
