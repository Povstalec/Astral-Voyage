package net.povstalec.astralvoyage.common.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.worldgen.dimension.SpaceshipChunkGenerator;

public class WorldGenInit {

    public static ResourceKey<DimensionType> SPACESHIP_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(AstralVoyage.MODID, "spaceship"));

    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, AstralVoyage.MODID);

    public static final RegistryObject<Codec<? extends ChunkGenerator>> SPACESHIP_CHUNK_GENERATOR = CHUNK_GENERATORS.register("spaceship", () -> SpaceshipChunkGenerator.CODEC);

    public static final ResourceKey<Biome> SPACESHIP_BIOME = ResourceKey.create(Registries.BIOME, new ResourceLocation(AstralVoyage.MODID, "spaceship"));

    public static void registerWorldgen(IEventBus bus) {
        CHUNK_GENERATORS.register(bus);
    }

    public static void registerDimensionType(){
        SPACESHIP_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(AstralVoyage.MODID, "spaceship"));
    }



}
