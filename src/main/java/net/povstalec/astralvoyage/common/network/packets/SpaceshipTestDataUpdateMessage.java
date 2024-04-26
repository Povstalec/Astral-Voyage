package net.povstalec.astralvoyage.common.network.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.astralvoyage.common.network.ClientPacketHandler;

public class SpaceshipTestDataUpdateMessage {

    public String testString;

    public SpaceshipTestDataUpdateMessage(String string){
        this.testString = string;
    }

    public static void write(SpaceshipTestDataUpdateMessage mes, FriendlyByteBuf buf){
        buf.writeUtf(mes.testString);
    }

    public static SpaceshipTestDataUpdateMessage read(FriendlyByteBuf buf){
        return new SpaceshipTestDataUpdateMessage(buf.readUtf());
    }

    public static void handle(SpaceshipTestDataUpdateMessage mes, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> ClientPacketHandler.handleTestSpaceshipDataUpdatePacket(mes));
        context.get().setPacketHandled(true);
    }


}
