package net.povstalec.astralvoyage.common.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum SpectralClass implements StringRepresentable
{
    // Remnants
    XW("white_dwarf",0.65f, RandomTextureLayers.Star.O.getTextureLayer()),
    XN("neutron_star", 0.01697f, RandomTextureLayers.Star.O.getTextureLayer()),
    XB("black_hole", 0.003f, RandomTextureLayers.Star.XB.getTextureLayer()),

    // Main Sequence
    O("o", 0.00003f, RandomTextureLayers.Star.O.getTextureLayer()),
    B("b", 0.12f, RandomTextureLayers.Star.B.getTextureLayer()),
    A("a", 0.61f, RandomTextureLayers.Star.A.getTextureLayer()),
    F("f", 3f, RandomTextureLayers.Star.F.getTextureLayer()),
    G("g", 7.6f, RandomTextureLayers.Star.G.getTextureLayer()),
    K("k", 12f, RandomTextureLayers.Star.K.getTextureLayer()),
    M("m", 76f, RandomTextureLayers.Star.M.getTextureLayer());

    private final String type;
    private final float chance;
    private final List<TextureLayerData> layers;
    SpectralClass(String type, float chance, List<TextureLayerData> layers)
    {
        this.type = type;
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
}
