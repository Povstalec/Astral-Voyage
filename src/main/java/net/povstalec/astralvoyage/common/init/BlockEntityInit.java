package net.povstalec.astralvoyage.common.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.block_entities.SpaceshipMovementBlockEntity;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AstralVoyage.MODID);

    public static final RegistryObject<BlockEntityType<SpaceshipMovementBlockEntity>> MOVEMENT_BLOCK = BLOCK_ENTITIES.register("movement",
            () -> BlockEntityType.Builder.of(SpaceshipMovementBlockEntity::new, BlockInit.SPACESHIP_MOVEMENT_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus)
    {
        BLOCK_ENTITIES.register(eventBus);
    }
}
