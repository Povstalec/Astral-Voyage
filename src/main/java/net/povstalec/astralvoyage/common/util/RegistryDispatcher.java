package net.povstalec.astralvoyage.common.util;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

/**
 * Helper for creating a deferred register and dispatch codec for a custom registry of serializer types.
 * This allows you to register serializers for type-dispatched data, e.g. consider the following json:
 * <pre>
 * {
 *   "type": "yourmod:sometype"
 *   "data": 5
 * }
 * </pre>
 * Here, "yourmod:sometype" refers to a registered codec that defines how to read the rest of the json object.
 * @param <T> Data type -- the things that get parsed from jsons
 * @param dispatcherCodec The codec for the serializer type.
 * @param dispatchedCodec The codec for the data type (this is what you would use for reading/writing json data)
 * @param registry The primary DeferredRegister for the serializer registry. Should only be used by the mod that created the registry.
 * @param registryGetter Supplier for the backing serializer registry
 */
public record RegistryDispatcher<T>(Codec<Codec<? extends T>> dispatcherCodec, Codec<T> dispatchedCodec, DeferredRegister<Codec<? extends T>> registry, Supplier<IForgeRegistry<Codec<? extends T>>> registryGetter)
{
    /**
     * Helper method for creating and registering a DeferredRegister for a registry of serializers.
     * @param <T> Data type -- the things that get parsed from jsons
     * @param modBus mod bus obtained via FMLJavaModLoadingContext.get().getModEventBus()
     * @param registryId The ID of your registry. Names should be singular to follow mojang's naming convention, e.g. "block", "bird"
     * @param typeLookup A function to get the registered serializer for a given T (e.g. RuleTest::getType)
     * @param extraSettings Additional registry configuration if necessary.
     * @return Dispatch codec and a DeferredRegister for a new custom registry of serializers;
     * the deferred register will have been subscribed, and a forge registry will be created for it.
     */
    public static <T> RegistryDispatcher<T> makeDispatchForgeRegistry(
            final IEventBus modBus,
            final ResourceLocation registryId,
            final Function<T,? extends Codec<? extends T>> typeLookup,
            final Consumer<RegistryBuilder<Codec<? extends T>>> extraSettings)
    {
        DeferredRegister<Codec<? extends T>> deferredRegister = DeferredRegister.create(registryId, registryId.getNamespace());
        Supplier<RegistryBuilder<Codec<? extends T>>> builderFactory = () ->
        {
            RegistryBuilder<Codec<? extends T>> builder = new RegistryBuilder<>();
            extraSettings.accept(builder);
            return builder;
        };
        Supplier<IForgeRegistry<Codec<? extends T>>> registryGetter = deferredRegister.makeRegistry(builderFactory);
        Codec<Codec<? extends T>> dispatcherCodec = ResourceLocation.CODEC.xmap(
                id -> registryGetter.get().getValue(id),
                codec -> registryGetter.get().getKey(codec));
        Codec<T> dispatchedCodec = dispatcherCodec.dispatch(typeLookup, Function.identity());
        deferredRegister.register(modBus);

        return new RegistryDispatcher<>(dispatcherCodec, dispatchedCodec, deferredRegister, registryGetter);
    }
}