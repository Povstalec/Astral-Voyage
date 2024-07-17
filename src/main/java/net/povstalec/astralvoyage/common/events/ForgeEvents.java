package net.povstalec.astralvoyage.common.events;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.povstalec.astralvoyage.common.capability.PlanetCapability;
import net.povstalec.astralvoyage.common.data.SpaceObjects;
import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.capability.GenericProvider;
import net.povstalec.astralvoyage.common.capability.SpaceshipCapability;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.init.WorldGenInit;

@Mod.EventBusSubscriber(modid = AstralVoyage.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        if (!level.dimensionTypeId().location().equals(WorldGenInit.SPACE_TYPE.location()))
            return;

        if (entity instanceof Player player) {
            if (player.isCreative() && player.getAbilities().flying)
                return;
            else if (player.isSpectator() && player.getAbilities().flying)
                return;
        }

        Vec3 movementVector = entity.getDeltaMovement();
        entity.setDeltaMovement(movementVector.x(), movementVector.y()+entity.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).getValue()-0.005, movementVector.z());
        if(entity.isShiftKeyDown()){
            entity.setDeltaMovement(movementVector.x(), movementVector.y()+entity.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).getValue()-0.02, movementVector.z());
        }
        entity.fallDistance = 0;
    }

    @SubscribeEvent
    public static void onPlayerEnterSpaceship(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        if(event.getEntity().level().isClientSide())
            return;

        if(event.getEntity().getServer().getLevel(event.getTo()).dimensionTypeId() == WorldGenInit.SPACE_TYPE)
        {
            event.getEntity().getServer().getPlayerList().getPlayer(event.getEntity().getUUID()).getAdvancements().award(event.getEntity().getServer().getAdvancements().getAdvancement(new ResourceLocation(AstralVoyage.MODID, "spaceship_first_use")), "");
        }
    }

    @SubscribeEvent
    public static void attachWorldCapabilies(AttachCapabilitiesEvent<Level> event) {
        if (event.getObject().dimensionTypeId().location().equals(WorldGenInit.SPACE_TYPE.location()))
            event.addCapability(new ResourceLocation(AstralVoyage.MODID, "spaceship"), new GenericProvider<>(CapabilitiesInit.SPACESHIP, new SpaceshipCapability()));

        if(event.getObject().dimensionTypeId().location().equals(WorldGenInit.PLANET_TYPE.location()))
            event.addCapability(new ResourceLocation(AstralVoyage.MODID, "planet"), new GenericProvider<>(CapabilitiesInit.PLANET, new PlanetCapability()));
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event)
    {
        MinecraftServer server = event.getServer();
        SpaceObjects.get(server).updateData(server);
    }
	
	@SubscribeEvent
	public static void onLevelTick(TickEvent.LevelTickEvent event)
	{
		Level level = event.level;
		
		if(event.haveTime() && level != null)
		{
			@NotNull LazyOptional<SpaceshipCapability> capability = level.getCapability(CapabilitiesInit.SPACESHIP);
			capability.ifPresent(cap -> 
			{
				if(cap != null)
				{
					cap.tick(level);
				}
			});

            @NotNull LazyOptional<PlanetCapability> planetCapability = level.getCapability(CapabilitiesInit.PLANET);
            planetCapability.ifPresent(
                    cap -> {
                        if(cap != null)
                            cap.tick(level);
                    }
            );
		}
	}

}
