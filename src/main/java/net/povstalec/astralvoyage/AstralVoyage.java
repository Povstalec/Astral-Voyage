package net.povstalec.astralvoyage;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.povstalec.astralvoyage.client.render.level.SpaceDimensionSpecialEffects;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.init.BlockInit;
import net.povstalec.astralvoyage.common.init.ItemInit;
import net.povstalec.astralvoyage.common.init.ItemTabsInit;
import net.povstalec.astralvoyage.common.init.WorldGenInit;
import net.povstalec.astralvoyage.common.network.AVNetwork;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AstralVoyage.MODID)
public class AstralVoyage
{
    
    public static final String MODID = "astralvoyage";
    
    public static final Logger LOGGER = LogUtils.getLogger();

    public AstralVoyage()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        BlockInit.register(modEventBus);
        ItemInit.register(modEventBus);
        ItemTabsInit.register(modEventBus);
        WorldGenInit.registerWorldgen(modEventBus);
        
        modEventBus.addListener((DataPackRegistryEvent.NewRegistry event) -> 
        {
            event.dataPackRegistry(SpaceObject.REGISTRY_KEY, SpaceObject.CODEC, SpaceObject.CODEC);
        });
        
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        AVNetwork.registerPackets();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }

        @SubscribeEvent
        public static void registerDimensionalEffects(RegisterDimensionSpecialEffectsEvent event){
            SpaceDimensionSpecialEffects.registerSkyEffects(event);
        }
    }
}
