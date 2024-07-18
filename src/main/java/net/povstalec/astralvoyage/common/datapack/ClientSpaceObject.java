package net.povstalec.astralvoyage.common.datapack;

import java.util.List;

import javax.annotation.Nullable;

import org.joml.Vector3f;

import net.minecraft.resources.ResourceKey;
import net.povstalec.astralvoyage.common.util.TextureLayerData;

public class ClientSpaceObject {

    public ResourceKey<SpaceObject> key;
    @Nullable public Vector3f galPos;
    public Vector3f solarPos;
    public float size;
    @Nullable Double orbitStartAngle;
    public List<TextureLayerData> textureLayers;

    public ClientSpaceObject(ResourceKey<SpaceObject> key, float size, @Nullable Double orbitStartAngle, Vector3f solarPos, @Nullable Vector3f galPos, List<TextureLayerData> layers)
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

    @Nullable
    public Vector3f getGalacticPos()
    {
        return galPos;
    }

    @Nullable
    public Double getOrbitOffset()
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
}
