package net.povstalec.astralvoyage.common.util;

import java.util.List;

import org.joml.Vector3f;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;

public class DiskParameter
{
    public static class AccretionDisk extends DiskParameter
    {
        public static final Codec<AccretionDisk> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("temperature").forGetter(AccretionDisk::getTemperature),
                Codec.FLOAT.fieldOf("luminosity").forGetter(AccretionDisk::getLuminosity),
                Codec.FLOAT.fieldOf("width").forGetter(AccretionDisk::getWidth),

                Codec.FLOAT.listOf().comapFlatMap(f -> Util.fixedSize(f, 3)
                        .map(vec -> new Vector3f(vec.get(0), vec.get(1), vec.get(2))),
                        element -> List.of(element.get(0), element.get(1), element.get(2))).stable().fieldOf("rotation_speed").forGetter(AccretionDisk::getRotationSpeed),
                Codec.FLOAT.listOf().comapFlatMap(f -> Util.fixedSize(f, 3)
                                .map(vec -> new Vector3f(vec.get(0), vec.get(1), vec.get(2))),
                        element -> List.of(element.get(0), element.get(1), element.get(2))).stable().fieldOf("rotation_direction").forGetter(AccretionDisk::getRotationDirection),
                Codec.DOUBLE.fieldOf("anomaly").forGetter(AccretionDisk::getAnomaly)
                ).apply(instance, AccretionDisk::new));

        public float temperature;
        public float luminosity;
        public float width;
        public Vector3f rotationSpeed;
        public Vector3f rotationDirection;
        public double anomaly;

        public AccretionDisk(float temperature, float luminosity, float width, Vector3f rotationSpeed, Vector3f rotationDirection, double anomaly)
        {
            this.temperature = temperature;
            this.luminosity = luminosity;
            this.width = width;
            this.rotationSpeed = rotationSpeed;
            this.rotationDirection = rotationDirection;
            this.anomaly = anomaly;
        }

        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();

            tag.putFloat("temperature", this.temperature);
            tag.putFloat("luminosity", this.luminosity);
            tag.putFloat("width", this.width);

            CompoundTag rotationSpeed = new CompoundTag();
            rotationSpeed.putFloat("x", this.rotationSpeed.x);
            rotationSpeed.putFloat("y", this.rotationSpeed.y);
            rotationSpeed.putFloat("z", this.rotationSpeed.z);
            tag.put("rotation_speed", rotationSpeed);

            CompoundTag rotationDirection = new CompoundTag();
            rotationSpeed.putFloat("x", this.rotationSpeed.x);
            rotationSpeed.putFloat("y", this.rotationSpeed.y);
            rotationSpeed.putFloat("z", this.rotationSpeed.z);
            tag.put("rotation_direction", rotationDirection);

            tag.putDouble("anomaly", this.anomaly);

            return tag;
        }

        public static AccretionDisk deserializeNBT(CompoundTag tag)
        {
            float temperature = tag.getFloat("temperature");
            float luminosity = tag.getFloat("luminosity");
            float width = tag.getFloat("width");

            CompoundTag rotationSpeedTag = tag.getCompound("rotation_speed");
            float rotationSpeedX = rotationSpeedTag.getFloat("x");
            float rotationSpeedY = rotationSpeedTag.getFloat("y");
            float rotationSpeedZ = rotationSpeedTag.getFloat("z");
            Vector3f rotationSpeed = new Vector3f(rotationSpeedX, rotationSpeedY, rotationSpeedZ);

            CompoundTag rotationDirectionTag = tag.getCompound("rotation_speed");
            float rotationDirectionX = rotationDirectionTag.getFloat("x");
            float rotationDirectionY = rotationDirectionTag.getFloat("y");
            float rotationDirectionZ = rotationDirectionTag.getFloat("z");
            Vector3f rotationDirection = new Vector3f(rotationDirectionX, rotationDirectionY, rotationDirectionZ);

            double anomaly = tag.getDouble("anomaly");

            return new AccretionDisk(temperature, luminosity, width, rotationSpeed, rotationDirection, anomaly);
        }

        public float getTemperature()
        {
            return temperature;
        }

        public float getLuminosity()
        {
            return luminosity;
        }

        public float getWidth()
        {
            return width;
        }

        public Vector3f getRotationSpeed()
        {
            return rotationSpeed;
        }

        public Vector3f getRotationDirection()
        {
            return rotationDirection;
        }

        public double getAnomaly()
        {
            return anomaly;
        }
    }

    public static class PlanetaryRing extends DiskParameter
    {
        public static final Codec<PlanetaryRing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.listOf().comapFlatMap(f -> Util.fixedSize(f, 3)
                                .map(vec -> new Vector3f(vec.get(0), vec.get(1), vec.get(2))),
                        element -> List.of(element.get(0), element.get(1), element.get(2))).stable().fieldOf("rotation_speed").forGetter(PlanetaryRing::getRotationSpeed),
                Codec.FLOAT.listOf().comapFlatMap(f -> Util.fixedSize(f, 3)
                                .map(vec -> new Vector3f(vec.get(0), vec.get(1), vec.get(2))),
                        element -> List.of(element.get(0), element.get(1), element.get(2))).stable().fieldOf("rotation_direction").forGetter(PlanetaryRing::getRotationDirection),
                Codec.DOUBLE.fieldOf("anomaly").forGetter(PlanetaryRing::getAnomaly),
                Codec.FLOAT.fieldOf("width").forGetter(PlanetaryRing::getWidth)
                ).apply(instance, PlanetaryRing::new));

        public float width;
        public Vector3f rotationSpeed;
        public Vector3f rotationDirection;
        public double anomaly;

        public PlanetaryRing(Vector3f rotationSpeed, Vector3f rotationDirection, double anomaly, float width)
        {
            this.width = width;
            this.rotationSpeed = rotationSpeed;
            this.rotationDirection = rotationDirection;
            this.anomaly = anomaly;
        }

        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();

            CompoundTag rotationSpeed = new CompoundTag();
            rotationSpeed.putFloat("x", this.rotationSpeed.x);
            rotationSpeed.putFloat("y", this.rotationSpeed.y);
            rotationSpeed.putFloat("z", this.rotationSpeed.z);
            tag.put("rotation_speed", rotationSpeed);

            CompoundTag rotationDirection = new CompoundTag();
            rotationSpeed.putFloat("x", this.rotationSpeed.x);
            rotationSpeed.putFloat("y", this.rotationSpeed.y);
            rotationSpeed.putFloat("z", this.rotationSpeed.z);
            tag.put("rotation_direction", rotationDirection);

            tag.putDouble("anomaly", this.anomaly);
            tag.putFloat("width", this.width);

            return tag;
        }

        public static PlanetaryRing deserializeNBT(CompoundTag tag)
        {
            CompoundTag rotationSpeedTag = tag.getCompound("rotation_speed");
            float rotationSpeedX = rotationSpeedTag.getFloat("x");
            float rotationSpeedY = rotationSpeedTag.getFloat("y");
            float rotationSpeedZ = rotationSpeedTag.getFloat("z");
            Vector3f rotationSpeed = new Vector3f(rotationSpeedX, rotationSpeedY, rotationSpeedZ);

            CompoundTag rotationDirectionTag = tag.getCompound("rotation_speed");
            float rotationDirectionX = rotationDirectionTag.getFloat("x");
            float rotationDirectionY = rotationDirectionTag.getFloat("y");
            float rotationDirectionZ = rotationDirectionTag.getFloat("z");
            Vector3f rotationDirection = new Vector3f(rotationDirectionX, rotationDirectionY, rotationDirectionZ);

            double anomaly = tag.getDouble("anomaly");
            float width = tag.getFloat("width");

            return new PlanetaryRing(rotationSpeed, rotationDirection, anomaly, width);
        }

        public float getWidth()
        {
            return width;
        }

        public Vector3f getRotationSpeed()
        {
            return rotationSpeed;
        }

        public Vector3f getRotationDirection()
        {
            return rotationDirection;
        }

        public double getAnomaly()
        {
            return anomaly;
        }
    }
}
