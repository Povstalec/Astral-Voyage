package net.povstalec.astralvoyage.common.network.packets;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.astralvoyage.common.network.ClientPacketHandler;

public class SpaceObjectUpdateMessage {
	
    public CompoundTag tag;

    public SpaceObjectUpdateMessage(CompoundTag tag)
    {
    	this.tag = tag;
    }

    public static void write(SpaceObjectUpdateMessage mes, FriendlyByteBuf buf){
        buf.writeNbt(mes.tag);
    }

    public static SpaceObjectUpdateMessage read(FriendlyByteBuf buf){
        return new SpaceObjectUpdateMessage(buf.readNbt());
    }

    public static void handle(SpaceObjectUpdateMessage mes, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> ClientPacketHandler.handleSpaceObjectUpdatePacket(mes));
        context.get().setPacketHandled(true);
    }
}
