package net.povstalec.astralvoyage.common.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public enum PlanetClass implements StringRepresentable
{
    METAL_RICH("metal_rich", Pair.of(0.5, 5D), Pair.of(140D, 890D), Pair.of(0D, 23D), Pair.of(1.2D, 15D),
            null, null, null),
    FROZEN("icy", Pair.of(0.25D, 5D), Pair.of(140D, 890D), Pair.of(0D, 24D), Pair.of(0.1D, 10D),
            Pair.of(100D, 320D), Pair.of(0.25D, 1.6D), Pair.of(2.3D, 9.8D)),
    BARREN("barren", Pair.of(0.25D, 2D), Pair.of(140D, 890D), Pair.of(0D, 24D), Pair.of(0.02D, 0.55D),
            null, null, null),
    MOLTEN("molten", Pair.of(0.8D, 3.5D), Pair.of(80D, 680D), Pair.of(0D, 12D), Pair.of(1.2D, 4.5D),
            null, null, null),
    TOXIC("toxic", Pair.of(0.8D, 2.4D), Pair.of(450D, 760D), Pair.of(0D, 90D), Pair.of(0.9D, 3.5D),
            Pair.of(350D, 1200D), Pair.of(3D, 345D), Pair.of(2.5D, 8.5D)),

    //Habitable
    DESERT("desert", Pair.of(0.6D, 1.6D), Pair.of(270D, 480D), Pair.of(0D, 24D), Pair.of(0.8D, 1.4D),
            Pair.of(300D, 520D), Pair.of(0.8D, 1.2D), Pair.of(7.6D, 8.9D)),
    ARID("arid", Pair.of(0.6D, 1.6D), Pair.of(270D, 480D), Pair.of(0D, 24D), Pair.of(0.8D, 1.4D),
            Pair.of(290D, 420D), Pair.of(0.8D, 1.2D), Pair.of(7.6D, 8.9D)),
    ARCTIC("arctic", Pair.of(0.6D, 1.6D), Pair.of(270D, 480D), Pair.of(0D, 24D), Pair.of(0.8D, 1.4D),
            Pair.of(150D, 240D), Pair.of(0.8D, 1.2D), Pair.of(7.6D, 8.9D)),
    TUNDRA("tundra", Pair.of(0.6D, 1.6D), Pair.of(270D, 480D), Pair.of(0D, 24D), Pair.of(0.8D, 1.4D),
            Pair.of(220D, 290D), Pair.of(0.8D, 1.2D), Pair.of(7.6D, 8.9D)),
    OCEAN("ocean", Pair.of(0.6D, 1.6D), Pair.of(270D, 480D), Pair.of(0D, 24D), Pair.of(0.8D, 1.4D),
            Pair.of(260D, 320D), Pair.of(0.8D, 1.2D), Pair.of(7.6D, 8.9D)),
    GAIA("gaia", Pair.of(1.0D, 1.01D), Pair.of(365D, 366D), Pair.of(0D, 12D), Pair.of(1.0D, 1.01D),
            Pair.of(288D, 289D), Pair.of(1.0D, 1.01D), Pair.of(8.4D, 8.5D)),
    EARTH_LIKE("earth_like", Pair.of(0.8D, 1.2D), Pair.of(235D, 490D), Pair.of(0D, 12D), Pair.of(0.8D, 6D),
            Pair.of(270D, 300D), Pair.of(0.8D, 1.2D), Pair.of(7.9D, 8.8D));


    private String type;
    private Pair<Double, Double> massRange;
    private Pair<Double, Double> rotationPeriodRange;
    private Pair<Double, Double> axialTiltRange;
    @Nullable private Pair<Double, Double> magneticFieldStrengthRange;
    @Nullable private Pair<Double, Double> seaLevelTemperatureRange;
    @Nullable private Pair<Double, Double> seaLevelPressureRange;
    @Nullable private Pair<Double, Double> scaleHeightRange;
    PlanetClass(String type, Pair<Double, Double> massRange, Pair<Double, Double> rotationPeriodRange,
                Pair<Double, Double> axialTiltRange,
                @Nullable Pair<Double, Double> magneticFieldStrengthRange,
                @Nullable Pair<Double, Double> seaLevelTemperatureRange,
                @Nullable Pair<Double, Double> seaLevelPressureRange,
                @Nullable Pair<Double, Double> scaleHeightRange)
    {
        this.type = type;
        this.massRange = massRange;
        this.rotationPeriodRange = rotationPeriodRange;
        this.axialTiltRange = axialTiltRange;
        this.magneticFieldStrengthRange = magneticFieldStrengthRange;
        this.seaLevelTemperatureRange = seaLevelTemperatureRange;
        this.seaLevelPressureRange = seaLevelPressureRange;
        this.scaleHeightRange = scaleHeightRange;
    }

    @Override
    public String getSerializedName()
    {
        return type;
    }

    public Pair<Double, Double> getMassRange()
    {
        return massRange;
    }

    public Pair<Double, Double> getRotationPeriodRange()
    {
        return rotationPeriodRange;
    }

    public Pair<Double, Double> getAxialTiltRange()
    {
        return axialTiltRange;
    }

    @Nullable
    public Pair<Double, Double> getMagneticFieldStrengthRange()
    {
        return magneticFieldStrengthRange;
    }

    @Nullable
    public Pair<Double, Double> getSeaLevelTemperatureRange()
    {
        return seaLevelTemperatureRange;
    }

    @Nullable
    public Pair<Double, Double> getSeaLevelPressureRange()
    {
        return seaLevelPressureRange;
    }

    @Nullable
    public Pair<Double, Double> getScaleHeightRange()
    {
        return scaleHeightRange;
    }

    public PlanetProperties classToProperties()
    {
        Random random = new Random();

        double mass = random.nextDouble(this.getMassRange().getFirst(), this.getMassRange().getSecond());
        double rotationPeriod = random.nextDouble(this.getRotationPeriodRange().getFirst(), this.getRotationPeriodRange().getSecond());
        double axialTilt = random.nextDouble(this.getAxialTiltRange().getFirst(), this.getAxialTiltRange().getSecond());
        double magneticStrength = 0;
        if(this.getMagneticFieldStrengthRange() != null)
            magneticStrength = random.nextDouble(this.getMagneticFieldStrengthRange().getFirst(), this.getMagneticFieldStrengthRange().getSecond());

        return new PlanetProperties(mass, rotationPeriod, axialTilt, Optional.of(magneticStrength));
    }
}
