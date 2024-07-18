package net.povstalec.astralvoyage.common.network;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.network.packets.PlanetRenderUpdateMessage;
import net.povstalec.astralvoyage.common.network.packets.PlanetUpdateMessage;
import net.povstalec.astralvoyage.common.network.packets.RenderObjectUpdateMessage;
import net.povstalec.astralvoyage.common.network.packets.SpaceObjectUpdateMessage;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {

    public static void handleSpaceObjectUpdatePacket(SpaceObjectUpdateMessage mes) {
        getLevel().ifPresent(level ->
        {
            level.getCapability(CapabilitiesInit.SPACESHIP).ifPresent(
            cap -> {
                cap.deserializeNBT(mes.tag);
            });
        });
    }

    public static void handlePlanetUpdatePacket(PlanetUpdateMessage mes)
    {
        getLevel().ifPresent(level ->
        {
            level.getCapability(CapabilitiesInit.PLANET).ifPresent(
            cap -> {
                cap.deserializeNBT(mes.tag);
            });
        });
    }

    public static void handlePlanetRenderUpdatePacket(PlanetRenderUpdateMessage mes)
    {
        getLevel().ifPresent(
        level -> {
            level.getCapability(CapabilitiesInit.PLANET).ifPresent(
            cap -> {
                   cap.setRenderObjects(mes.objects);
            });
        });
    }

    public static void handleRenderObjectUpdatePacket(RenderObjectUpdateMessage mes) {
        getLevel().ifPresent(level -> {
            level.getCapability(CapabilitiesInit.SPACESHIP).ifPresent(cap -> {
                cap.setRenderObjects(mes.objects);
            });
        });
    }

    public static Optional<Level> getLevel(){
    	Minecraft minecraft = Minecraft.getInstance();
    	
        return minecraft.level == null ? Optional.empty() : Optional.of(Minecraft.getInstance().level);
    }
}
