package net.povstalec.astralvoyage.common.util;

import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3f;

public class Rotation {

    public float yaw;
    public float pitch;
    public float roll;

    public Rotation(float yaw, float pitch, float roll)
    {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public static Rotation sphericalToRotation(SphericalCoords sphericalCoords)
    {
        float yaw = (float) sphericalCoords.theta;
        float pitch = (float) sphericalCoords.phi;
        float roll = 0.0F;

        return new Rotation(yaw, pitch, roll);
    }

    public void set(Rotation rotation)
    {
        this.yaw = rotation.yaw;
        this.pitch = rotation.pitch;
        this.roll = rotation.roll;
    }

    public float step()
    {
        return asVector().length();
    }

    public CompoundTag serialize()
    {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("yaw", this.yaw);
        tag.putFloat("pitch",this.pitch);
        tag.putFloat("roll", this.roll);

        return tag;
    }

    public static Rotation deserialize(CompoundTag tag)
    {
        CompoundTag rotationTag = tag.getCompound("rotation");

        float yaw = rotationTag.getFloat("yaw");
        float pitch = rotationTag.getFloat("pitch");
        float roll = rotationTag.getFloat("roll");

        return new Rotation(yaw, pitch, roll);
    }

    public Vector3f asVector()
    {
        return new Vector3f(this.yaw, this.pitch, this.roll);
    }

    @Override
    public String toString()
    {
        return "Rotation[yaw=\"" + yaw + "\", pitch=\"" + pitch + "\", roll=\"" + roll + "\"]";
    }
}
