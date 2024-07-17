package net.povstalec.astralvoyage.common.datapack;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.povstalec.astralvoyage.common.init.SpaceObjectTypeInit;
import net.povstalec.astralvoyage.common.util.DiskParameter;
import net.povstalec.astralvoyage.common.util.SpectralClass;
import net.povstalec.astralvoyage.common.util.StarProperties;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class StarType implements SpaceObjectType
{
    public static final Codec<StarType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.either(StringRepresentable.fromEnum(SpectralClass::values),
                    StarProperties.CODEC).fieldOf("star_data").forGetter(StarType::getClassOrProperties),
            DiskParameter.AccretionDisk.CODEC.optionalFieldOf("accretion_disk").forGetter(StarType::getAccretionDisk)
    ).apply(instance, StarType::new));

    public static final String STAR_PROPERTIES = "star_properties";
    public static final String ACCREATION_DISK = "accretion_disk";
    public StarProperties starProperties;
    @Nullable DiskParameter.AccretionDisk disk;
    public StarType(Either<SpectralClass, StarProperties> starClassProperties, Optional<DiskParameter.AccretionDisk> disk)
    {
        starClassProperties.mapBoth(
                spectral -> this.starProperties = spectral.classToProperties(),
                properties -> this.starProperties = properties
        );

        this.disk = disk.orElse(null);
    }

    public Optional<DiskParameter.AccretionDisk> getAccretionDisk()
    {
        return Optional.ofNullable(this.disk);
    }

    @Override
    public Codec<? extends SpaceObjectType> getType()
    {
        return SpaceObjectTypeInit.STAR.get();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();

        if(this.starProperties != null)
            tag.put(STAR_PROPERTIES, this.starProperties.serializeNBT());

        if(this.disk != null)
            tag.put(ACCREATION_DISK, this.disk.serializeNBT());

        return tag;
    }

    @Override
    public SpaceObjectType deserializeNBT(CompoundTag tag)
    {
        StarType type = new StarType(Either.left(SpectralClass.M), null);

        if(tag.contains(STAR_PROPERTIES))
            type.setStarProperties(StarProperties.deserializeNBT(tag.getCompound(STAR_PROPERTIES)));

        if(tag.contains(ACCREATION_DISK))
            type.setDisk(DiskParameter.AccretionDisk.deserializeNBT(tag.getCompound(ACCREATION_DISK)));

        return type;
    }

    public Either<SpectralClass, StarProperties> getClassOrProperties()
    {
        if(this.starProperties != null)
            return Either.right(this.starProperties);

        else return null;
    }

    public StarProperties getStarProperties()
    {
        return starProperties;
    }

    public void setStarProperties(@Nullable StarProperties starProperties)
    {
        this.starProperties = starProperties;
    }

    public void setDisk(@Nullable DiskParameter.AccretionDisk disk)
    {
        this.disk = disk;
    }

    public static StarType randomType(Random random)
    {
        SpectralClass spectralClass = SpectralClass.randomClass(random);
        StarProperties properties = spectralClass.classToProperties();
        return new StarType(Either.right(properties), Optional.empty());
    }
}
