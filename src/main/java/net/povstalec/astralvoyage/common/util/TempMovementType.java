package net.povstalec.astralvoyage.common.util;

import net.minecraft.util.StringRepresentable;

public enum TempMovementType implements StringRepresentable {
    GALACTIC("galactic"),
    SOLAR("solar");


    private final String name;

    TempMovementType(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
