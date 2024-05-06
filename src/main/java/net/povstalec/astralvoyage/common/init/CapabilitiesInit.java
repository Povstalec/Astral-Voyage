package net.povstalec.astralvoyage.common.init;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.capability.SpaceshipCapability;

@Mod.EventBusSubscriber(modid = AstralVoyage.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapabilitiesInit {
    public static final Capability<SpaceshipCapability> SPACESHIP = CapabilityManager.get(new CapabilityToken<SpaceshipCapability>() {});

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event){
        event.register(SpaceshipCapability.class);
    }

}
