package net.povstalec.astralvoyage.common.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public interface ISpaceshipLevel extends INBTSerializable<CompoundTag> {

    Level getLevel();
    void tick();

    void clientUpdate();
    void setString(String string);
    String getString();
}
