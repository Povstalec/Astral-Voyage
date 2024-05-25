package net.povstalec.astralvoyage.common.init;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.blocks.SpaceshipGeneratorBlock;
import net.povstalec.astralvoyage.common.blocks.SpaceshipMovementBlock;

public class BlockInit {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AstralVoyage.MODID);

    public static final RegistryObject<SpaceshipGeneratorBlock> SPACESHIP_GENERATOR_BLOCK = registerWithItem("spaceship_generator_block", () -> new SpaceshipGeneratorBlock(BlockBehaviour.Properties.of().forceSolidOn()));

    public static final RegistryObject<SpaceshipMovementBlock> SPACESHIP_MOVEMENT_GALACTIC_X_BLOCK = registerWithItem("spaceship_movement_galactic_x_block", () -> new SpaceshipMovementBlock(BlockBehaviour.Properties.of().forceSolidOn(), Direction.Axis.X, true));
    public static final RegistryObject<SpaceshipMovementBlock> SPACESHIP_MOVEMENT_GALACTIC_Y_BLOCK = registerWithItem("spaceship_movement_galactic_y_block", () -> new SpaceshipMovementBlock(BlockBehaviour.Properties.of().forceSolidOn(), Direction.Axis.Y, true));
    public static final RegistryObject<SpaceshipMovementBlock> SPACESHIP_MOVEMENT_GALACTIC_Z_BLOCK = registerWithItem("spaceship_movement_galactic_z_block", () -> new SpaceshipMovementBlock(BlockBehaviour.Properties.of().forceSolidOn(), Direction.Axis.Z, true));

    public static final RegistryObject<SpaceshipMovementBlock> SPACESHIP_MOVEMENT_SOLAR_X_BLOCK = registerWithItem("spaceship_movement_solar_x_block", () -> new SpaceshipMovementBlock(BlockBehaviour.Properties.of().forceSolidOn(), Direction.Axis.X, false));
    public static final RegistryObject<SpaceshipMovementBlock> SPACESHIP_MOVEMENT_SOLAR_Y_BLOCK = registerWithItem("spaceship_movement_solar_y_block", () -> new SpaceshipMovementBlock(BlockBehaviour.Properties.of().forceSolidOn(), Direction.Axis.Y, false));
    public static final RegistryObject<SpaceshipMovementBlock> SPACESHIP_MOVEMENT_SOLAR_Z_BLOCK = registerWithItem("spaceship_movement_solar_z_block", () -> new SpaceshipMovementBlock(BlockBehaviour.Properties.of().forceSolidOn(), Direction.Axis.Z, false));


    public static <T extends Block> RegistryObject<T> registerWithItem(String name, final Supplier<T> block, Function<Item.Properties, Item.Properties> properties){
        final RegistryObject<T> reg = BLOCKS.register(name, block);
        ItemInit.ITEMS.register(name, () -> new BlockItem(reg.get(), properties.apply(new Item.Properties())));
        return reg;
    }

    public static <T extends Block> RegistryObject<T> registerWithItem(String name, final Supplier<T> block){
        return registerWithItem(name, block, p -> p);
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

}
