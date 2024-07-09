package net.povstalec.astralvoyage.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.astralvoyage.common.network.ClientPacketHandler;

import java.util.function.Supplier;

public class PlanetUpdateMessage {

    public CompoundTag tag;

    public PlanetUpdateMessage(CompoundTag tag)
    {
        this.tag = tag;
    }

    public static void write(PlanetUpdateMessage mes, FriendlyByteBuf buf){
        buf.writeNbt(mes.tag);
    }

    public static PlanetUpdateMessage read(FriendlyByteBuf buf){
        return new PlanetUpdateMessage(buf.readNbt());
    }

    public static void handle(PlanetUpdateMessage mes, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> ClientPacketHandler.handlePlanetUpdatePacket(mes));
        context.get().setPacketHandled(true);
    }
}
