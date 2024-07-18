package net.povstalec.astralvoyage.common.datapack;

import com.mojang.serialization.Codec;

import net.minecraft.nbt.CompoundTag;

public interface SpaceObjectType
{
    Codec<? extends SpaceObjectType> getType();

    CompoundTag serializeNBT();
    SpaceObjectType deserializeNBT(CompoundTag tag);
}
