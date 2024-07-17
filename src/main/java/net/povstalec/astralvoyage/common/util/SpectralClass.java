package net.povstalec.astralvoyage.common.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Random;

public enum SpectralClass implements StringRepresentable
{
    // Remnants
    XW("white_dwarf",new Pair<>(8000d, 40000d), new Pair<>(0.17d, 1.4d), new Pair<>(2100d, 6300d), new Pair<>(0.02d, 0.1d), 0.65f, RandomTextureLayers.Star.O.getTextureLayer()),
    XN("neutron_star", new Pair<>(600000d, 1000000d), new Pair<>(1.4, 4.5d), new Pair<>(10d, 20d), new Pair<>(0.5d, 3d), 0.01697f, RandomTextureLayers.Star.O.getTextureLayer()),
    XB("black_hole", new Pair<>(0d, 0d), new Pair<>(5d, 100d), new Pair<>(10d, 150d), new Pair<>(0d, 1d), 0.003f, RandomTextureLayers.Star.XB.getTextureLayer()),

    // Main Sequence
    O("o", new Pair<>(33000d, Double.MAX_VALUE), new Pair<>(16d, Double.MAX_VALUE), new Pair<>(105000000D, Double.MAX_VALUE), new Pair<>(30000d, Double.MAX_VALUE), 0.00003f, RandomTextureLayers.Star.O.getTextureLayer()),
    B("b", new Pair<>(10000d, 33000d), new Pair<>(2.1d, 16d), new Pair<>(1260000d, 105000000d), new Pair<>(25d, 30000d), 0.12f, RandomTextureLayers.Star.B.getTextureLayer()),
    A("a", new Pair<>(7300d, 10000d), new Pair<>(1.4d, 2.1d), new Pair<>(980000d, 1260000d), new Pair<>(5d, 25d), 0.61f, RandomTextureLayers.Star.A.getTextureLayer()),
    F("f", new Pair<>(6000d, 7300d), new Pair<>(1.04d, 1.4d), new Pair<>(805000d, 980000d), new Pair<>(1.5d, 5d), 3f, RandomTextureLayers.Star.F.getTextureLayer()),
    G("g", new Pair<>(5300d, 6000d), new Pair<>(0.8d, 1.04d), new Pair<>(672000d, 805000d), new Pair<>(0.6d, 1.5d), 7.6f, RandomTextureLayers.Star.G.getTextureLayer()),
    K("k", new Pair<>(3900d, 5300d), new Pair<>(0.45d, 0.8d), new Pair<>(490000d, 672000d), new Pair<>(0.08d, 0.6d), 12f, RandomTextureLayers.Star.K.getTextureLayer()),
    M("m", new Pair<>(2300d, 3900d), new Pair<>(0.08d, 0.45d), new Pair<>(7000d, 490000d), new Pair<>(0.01d, 0.08d), 76f, RandomTextureLayers.Star.M.getTextureLayer());

    private final String type;
    private final float chance;
    private final Pair<Double, Double> surface_temperature;
    private final Pair<Double, Double> mass;
    private final Pair<Double, Double> radius;
    private final Pair<Double, Double> luminosity;
    private final List<TextureLayerData> layers;
    SpectralClass(String type, Pair<Double, Double> surface_temperature, Pair<Double, Double> mass, Pair<Double, Double> radius, Pair<Double, Double> luminosity, float chance, List<TextureLayerData> layers)
    {
        this.type = type;
        this.surface_temperature = surface_temperature;
        this.mass = mass;
        this.radius = radius;
        this.luminosity = luminosity;
        this.chance = chance;
        this.layers = layers;
    }

    @Override
    public String getSerializedName()
    {
        return type;
    }

    public float getChance() {
        return chance;
    }

    public Pair<Double, Double> getSurfaceTemperatureRange() {
        return surface_temperature;
    }

    public Pair<Double, Double> getLuminosityRange() {
        return luminosity;
    }

    public Pair<Double, Double> getMassRange() {
        return mass;
    }

    public Pair<Double, Double> getRadiusRange() {
        return radius;
    }

    public List<TextureLayerData> getLayers() {
        return layers;
    }

    public static SpectralClass randomClass(Random random)
    {
        float pick = random.nextFloat()*100f;
        for(SpectralClass type : SpectralClass.values())
        {
            pick -= type.getChance();
            if(pick <= 0)
                return type;
        }
        return SpectralClass.M;
    }

    public StarProperties classToProperties()
    {
        Random random = new Random();
        double surfaceTemperature = random.nextDouble(this.getSurfaceTemperatureRange().getFirst(), this.getSurfaceTemperatureRange().getSecond());
        double mass = random.nextDouble(this.getMassRange().getFirst(), this.getMassRange().getSecond());
        double radius = random.nextDouble(this.getRadiusRange().getFirst(), this.getRadiusRange().getSecond());
        double luminosity = random.nextDouble(this.getLuminosityRange().getFirst(), this.getLuminosityRange().getSecond());

        return new StarProperties(surfaceTemperature, mass, radius, luminosity);
    }
}
