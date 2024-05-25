package net.povstalec.astralvoyage.common.datapack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.povstalec.astralvoyage.common.network.packets.TextureLayerData;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ClientSpaceObject {

    public ResourceKey<SpaceObject> key;
    public Vector3f solarPos;
    public float size;
    public List<TextureLayerData> textureLayers;

    public ClientSpaceObject(ResourceKey<SpaceObject> key, float size, Vector3f solarPos, List<TextureLayerData> layers)
    {
        this.key = key;
        this.size = size;
        this.solarPos = solarPos;
        this.textureLayers = layers;
    }

    public ResourceKey<SpaceObject> getKey() {
        return key;
    }

    public float getSize()
    {
        return this.size;
    }

    public Vector3f getSolarPos() {
        return solarPos;
    }

    public List<TextureLayerData> getTextureLayers() {
        return textureLayers;
    }

    public void setKey(ResourceKey<SpaceObject> key) {
        this.key = key;
    }

    public void setSolarPos(Vector3f solarPos) {
        this.solarPos = solarPos;
    }

    public void setTextureLayers(List<TextureLayerData> textureLayers) {
        this.textureLayers = textureLayers;
    }

    public CompoundTag serialize(){
        CompoundTag tag = new CompoundTag();

        tag.putString("key", this.key.toString());

        tag.putFloat("size", this.size);

        CompoundTag solarPos = new CompoundTag();
        solarPos.putFloat("x", this.solarPos.x);
        solarPos.putFloat("y", this.solarPos.y);
        solarPos.putFloat("z", this.solarPos.z);

        tag.put("solar_pos", solarPos);

        ListTag textureLayers = new ListTag();
        this.textureLayers.forEach(textureLayer -> textureLayers.add(TextureLayerData.serialize(textureLayer)));
        tag.put("texture_layers", textureLayers);

        return tag;
    }

    public static ClientSpaceObject deserialize(CompoundTag tag){
        ResourceKey<SpaceObject> key = SpaceObject.stringToSpaceObjectKey(tag.getString("key"));

        float size = tag.getFloat("size");

        CompoundTag solarPos = (CompoundTag) tag.get("solar_pos");
        Vector3f solarPosV = new Vector3f(solarPos.getFloat("x"), solarPos.getFloat("y"), solarPos.getFloat("z"));

        ListTag layersTag = tag.getList("texture_layers", Tag.TAG_LIST);
        List<TextureLayerData> textureLayers = new ArrayList<>();
        layersTag.forEach(layertag -> textureLayers.add(TextureLayerData.deserialize((CompoundTag) layertag)));

        return new ClientSpaceObject(key, size, solarPosV, textureLayers);
    }
}
