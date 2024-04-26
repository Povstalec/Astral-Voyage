package net.povstalec.astralvoyage.common.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.cap.GenericProvider;
import net.povstalec.astralvoyage.common.cap.SpaceshipCapability;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;
import net.povstalec.astralvoyage.common.init.WorldGenInit;

@Mod.EventBusSubscriber(modid = AstralVoyage.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        if (!level.dimensionTypeId().location().equals(WorldGenInit.SPACESHIP_TYPE.location()))
            return;

        if (entity instanceof Player player) {
            if (player.isCreative() && player.getAbilities().flying)
                return;
            else if (player.isSpectator() && player.getAbilities().flying)
                return;
        }

        Vec3 movementVector = entity.getDeltaMovement();
        entity.setDeltaMovement(movementVector.x(), movementVector.y()+entity.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).getValue(), movementVector.z());
        entity.setSwimming(true);
        if(entity.isShiftKeyDown()){
            entity.setDeltaMovement(movementVector.x(), movementVector.y()+entity.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).getValue()-0.02, movementVector.z());
        }
        entity.fallDistance = 0;
    }

    @SubscribeEvent
    public static void attachWorldCapabilies(AttachCapabilitiesEvent<Level> event){
        if(event.getObject().dimensionTypeId().location().equals(WorldGenInit.SPACESHIP_TYPE.location()))
            event.addCapability(new ResourceLocation(AstralVoyage.MODID, "spaceship"), new GenericProvider<>(CapabilitiesInit.SPACESHIP, new SpaceshipCapability(event.getObject())));
     }

}
