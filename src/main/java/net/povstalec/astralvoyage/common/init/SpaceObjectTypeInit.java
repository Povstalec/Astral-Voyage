package net.povstalec.astralvoyage.common.init;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.datapack.SpaceObjectType;
import net.povstalec.astralvoyage.common.datapack.StarType;
import net.povstalec.astralvoyage.common.util.RegistryDispatcher;
import net.povstalec.astralvoyage.common.util.SpectralClass;

import java.util.HashMap;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = AstralVoyage.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpaceObjectTypeInit {

    public static final HashMap<String, SpaceObjectType> TYPE_SET = new HashMap<>();
    public static final RegistryDispatcher<SpaceObjectType> OBJECT_TYPE_DISPATCHER = RegistryDispatcher.makeDispatchForgeRegistry(
            FMLJavaModLoadingContext.get().getModEventBus(),
            new ResourceLocation(AstralVoyage.MODID, "space_object_type"),
            SpaceObjectType::getType, // using a method reference here seems to confuse eclipse
            builder -> {}
    );

    public static final RegistryObject<Codec<StarType>> STAR = register("star",
            () -> StarType.CODEC, new StarType(Either.left(SpectralClass.M)));

    public static <S extends SpaceObjectType> RegistryObject<Codec<S>> register(String name, Supplier<Codec<S>> supplier, S type)
    {
        TYPE_SET.put(name, type);
        return OBJECT_TYPE_DISPATCHER.registry().register(name, supplier);
    }
}
