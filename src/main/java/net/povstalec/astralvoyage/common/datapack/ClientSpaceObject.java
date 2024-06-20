package net.povstalec.astralvoyage.common.datapack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.povstalec.astralvoyage.common.util.TextureLayerData;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientSpaceObject {

    public ResourceKey<SpaceObject> key;
    public Optional<Vector3f> galPos;
    public Vector3f solarPos;
    public float size;
    public Optional<Double> orbitStartAngle;
    public List<TextureLayerData> textureLayers;

    public ClientSpaceObject(ResourceKey<SpaceObject> key, float size, Optional<Double> orbitStartAngle, Vector3f solarPos, Optional<Vector3f> galPos, List<TextureLayerData> layers)
    {
        this.key = key;
        this.size = size;
        this.orbitStartAngle = orbitStartAngle;
        this.solarPos = solarPos;
        this.galPos = galPos;
        this.textureLayers = layers;
    }

    public ResourceKey<SpaceObject> getKey() {
        return key;
    }

    public float getSize()
    {
        return this.size;
    }

    public Optional<Vector3f> getGalacticPos()
    {
        return galPos;
    }

    public Optional<Double> getOrbitOffset()
    {
        return this.orbitStartAngle;
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

        tag.putString("key", this.key.location().toString());

        tag.putFloat("size", this.size);

        if(this.orbitStartAngle.isPresent())
            tag.putDouble("orbit_start_angle", this.orbitStartAngle.get());

        CompoundTag solarPos = new CompoundTag();
        solarPos.putFloat("x", this.solarPos.x);
        solarPos.putFloat("y", this.solarPos.y);
        solarPos.putFloat("z", this.solarPos.z);

        if(galPos.isPresent())
        {
            CompoundTag galPos = new CompoundTag();
            galPos.putFloat("x", this.galPos.get().x);
            galPos.putFloat("y", this.galPos.get().y);
            galPos.putFloat("z", this.galPos.get().z);
            tag.put("galactic_pos", galPos);
        }

        tag.put("solar_pos", solarPos);

        ListTag textureLayersTag = new ListTag();
        this.textureLayers.forEach(textureLayer -> textureLayersTag.add(TextureLayerData.serialize(textureLayer)));
        tag.put("texture_layers", textureLayersTag);

        return tag;
    }

    public static ClientSpaceObject deserialize(CompoundTag tag){
        ResourceKey<SpaceObject> key = SpaceObject.stringToSpaceObjectKey(tag.getString("key"));

        float size = tag.getFloat("size");

        Optional<Double> orbitStartAngle = Optional.empty();
        if(tag.contains("orbit_start_angle"))
            orbitStartAngle = Optional.of(tag.getDouble("orbit_start_angle"));



        CompoundTag solarPos = tag.getCompound("solar_pos");
        Vector3f solarPosV = new Vector3f(solarPos.getFloat("x"), solarPos.getFloat("y"), solarPos.getFloat("z"));

        Optional<Vector3f> galPosV = Optional.empty();
        if(tag.contains("galactic_pos"))
        {
            CompoundTag galPos = tag.getCompound("galactic_pos");
            galPosV = Optional.of(new Vector3f(galPos.getFloat("x"), galPos.getFloat("y"), galPos.getFloat("y")));
        }

        ListTag layersTag = tag.getList("texture_layers", Tag.TAG_COMPOUND);
        List<TextureLayerData> textureLayers = new ArrayList<>();
        layersTag.forEach(layertag -> {
            CompoundTag layerTag = (CompoundTag) layertag;
            TextureLayerData data = TextureLayerData.deserialize(layerTag);
            textureLayers.add(data);
        });

        return new ClientSpaceObject(key, size, orbitStartAngle, solarPosV, galPosV, textureLayers);
    }
}
