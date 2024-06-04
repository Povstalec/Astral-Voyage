package net.povstalec.astralvoyage.common.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.astralvoyage.AstralVoyage;

import java.util.List;

public enum RandomTextureLayers {

    MERCURY(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/mercury"), new Pair<>(List.of(255, 255, 255, 255), false))),
    VENUS(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/venus"), new Pair<>(List.of(255, 255, 255, 255), false))),
    EARTH(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/earth"), new Pair<>(List.of(255, 255, 255, 255), false))),
    MARS(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/mars"), new Pair<>(List.of(255, 255, 255, 255), false))),
    JUPITER(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/jupiter"), new Pair<>(List.of(255, 255, 255, 255), false))),
    SATURN(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/saturn"), new Pair<>(List.of(255, 255, 255, 255), false))),
    URANUS(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/uranus"), new Pair<>(List.of(255, 255, 255, 255), false))),
    NEPTUNE(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/neptune"), new Pair<>(List.of(255, 255, 255, 255), false))),

    MERCURY_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/mercury_halo"), new Pair<>(List.of(255, 255, 255, 255), true))),
    VENUS_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/venus_halo"), new Pair<>(List.of(255, 255, 255, 255), true))),
    EARTH_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/earth_halo"), new Pair<>(List.of(255, 255, 255, 255), true))),
    MARS_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/mars_halo"), new Pair<>(List.of(255, 255, 255, 255), true))),
    JUPITER_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/jupiter_halo"), new Pair<>(List.of(255, 255, 255, 255), true))),
    SATURN_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/saturn_halo"), new Pair<>(List.of(255, 255, 255, 255), true))),
    URANUS_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/uranus_halo"), new Pair<>(List.of(255, 255, 255, 255), true))),
    NEPTUNE_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/neptune_halo"), new Pair<>(List.of(255, 255, 255, 255), true)));


    private Pair<ResourceLocation, Pair<List<Integer>, Boolean>> texture_layer;

    RandomTextureLayers(Pair<ResourceLocation, Pair<List<Integer>, Boolean>> layer)
    {
        this.texture_layer = layer;
    }

    public Pair<ResourceLocation, Pair<List<Integer>, Boolean>> getTextureLayer() {
        return texture_layer;
    }
}
