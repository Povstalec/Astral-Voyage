package net.povstalec.astralvoyage.common.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.entities.PilotSeatEntity;

public class EntitiesInit
{

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AstralVoyage.MODID);

    public static final RegistryObject<EntityType<PilotSeatEntity>> PILOT_SEAT = ENTITIES.register("pilot_seat",
            () -> EntityType.Builder.of(PilotSeatEntity::new, MobCategory.MISC).sized(1, 1).build("pilot_seat"));


    public static void register(IEventBus bus)
    {
        ENTITIES.register(bus);
    }
}
