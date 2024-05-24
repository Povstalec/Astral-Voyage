package net.povstalec.astralvoyage.common.datapack;

import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientSpaceObject {

    public ResourceKey<SpaceObject> key;
    public Vector3f solarPos;
    public List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers;

    public ClientSpaceObject(ResourceKey<SpaceObject> key, Vector3f solarPos, List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> layers)
    {
        this.key = key;
        this.solarPos = solarPos;
        this.textureLayers = layers;
    }

    public ResourceKey<SpaceObject> getKey() {
        return key;
    }

    public Vector3f getSolarPos() {
        return solarPos;
    }

    public List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> getTextureLayers() {
        return textureLayers;
    }

    public void setKey(ResourceKey<SpaceObject> key) {
        this.key = key;
    }

    public void setSolarPos(Vector3f solarPos) {
        this.solarPos = solarPos;
    }

    public void setTextureLayers(List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers) {
        this.textureLayers = textureLayers;
    }

    public CompoundTag serialize(){
        CompoundTag tag = new CompoundTag();

        tag.putString("key", this.key.toString());
        CompoundTag solarPos = new CompoundTag();
        solarPos.putFloat("x", this.solarPos.x);
        solarPos.putFloat("y", this.solarPos.y);
        solarPos.putFloat("z", this.solarPos.z);

        tag.put("solar_pos", solarPos);

        ListTag textureLayers = new ListTag();
        this.textureLayers.forEach(textureLayer -> {
            CompoundTag layer = new CompoundTag();
            CompoundTag textureSettings = new CompoundTag();
            StringTag rl = StringTag.valueOf(textureLayer.getFirst().toString());
            IntArrayTag rgba = new IntArrayTag(textureLayer.getSecond().getFirst());
            textureSettings.put("rgba", rgba);
            textureSettings.putBoolean("blend", textureLayer.getSecond().getSecond());
            layer.put("texture", rl);
            layer.put("texture_settings", textureSettings);
            textureLayers.add(layer);
        });
        tag.put("texture_layers", textureLayers);

        return tag;
    }

    public ClientSpaceObject deserialize(CompoundTag tag){
        ResourceKey<SpaceObject> key = SpaceObject.stringToSpaceObjectKey(tag.getString("key"));

        CompoundTag solarPos = (CompoundTag) tag.get("solar_pos");
        Vector3f solarPosV = new Vector3f(solarPos.getFloat("x"), solarPos.getFloat("y"), solarPos.getFloat("z"));

        ListTag layersTag = tag.getList("texture_layers", Tag.TAG_LIST);
        List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers = new ArrayList<>();
        layersTag.forEach(layertag -> {
            CompoundTag layerTag = (CompoundTag) layertag;
            CompoundTag textureSettingsTag = layerTag.getCompound("texture_settings");
            Pair<List<Integer>, Boolean> textureSettings = new Pair<>(Arrays.stream(textureSettingsTag.getIntArray("rgba")).boxed().collect(Collectors.toList()), textureSettingsTag.getBoolean("blend"));
            Pair<ResourceLocation, Pair<List<Integer>, Boolean>> layer = new Pair(ResourceLocation.tryParse(layerTag.getString("texture")), textureSettings);
            textureLayers.add(layer);
        });

        return new ClientSpaceObject(key, solarPosV, textureLayers);
    }
}
