package net.povstalec.astralvoyage.common.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.network.packets.TextureLayerData;

import java.util.List;

public class RandomTextureLayers {

    public enum Planet {
        MERCURY(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/mercury.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
        VENUS(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/venus.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
        EARTH(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/earth.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
        MARS(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/mars.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
        JUPITER(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/jupiter.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
        SATURN(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/saturn.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
        URANUS(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/uranus.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
        NEPTUNE(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/neptune.png"), new Pair<>(List.of(255, 255, 255, 255), false))),

        MERCURY_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/mercury_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true))),
        VENUS_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/venus_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true))),
        EARTH_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/earth_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true))),
        MARS_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/mars_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true))),
        JUPITER_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/jupiter_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true))),
        SATURN_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/saturn_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true))),
        URANUS_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/uranus_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true))),
        NEPTUNE_HALO(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/neptune_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true)));

        private Pair<ResourceLocation, Pair<List<Integer>, Boolean>> texture_layer;

        Planet(Pair<ResourceLocation, Pair<List<Integer>, Boolean>> layer)
        {
            this.texture_layer = layer;
        }

        public Pair<ResourceLocation, Pair<List<Integer>, Boolean>> getTextureLayer() {
            return texture_layer;
        }
    }

    public enum Star {

        G(new Pair<>(new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_g.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
                new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_g_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true))))),
        M(new Pair<>(new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_m.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
                new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_m_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true))))),
        F(new Pair<>(new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_f.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
                new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_f_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true))))),
        B(new Pair<>(new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_b.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
                new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_b_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true))))),
        A(new Pair<>(new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_a.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
                new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_a_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true))))),
        O(new Pair<>(new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_o.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
                new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_o_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true))))),
        K(new Pair<>(new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_k.png"), new Pair<>(List.of(255, 255, 255, 255), false))),
                new TextureLayerData(new Pair<>(new ResourceLocation(AstralVoyage.MODID, "textures/environment/stars/star_k_halo.png"), new Pair<>(List.of(255, 255, 255, 255), true)))));



        private Pair<TextureLayerData, TextureLayerData> texture_layer_pair;

        Star(Pair<TextureLayerData, TextureLayerData> layer)
        {
            this.texture_layer_pair = layer;
        }

        public Pair<TextureLayerData, TextureLayerData> getTextureLayer() {
            return texture_layer_pair;
        }

    }



}
