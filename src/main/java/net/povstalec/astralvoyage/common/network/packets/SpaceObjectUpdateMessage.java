package net.povstalec.astralvoyage.common.network.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.astralvoyage.common.network.ClientPacketHandler;

public class SpaceObjectUpdateMessage {
	
    public String locationString;
    
    public float galacticX;
    public float galacticY;
    public float galacticZ;
    
    public float xAxisRotation;
    public float yAxisRotation;
    public float zAxisRotation;

    public SpaceObjectUpdateMessage(String locationString, 
    		float galacticX, float galacticY, float galacticZ,
    		float xAxisRotation, float yAxisRotation, float zAxisRotation)
    {
    	this.locationString = locationString;
    	
        this.galacticX = galacticX;
        this.galacticY = galacticY;
        this.galacticZ = galacticZ;
    	
        this.xAxisRotation = xAxisRotation;
        this.yAxisRotation = yAxisRotation;
        this.zAxisRotation = zAxisRotation;
    }

    public static void write(SpaceObjectUpdateMessage mes, FriendlyByteBuf buf){
        buf.writeUtf(mes.locationString);
        
        buf.writeFloat(mes.galacticX);
        buf.writeFloat(mes.galacticY);
        buf.writeFloat(mes.galacticZ);
        
        buf.writeFloat(mes.xAxisRotation);
        buf.writeFloat(mes.yAxisRotation);
        buf.writeFloat(mes.zAxisRotation);
    }

    public static SpaceObjectUpdateMessage read(FriendlyByteBuf buf){
        return new SpaceObjectUpdateMessage(buf.readUtf(), 
        		buf.readFloat(), buf.readFloat(), buf.readFloat(), 
        		buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(SpaceObjectUpdateMessage mes, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> ClientPacketHandler.handleSpaceObjectUpdatePacket(mes));
        context.get().setPacketHandled(true);
    }
}
