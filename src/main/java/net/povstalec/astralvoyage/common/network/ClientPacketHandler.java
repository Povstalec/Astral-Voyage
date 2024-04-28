package net.povstalec.astralvoyage.common.network;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.network.packets.SpaceshipTestDataUpdateMessage;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {

    public static void handleTestSpaceshipDataUpdatePacket(SpaceshipTestDataUpdateMessage mes) {
        getLevel().ifPresent(level -> {
            level.getCapability(CapabilitiesInit.SPACESHIP).ifPresent(cap -> {
                cap.setStellarLocationID(mes.testString);
            });
        });
    }

    public static Optional<Level> getLevel(){
    	Minecraft minecraft = Minecraft.getInstance();
    	
        return minecraft.level == null ? Optional.empty() : Optional.of(Minecraft.getInstance().level);
    }



}
