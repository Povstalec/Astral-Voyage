package net.povstalec.astralvoyage.client.render.level;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.DimensionSpecialEffectsManager;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.cap.ISpaceshipLevel;
import net.povstalec.astralvoyage.common.datapack.StellarLocation;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;

public class SpaceshipDimensionSpecialEffects extends DimensionSpecialEffects {
    public static final ResourceLocation SPACESHIP_EFFECTS = new ResourceLocation(AstralVoyage.MODID, "spaceship_effects");
    

	private Optional<DimensionSpecialEffects> parentEffects = Optional.empty();
    
    public SpaceshipDimensionSpecialEffects(float cloudLevel, boolean hasGround, SkyType skyType,
                                            boolean forceBrightLightmap, boolean constantAmbientLight)
    {
        super(cloudLevel, hasGround, skyType, forceBrightLightmap, constantAmbientLight);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 biomeFogColor, float daylight)
    {
        return biomeFogColor.multiply((double)(daylight * 0.94F + 0.06F), (double)(daylight * 0.94F + 0.06F), (double)(daylight * 0.91F + 0.09F));
    }

    @Override
    public boolean isFoggyAt(int x, int y)
    {
        return false;
    }

    @Override
    public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix)
    {
        return false;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
    {
    	Optional<StellarLocation> stellarLocation = getStellarLocationFromLevel(level);
    	
    	if(/*parentEffects.isEmpty() && */stellarLocation.isPresent())
    	{
    		parentEffects = Optional.of(DimensionSpecialEffectsManager.getForType(stellarLocation.get().getSpecialEffects()));
    	}
    	
    	if(parentEffects.isPresent())
    		parentEffects.get().renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
    	
        return true;
    }

    @Override
    public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ)
    {
        return false;
    }



    public static class Spaceship extends SpaceshipDimensionSpecialEffects
    {
    	
        public Spaceship()
        {
            super(Float.NaN, true, DimensionSpecialEffects.SkyType.NONE, false, false);
            // skyRenderer =
        }
    }




    public static void registerSkyEffects(RegisterDimensionSpecialEffectsEvent event)
    {
        event.register(SpaceshipDimensionSpecialEffects.SPACESHIP_EFFECTS, new SpaceshipDimensionSpecialEffects.Spaceship());
    }
    
    public static Optional<StellarLocation> getStellarLocationFromLevel(ClientLevel level)
    {
    	@NotNull LazyOptional<ISpaceshipLevel> capability = level.getCapability(CapabilitiesInit.SPACESHIP);
    	
    	Optional<String> stellarLocationID = capability.map(cap -> cap.getStellarLocationID());
    	
    	if(stellarLocationID.isPresent())
    	{
    		if(stellarLocationID.get() != null && ResourceLocation.isValidResourceLocation(stellarLocationID.get()))
    		{
    			Minecraft minecraft = Minecraft.getInstance();
        		ResourceLocation resourceLocation = new ResourceLocation(stellarLocationID.get());
    			
        		ClientPacketListener clientPacketListener = minecraft.getConnection();
        		RegistryAccess registries = clientPacketListener.registryAccess();
        		Registry<StellarLocation> stellarLocationRegistry = registries.registryOrThrow(StellarLocation.REGISTRY_KEY);
        		
        		if(stellarLocationRegistry.containsKey(resourceLocation))
        		{
        			return Optional.of(stellarLocationRegistry.get(resourceLocation));
        		}
    		}
    	}
    	
    	return Optional.empty();
    }
}
