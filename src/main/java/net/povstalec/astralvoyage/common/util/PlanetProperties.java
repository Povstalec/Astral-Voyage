package net.povstalec.astralvoyage.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.Optional;

public class PlanetProperties
{
    public static final Codec<PlanetProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("mass").forGetter(PlanetProperties::getMass),
            Codec.DOUBLE.fieldOf("rotation_period").forGetter(PlanetProperties::getRotationPeriod),
            Codec.DOUBLE.fieldOf("axial_tilt").forGetter(PlanetProperties::getAxialTilt),
            Codec.DOUBLE.optionalFieldOf("magnetic_field_strength").forGetter(PlanetProperties::getMagneticStrengthOptional)
    ).apply(instance, PlanetProperties::new));

    public static final String MASS = "mass";
    public static final String ROTATION_PERIOD = "rotation_period";
    public static final String AXIAL_TILT = "axial_tilt";
    public static final String MAGNETIC_POWER = "magnetic_power";

    public double mass;
    public double rotationPeriod;
    public double axialTilt;
    @Nullable public Double magnetic_power;
    public PlanetProperties(double mass, double rotation_period, double axial_tilt, Optional<Double> magnetic_power)
    {
        this.mass = mass;
        this.rotationPeriod = rotation_period;
        this.axialTilt = axial_tilt;
        this.magnetic_power = magnetic_power.orElse(null);
    }

    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putDouble(MASS, this.mass);
        tag.putDouble(ROTATION_PERIOD, this.rotationPeriod);
        tag.putDouble(AXIAL_TILT, this.axialTilt);
        if(this.magnetic_power != null)
            tag.putDouble(MAGNETIC_POWER, this.magnetic_power);
        return tag;
    }

    public static PlanetProperties deserializeNBT(CompoundTag tag)
    {
        double mass = tag.getDouble(MASS);
        double rotation_period = tag.getDouble(ROTATION_PERIOD);
        double axial_tilt = tag.getDouble(AXIAL_TILT);

        Optional<Double> magnetic_power = Optional.empty();
        if(tag.contains(MAGNETIC_POWER))
            magnetic_power = Optional.of(tag.getDouble(MAGNETIC_POWER));

        return new PlanetProperties(mass, rotation_period, axial_tilt, magnetic_power);
    }

    public double getMass()
    {
        return mass;
    }

    public double getRotationPeriod()
    {
        return rotationPeriod;
    }

    public double getAxialTilt()
    {
        return axialTilt;
    }

    public Optional<Double> getMagneticStrengthOptional() {
        return Optional.ofNullable(this.magnetic_power);
    }

    @Nullable
    public Double getMagneticStrength() {
        return magnetic_power;
    }

    public void setRotationPeriod(double rotationPeriod)
    {
        this.rotationPeriod = rotationPeriod;
    }

    public void setAxialTilt(double axialTilt)
    {
        this.axialTilt = axialTilt;
    }

    public void setMass(double mass)
    {
        this.mass = mass;
    }

}
