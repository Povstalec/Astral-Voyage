package net.povstalec.astralvoyage.common.network.packets.spaceship;

import net.minecraft.network.FriendlyByteBuf;
import net.povstalec.astralvoyage.common.cap.ISpaceshipLevel;

public abstract class SpaceshipData {
    final int id;

    public SpaceshipData(int id){
        this.id = id;
    }

    public SpaceshipData(int id, ISpaceshipLevel spaceship){
        this(id);
        this.createFromSpaceship(spaceship);
    }

    public int getId(){
        return this.id;
    }

    public abstract void serialize(FriendlyByteBuf buf);
    public abstract void deserialize(FriendlyByteBuf buf);
    public abstract void apply(ISpaceshipLevel tardis);
    public abstract void createFromSpaceship(ISpaceshipLevel tardis);


}
