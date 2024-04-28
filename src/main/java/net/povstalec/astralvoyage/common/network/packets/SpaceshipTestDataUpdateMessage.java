package net.povstalec.astralvoyage.common.network.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.astralvoyage.common.network.ClientPacketHandler;

public class SpaceshipTestDataUpdateMessage {

    public String effects;
    public float xAxisRotation;
    public float yAxisRotation;
    public float zAxisRotation;

    public SpaceshipTestDataUpdateMessage(String effects, float xAxisRotation, float yAxisRotation, float zAxisRotation){
        this.effects = effects;
        this.xAxisRotation = xAxisRotation;
        this.yAxisRotation = yAxisRotation;
        this.zAxisRotation = zAxisRotation;
    }

    public static void write(SpaceshipTestDataUpdateMessage mes, FriendlyByteBuf buf){
        buf.writeUtf(mes.effects);
        buf.writeFloat(mes.xAxisRotation);
        buf.writeFloat(mes.yAxisRotation);
        buf.writeFloat(mes.zAxisRotation);
    }

    public static SpaceshipTestDataUpdateMessage read(FriendlyByteBuf buf){
        return new SpaceshipTestDataUpdateMessage(buf.readUtf(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(SpaceshipTestDataUpdateMessage mes, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> ClientPacketHandler.handleTestSpaceshipDataUpdatePacket(mes));
        context.get().setPacketHandled(true);
    }


}
