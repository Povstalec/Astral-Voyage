package net.povstalec.astralvoyage.common.events;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.povstalec.astralvoyage.common.data.SpaceObjects;
import net.povstalec.astralvoyage.common.datapack.ClientSpaceObject;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.network.packets.TextureLayerData;
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
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = AstralVoyage.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void playerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        MinecraftServer server = event.getEntity().getServer();
        ResourceKey<Level> to = event.getTo();
        ServerLevel levelTo = server.getLevel(to);

        if(levelTo.dimensionTypeId().location().equals(WorldGenInit.SPACE_TYPE.location()))
        {
            List<ClientSpaceObject> list = new ArrayList<>();
            SpaceObjects.get(server).spaceObjects.forEach((id, object) -> list.add(new ClientSpaceObject(object.getKey(), object.getSize(), Optional.of(Double.valueOf(0)), new Vector3f(0), object.getGalacticPos(), TextureLayerData.toDataList(object.getTextureLayers()))));
            levelTo.getCapability(CapabilitiesInit.SPACESHIP).ifPresent(cap -> cap.setRenderObjects(list));
        }
    }

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
    public static void attachWorldCapabilies(AttachCapabilitiesEvent<Level> event) {
        if (event.getObject().dimensionTypeId().location().equals(WorldGenInit.SPACE_TYPE.location()))
            event.addCapability(new ResourceLocation(AstralVoyage.MODID, "spaceship"), new GenericProvider<>(CapabilitiesInit.SPACESHIP, new SpaceshipCapability()));
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
		}
	}

}
