package net.povstalec.astralvoyage.common.util;

import net.minecraft.util.StringRepresentable;

public enum EnumMovementType implements StringRepresentable {

    SOLAR("solar"),
    GALACTIC("galactic");

    private String type;
    EnumMovementType(String type)
    {
        this.type = type;
    }

    @Override
    public String getSerializedName() {
        return this.type;
    }
}
