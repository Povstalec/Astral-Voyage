package net.povstalec.astralvoyage.common.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public interface ISpaceshipLevel extends INBTSerializable<CompoundTag> {

    void tick(Level level);

    void clientUpdate(Level level, String id);
    void setStellarLocationID(String id);
    String getStellarLocationID();
}
