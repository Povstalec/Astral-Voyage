package net.povstalec.astralvoyage.common.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.povstalec.astralvoyage.common.init.SpaceObjectTypeInit;

public class StarType implements SpaceObjectType
{
    public static final Codec<StarType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("luminosity").forGetter(star -> star.luminosity)
    ).apply(instance, StarType::new));

    public float luminosity;
    public StarType(float luminosity)
    {
        this.luminosity = luminosity;
    }

    @Override
    public Codec<? extends SpaceObjectType> getType() {
        return SpaceObjectTypeInit.STAR.get();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("luminosity", this.luminosity);
        return tag;
    }

    @Override
    public SpaceObjectType deserializeNBT(CompoundTag tag) {
        StarType type = new StarType(0);
        type.setLuminosity(tag.getFloat("luminosity"));
        return type;
    }

    public float getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(float luminosity) {
        this.luminosity = luminosity;
    }
}
