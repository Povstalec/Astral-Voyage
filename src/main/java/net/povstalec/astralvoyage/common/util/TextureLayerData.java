package net.povstalec.astralvoyage.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;

public class TextureLayerData
{
    private static final Codec<Pair<List<Integer>, Boolean>> TEXTURE_SETTINGS = Codec.pair(Codec.INT.listOf().fieldOf("rgba").codec(), Codec.BOOL.fieldOf("blends").codec());
    private static final Codec<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> TEXTURE_LAYER = Codec.pair(ResourceLocation.CODEC.fieldOf("texture").codec(), TEXTURE_SETTINGS.fieldOf("texture_settings").codec());

    public static final Codec<TextureLayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TEXTURE_LAYER.fieldOf("layer").forGetter(TextureLayerData::getLayer)
    ).apply(instance, TextureLayerData::new));
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

    public CompoundTag serialize()
    {
        CompoundTag layer = new CompoundTag();
        CompoundTag textureSettings = new CompoundTag();
        StringTag rl = StringTag.valueOf(this.getLayer().getFirst().toString());
        IntArrayTag rgba = new IntArrayTag(this.getLayer().getSecond().getFirst());
        textureSettings.put("rgba", rgba);
        textureSettings.putBoolean("blend", this.getLayer().getSecond().getSecond());
        layer.put("texture", rl);
        layer.put("texture_settings", textureSettings);
        return layer;
    }

    public static TextureLayerData deserialize(CompoundTag tag)
    {
        ResourceLocation rl = ResourceLocation.tryParse(tag.getString("texture"));
        CompoundTag textureSettingsTag = tag.getCompound("texture_settings");
        Pair<List<Integer>, Boolean> textureSettings = new Pair<>(Arrays.stream(textureSettingsTag.getIntArray("rgba")).boxed().collect(Collectors.toList()), textureSettingsTag.getBoolean("blend"));
        Pair<ResourceLocation, Pair<List<Integer>, Boolean>> layer = new Pair<>(rl, textureSettings);

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
