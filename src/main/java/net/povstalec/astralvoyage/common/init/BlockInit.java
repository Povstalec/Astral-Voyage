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

    public static final RegistryObject<SpaceshipMovementBlock> SPACESHIP_MOVEMENT_BLOCK = registerWithItem("spaceship_movement_block", () -> new SpaceshipMovementBlock(BlockBehaviour.Properties.of().forceSolidOn()));


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
