package net.povstalec.astralvoyage.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

public class AtmosphereParameter
{
    public static final Codec<AtmosphereParameter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("sea_level_temperature").forGetter(AtmosphereParameter::getSeaLevelTemperature),
            Codec.DOUBLE.fieldOf("sea_level_pressure").forGetter(AtmosphereParameter::getSeaLevelPressure),
            Codec.DOUBLE.fieldOf("scale_height").forGetter(AtmosphereParameter::getScaleHeight)
    ).apply(instance, AtmosphereParameter::new));

    public static final String SEA_LEVEL_TEMPERATURE = "sea_level_temperature";
    public static final String SEA_LEVEL_PRESSURE = "sea_level_pressure";
    public static final String SCALE_HEIGHT = "scale_height";

    public double seaLevelTemperature;
    public double seaLevelPressure;
    public double scaleHeight;
    public AtmosphereParameter(double seaLevelTemperature, double seaLevelPressure, double scaleHeight)
    {
        this.seaLevelTemperature = seaLevelTemperature;
        this.seaLevelPressure = seaLevelPressure;
        this.scaleHeight = scaleHeight;
    }

    public double getSeaLevelTemperature()
    {
        return seaLevelTemperature;
    }

    public double getSeaLevelPressure()
    {
        return seaLevelPressure;
    }

    public double getScaleHeight()
    {
        return scaleHeight;
    }

    public void setSeaLevelTemperature(double seaLevelTemperature)
    {
        this.seaLevelTemperature = seaLevelTemperature;
    }

    public void setSeaLevelPressure(double seaLevelPressure)
    {
        this.seaLevelPressure = seaLevelPressure;
    }

    public void setScaleHeight(double scaleHeight)
    {
        this.scaleHeight = scaleHeight;
    }

    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putDouble(SEA_LEVEL_TEMPERATURE, this.seaLevelTemperature);
        tag.putDouble(SEA_LEVEL_PRESSURE, this.seaLevelPressure);
        tag.putDouble(SCALE_HEIGHT, this.scaleHeight);
        return tag;
    }

    public static AtmosphereParameter deserializeNBT(CompoundTag tag)
    {
        double seaLevelTemperature = tag.getDouble(SEA_LEVEL_TEMPERATURE);
        double seaLevelPressure = tag.getDouble(SEA_LEVEL_PRESSURE);
        double scaleHeight = tag.getDouble(SCALE_HEIGHT);

        return new AtmosphereParameter(seaLevelTemperature, seaLevelPressure, scaleHeight);
    }
}
