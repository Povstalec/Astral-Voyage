package net.povstalec.astralvoyage.common.network.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.astralvoyage.common.network.ClientPacketHandler;

public class SpaceObjectUpdateMessage {
	
    public float solarX;
    public float solarY;
    public float solarZ;
    
    public float galacticX;
    public float galacticY;
    public float galacticZ;
    
    public float xAxisRotation;
    public float yAxisRotation;
    public float zAxisRotation;

    public SpaceObjectUpdateMessage(float solarX, float solarY, float solarZ,
    		float galacticX, float galacticY, float galacticZ,
    		float xAxisRotation, float yAxisRotation, float zAxisRotation)
    {
    	this.solarX = solarX;
        this.solarY = solarY;
        this.solarZ = solarZ;
    	
        this.galacticX = galacticX;
        this.galacticY = galacticY;
        this.galacticZ = galacticZ;
    	
        this.xAxisRotation = xAxisRotation;
        this.yAxisRotation = yAxisRotation;
        this.zAxisRotation = zAxisRotation;
    }

    public static void write(SpaceObjectUpdateMessage mes, FriendlyByteBuf buf){
        buf.writeFloat(mes.solarX);
        buf.writeFloat(mes.solarY);
        buf.writeFloat(mes.solarZ);
        
        buf.writeFloat(mes.galacticX);
        buf.writeFloat(mes.galacticY);
        buf.writeFloat(mes.galacticZ);
        
        buf.writeFloat(mes.xAxisRotation);
        buf.writeFloat(mes.yAxisRotation);
        buf.writeFloat(mes.zAxisRotation);
    }

    public static SpaceObjectUpdateMessage read(FriendlyByteBuf buf){
        return new SpaceObjectUpdateMessage(buf.readFloat(), buf.readFloat(), buf.readFloat(),
        		buf.readFloat(), buf.readFloat(), buf.readFloat(), 
        		buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(SpaceObjectUpdateMessage mes, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> ClientPacketHandler.handleSpaceObjectUpdatePacket(mes));
        context.get().setPacketHandled(true);
    }
}
