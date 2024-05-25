package net.povstalec.astralvoyage.common.network.packets;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public static CompoundTag serialize(TextureLayerData layerData)
    {
        CompoundTag layer = new CompoundTag();
        CompoundTag textureSettings = new CompoundTag();
        StringTag rl = StringTag.valueOf(layerData.getLayer().getFirst().toString());
        IntArrayTag rgba = new IntArrayTag(layerData.getLayer().getSecond().getFirst());
        textureSettings.put("rgba", rgba);
        textureSettings.putBoolean("blend", layerData.getLayer().getSecond().getSecond());
        layer.put("texture", rl);
        layer.put("texture_settings", textureSettings);
        return layer;
    }

    public static TextureLayerData deserialize(CompoundTag tag)
    {
        CompoundTag textureSettingsTag = tag.getCompound("texture_settings");
        Pair<List<Integer>, Boolean> textureSettings = new Pair<>(Arrays.stream(textureSettingsTag.getIntArray("rgba")).boxed().collect(Collectors.toList()), textureSettingsTag.getBoolean("blend"));
        Pair<ResourceLocation, Pair<List<Integer>, Boolean>> layer = new Pair(ResourceLocation.tryParse(tag.getString("texture")), textureSettings);

        return new TextureLayerData(layer);
    }

    public static List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> toPairList(List<TextureLayerData> dataList)
    {
        List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> pairList = new ArrayList<>();
        dataList.forEach(data -> pairList.add(data.getLayer()));
        return pairList;
    }

    public static List<TextureLayerData> toDataList(List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> pairList)
    {
        List<TextureLayerData> dataList = new ArrayList<>();
        pairList.forEach(pair -> dataList.add(new TextureLayerData(pair)));
        return dataList;
    }
}
