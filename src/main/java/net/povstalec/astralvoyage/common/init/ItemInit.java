package net.povstalec.astralvoyage.common.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.astralvoyage.AstralVoyage;

import java.util.function.Function;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AstralVoyage.MODID);

    public static RegistryObject<BlockItem> registerBlockItem(RegistryObject<?extends Block> block, Function<Item.Properties, Item.Properties> prop){
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), prop.apply(new Item.Properties())));
    }

    public static RegistryObject<BlockItem> registerBlockItem(RegistryObject<? extends Block> block){
        return registerBlockItem(block, prop -> prop);
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
