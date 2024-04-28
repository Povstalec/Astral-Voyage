package net.povstalec.astralvoyage.client.render.level;

import java.util.Optional;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.DimensionSpecialEffectsManager;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.cap.ISpaceshipLevel;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;

public class SpaceshipDimensionSpecialEffects extends DimensionSpecialEffects {
    public static final ResourceLocation SPACESHIP_EFFECTS = new ResourceLocation(AstralVoyage.MODID, "spaceship_effects");
    

	private Optional<DimensionSpecialEffects> parentEffects = Optional.empty();
    
    public SpaceshipDimensionSpecialEffects(float cloudLevel, boolean hasGround, SkyType skyType,
                                            boolean forceBrightLightmap, boolean constantAmbientLight)
    {
        super(cloudLevel, hasGround, skyType, forceBrightLightmap, constantAmbientLight);
    }

    @Nullable
    public float[] getSunriseColor(float p_108872_, float p_108873_)
    {
          return new float[] {0, 0, 0, 0};
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
    	@NotNull LazyOptional<ISpaceshipLevel> capability = getSpaceShipCapability(level);
    	Optional<ResourceLocation> effects = getEffectsFromLevel(capability);
    	float xAxisRotation = getXAxisRotation(capability);
    	float yAxisRotation = getYAxisRotation(capability);
    	float zAxisRotation = getZAxisRotation(capability);
    	
    	if(/*parentEffects.isEmpty() && */effects.isPresent())
    	{
    		parentEffects = Optional.of(DimensionSpecialEffectsManager.getForType(effects.get()));
    	}
    	

        poseStack.mulPose(Axis.YP.rotationDegrees(yAxisRotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(xAxisRotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(zAxisRotation));
    	
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
        }
    }




    public static void registerSkyEffects(RegisterDimensionSpecialEffectsEvent event)
    {
        event.register(SpaceshipDimensionSpecialEffects.SPACESHIP_EFFECTS, new SpaceshipDimensionSpecialEffects.Spaceship());
    }
    
    public static @NotNull LazyOptional<ISpaceshipLevel> getSpaceShipCapability(ClientLevel level)
    {
    	return level.getCapability(CapabilitiesInit.SPACESHIP);
    }
    
    public static Optional<ResourceLocation> getEffectsFromLevel(@NotNull LazyOptional<ISpaceshipLevel> capability)
    {
    	Optional<String> effects = capability.map(cap -> cap.getEffects());
    	
    	if(effects.isPresent())
    	{
    		if(effects.get() != null && ResourceLocation.isValidResourceLocation(effects.get()))
    		{
    			return Optional.of(new ResourceLocation(effects.get()));
    		}
    	}
    	
    	return Optional.empty();
    }
    
    public static float getXAxisRotation(@NotNull LazyOptional<ISpaceshipLevel> capability)
    {
    	Optional<Float> rotation = capability.map(cap -> cap.getXAxisRotation());
    	
    	if(rotation.isPresent())
    		return rotation.get();
    	
    	return 0;
    }
    
    public static float getYAxisRotation(@NotNull LazyOptional<ISpaceshipLevel> capability)
    {
    	Optional<Float> rotation = capability.map(cap -> cap.getYAxisRotation());
    	
    	if(rotation.isPresent())
    		return rotation.get();
    	
    	return 0;
    }
    
    public static float getZAxisRotation(@NotNull LazyOptional<ISpaceshipLevel> capability)
    {
    	Optional<Float> rotation = capability.map(cap -> cap.getZAxisRotation());
    	
    	if(rotation.isPresent())
    		return rotation.get();
    	
    	return 0;
    }
}
