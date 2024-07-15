package net.povstalec.astralvoyage.common.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.povstalec.astralvoyage.common.init.SpaceObjectTypeInit;
import net.povstalec.astralvoyage.common.util.SpectralClass;

public class StarType implements SpaceObjectType
{
    public static final Codec<StarType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StringRepresentable.fromEnum(SpectralClass::values).fieldOf("spectral_class").forGetter(StarType::getSpectralClass)
    ).apply(instance, StarType::new));

    public SpectralClass spectral;
    public StarType(SpectralClass spectral)
    {
        this.spectral = spectral;
    }

    @Override
    public Codec<? extends SpaceObjectType> getType() {
        return SpaceObjectTypeInit.STAR.get();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("spectral_class", this.spectral.getSerializedName());
        return tag;
    }

    @Override
    public SpaceObjectType deserializeNBT(CompoundTag tag) {
        StarType type = new StarType(SpectralClass.M);
        type.setSpectral(SpectralClass.valueOf(tag.getString("spectral_class")));
        return type;
    }

    public SpectralClass getSpectralClass() {
        return this.spectral;
    }

    public void setSpectral(SpectralClass spectral) {
        this.spectral = spectral;
    }
}
