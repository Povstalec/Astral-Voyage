package net.povstalec.astralvoyage.common.network;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.network.packets.SpaceObjectUpdateMessage;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {

    public static void handleTestSpaceshipDataUpdatePacket(SpaceObjectUpdateMessage mes) {
        getLevel().ifPresent(level -> {
            level.getCapability(CapabilitiesInit.SPACESHIP).ifPresent(cap -> {
                cap.setSpaceObject(mes.locationString);
                cap.setGalacticPostion(mes.galacticX, mes.galacticY, mes.galacticZ);
                cap.setRotation(mes.xAxisRotation, mes.yAxisRotation, mes.zAxisRotation);
            });
        });
    }

    public static Optional<Level> getLevel(){
    	Minecraft minecraft = Minecraft.getInstance();
    	
        return minecraft.level == null ? Optional.empty() : Optional.of(Minecraft.getInstance().level);
    }



}
