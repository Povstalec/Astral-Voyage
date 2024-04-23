package net.povstalec.astralvoyage.common.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.povstalec.astralvoyage.common.network.packets.spaceship.SpaceshipData;

public interface ISpaceshipLevel extends INBTSerializable<CompoundTag> {

    Level getLevel();
    void tick();

    void clientUpdate(SpaceshipData data);
}
