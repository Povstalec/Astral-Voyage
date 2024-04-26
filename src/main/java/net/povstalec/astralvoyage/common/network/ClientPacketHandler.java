package net.povstalec.astralvoyage.common.network;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.network.packets.SpaceshipDataUpdateMessage;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {

    public static void handleTardisUpdatePacket(SpaceshipDataUpdateMessage mes) {
        getLevel().ifPresent(level -> {
            level.getCapability(CapabilitiesInit.SPACESHIP).ifPresent(mes.data::apply);
        });
    }

    public static Optional<Level> getLevel(){
        return Minecraft.getInstance().level == null ? Optional.empty() : Optional.of(Minecraft.getInstance().level);
    }



}
