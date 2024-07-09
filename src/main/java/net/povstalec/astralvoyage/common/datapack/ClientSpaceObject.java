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
}
