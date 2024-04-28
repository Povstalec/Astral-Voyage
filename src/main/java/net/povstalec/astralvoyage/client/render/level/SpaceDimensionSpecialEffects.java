package net.povstalec.astralvoyage.client.render.level;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.povstalec.astralvoyage.AstralVoyage;

public class SpaceDimensionSpecialEffects extends DimensionSpecialEffects {
    public static final ResourceLocation EARTH_ORBIT_EFFECTS = new ResourceLocation(AstralVoyage.MODID, "earth_orbit");
    public static final ResourceLocation SOL_ORBIT_EFFECTS = new ResourceLocation(AstralVoyage.MODID, "sol_orbit");
    
    public SpaceDimensionSpecialEffects(float cloudLevel, boolean hasGround, SkyType skyType,
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
    	
        return true;
    }

    @Override
    public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ)
    {
        return false;
    }
    
    
    
    public static class EarthOrbit extends SpaceDimensionSpecialEffects
    {
    	
        public EarthOrbit()
        {
            super(Float.NaN, true, DimensionSpecialEffects.SkyType.NONE, false, false);
        }

        @Override
        public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
        {
        	
        	poseStack.pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360.0F));
            
            //this.renderStars(level, partialTicks, rain, stack, projectionMatrix, setupFog);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            
            Matrix4f lastMatrix = poseStack.last().pose();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            
            //this.renderCelestials(level, partialTicks, stack, lastMatrix, setupFog, bufferbuilder, rain);
            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
    		CelestialRenderer.renderSun(bufferbuilder, lastMatrix, 50.0F);
            
            poseStack.popPose();
        	
            return super.renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
        }
    }
    
    public static class SolOrbit extends SpaceDimensionSpecialEffects
    {
    	
        public SolOrbit()
        {
            super(Float.NaN, true, DimensionSpecialEffects.SkyType.NONE, false, false);
        }

        @Override
        public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
        {
        	
        	poseStack.pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360.0F));
            
            //this.renderStars(level, partialTicks, rain, stack, projectionMatrix, setupFog);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            
            Matrix4f lastMatrix = poseStack.last().pose();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            
            //this.renderCelestials(level, partialTicks, stack, lastMatrix, setupFog, bufferbuilder, rain);
            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
    		CelestialRenderer.renderSun(bufferbuilder, lastMatrix, 100.0F);
            
            poseStack.popPose();
        	
            return super.renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
        }
    }




    public static void registerSkyEffects(RegisterDimensionSpecialEffectsEvent event)
    {
        event.register(SpaceDimensionSpecialEffects.EARTH_ORBIT_EFFECTS, new SpaceDimensionSpecialEffects.EarthOrbit());
        event.register(SpaceDimensionSpecialEffects.SOL_ORBIT_EFFECTS, new SpaceDimensionSpecialEffects.SolOrbit());
    }
}
