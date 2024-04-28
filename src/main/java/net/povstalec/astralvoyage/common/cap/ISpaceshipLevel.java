package net.povstalec.astralvoyage.common.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public interface ISpaceshipLevel extends INBTSerializable<CompoundTag> {

    void tick(Level level);

    void clientUpdate(Level level);
    void setEffects(String effects);
    String getEffects();
    void setRotation(float xAxisRotation, float yAxisRotation, float zAxisRotation);
    float getXAxisRotation();
    float getYAxisRotation();
    float getZAxisRotation();
}
