package net.povstalec.astralvoyage.common.network.packets;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class TextureLayerData {
    public Pair<ResourceLocation, Pair<List<Integer>, Boolean>> layer;

    public TextureLayerData(Pair<ResourceLocation, Pair<List<Integer>, Boolean>> layer){
        this.layer = layer;
    }

    public Pair<ResourceLocation, Pair<List<Integer>, Boolean>> getLayer() {
        return layer;
    }

    public void setLayer(Pair<ResourceLocation, Pair<List<Integer>, Boolean>> layer) {
        this.layer = layer;
    }
}
