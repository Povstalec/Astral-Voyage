package net.povstalec.astralvoyage.client.render.level;

import java.util.Optional;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.capability.SpaceshipCapability;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;
import net.povstalec.astralvoyage.common.init.CapabilitiesInit;

public class SpaceDimensionSpecialEffects extends DimensionSpecialEffects
{
    public static final ResourceLocation SPACE_EFFECTS = new ResourceLocation(AstralVoyage.MODID, "space_effects");
    
    private GalaxyRenderer galaxy;
    
    public SpaceDimensionSpecialEffects(float cloudLevel, boolean hasGround, SkyType skyType,
                                            boolean forceBrightLightmap, boolean constantAmbientLight)
    {
        super(cloudLevel, hasGround, skyType, forceBrightLightmap, constantAmbientLight);
        galaxy = new GalaxyRenderer.SpiralGalaxy(10842L, (short) 1500, (byte) 4);
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
    	poseStack.pushPose();
    	
    	@NotNull LazyOptional<SpaceshipCapability> capability = getSpaceShipCapability(level);
    	
    	float xAxisRotation = getRotation(capability).x;
    	float yAxisRotation = getRotation(capability).y;
    	float zAxisRotation = getRotation(capability).z;
    	
        poseStack.mulPose(Axis.YP.rotationDegrees(yAxisRotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(xAxisRotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(zAxisRotation));
		
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		
		galaxy.setStarBuffer(getGalacticPosition(capability).x, getGalacticPosition(capability).y, getGalacticPosition(capability).z, 0, 0, 0);
		galaxy.renderStars(level, camera, partialTick, poseStack, projectionMatrix, setupFog);
		
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        RenderSystem.depthMask(true);
        
    	poseStack.popPose();
    	
        return true;
    }

    @Override
    public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ)
    {
        return false;
    }



    public static class Spaceship extends SpaceDimensionSpecialEffects
    {
    	
        public Spaceship()
        {
            super(Float.NaN, true, DimensionSpecialEffects.SkyType.NONE, false, false);
        }
    }




    public static void registerSkyEffects(RegisterDimensionSpecialEffectsEvent event)
    {
        event.register(SpaceDimensionSpecialEffects.SPACE_EFFECTS, new SpaceDimensionSpecialEffects.Spaceship());
    }
    
    public static @NotNull LazyOptional<SpaceshipCapability> getSpaceShipCapability(ClientLevel level)
    {
    	return level.getCapability(CapabilitiesInit.SPACESHIP);
    }
    
    public static Vector3f getGalacticPosition(@NotNull LazyOptional<SpaceshipCapability> capability)
    {
    	Optional<Vector3f> galacticPosition = capability.map(cap -> cap.getGalacticPosition());
    	
    	if(galacticPosition.isPresent())
    		return galacticPosition.get();
    	
    	return new Vector3f(0, 0, 0);
    }
    
    public static Vector3f getRotation(@NotNull LazyOptional<SpaceshipCapability> capability)
    {
    	Optional<Vector3f> rotation = capability.map(cap -> cap.getRotation());
    	
    	if(rotation.isPresent())
    		return rotation.get();
    	
    	return new Vector3f(0, 0, 0);
    }
}
