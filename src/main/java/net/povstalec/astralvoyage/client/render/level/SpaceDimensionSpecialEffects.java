package net.povstalec.astralvoyage.client.render.level;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import net.povstalec.astralvoyage.common.datapack.ClientSpaceObject;
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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.astralvoyage.common.capability.SpaceshipCapability;
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
    	
    	Vector3f rotation = getRotation(capability);
    	Vector3f oldRotation = getOldRotation(capability);
        List<ClientSpaceObject> renderObjects = getRenderObjects(capability);

    	float xAxisRotation =  Mth.lerp(partialTick, oldRotation.x, rotation.x);
    	float yAxisRotation = Mth.lerp(partialTick, oldRotation.y, rotation.y);
    	float zAxisRotation =Mth.lerp(partialTick, oldRotation.z, rotation.z);
    	
        poseStack.mulPose(Axis.YP.rotationDegrees(yAxisRotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(xAxisRotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(zAxisRotation));
		
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

    	Vector3f galacticPosition = getGalacticPosition(capability);
    	Vector3f oldGalacticPosition = getOldGalacticPosition(capability);

    	float galacticX =  Mth.lerp(partialTick, oldGalacticPosition.x, galacticPosition.x);
    	float galacticY = Mth.lerp(partialTick, oldGalacticPosition.y, galacticPosition.y);
    	float galacticZ =Mth.lerp(partialTick, oldGalacticPosition.z, galacticPosition.z);

		galaxy.setStarBuffer(galacticX, galacticY, galacticZ, 0, 0, 0);
		galaxy.renderStars(level, camera, partialTick, poseStack, projectionMatrix, setupFog);
		
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        renderObjects.forEach(object -> SpaceObjectRenderer.renderSurface(bufferbuilder, poseStack.last().pose(), object, getDistanceToObject(capability, object), getVectorToObject(capability, object), getDegreeToObject(capability, object)));
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

    public static float getDegreeToObject(@NotNull LazyOptional<SpaceshipCapability> capability, ClientSpaceObject object)
    {
        Vector3f shipPos = capability.map(cap -> cap.getSolarPosition()).get();
        float x = shipPos.x*object.solarPos.x+shipPos.y*object.solarPos.y+shipPos.z*object.solarPos.z;
        float y = (float) Math.sqrt(shipPos.x*shipPos.x+shipPos.y*shipPos.y+shipPos.z*shipPos.z);
        float z = (float) Math.sqrt(object.solarPos.x*object.solarPos.x+object.solarPos.y*object.solarPos.y+object.solarPos.z*object.solarPos.z);

        return (float) Math.acos(x/(y*z));
    }

    public static Vector3f getVectorToObject(@NotNull LazyOptional<SpaceshipCapability> capability, ClientSpaceObject object)
    {
        AtomicReference<Vector3f> spaceshipPos = new AtomicReference<>(new Vector3f());
        capability.ifPresent(ship -> spaceshipPos.set(ship.getSolarPosition()));
        return object.getSolarPos().sub(spaceshipPos.get());
    }

    public static float getDistanceToObject(@NotNull LazyOptional<SpaceshipCapability> capability, ClientSpaceObject object)
    {
        AtomicReference<Vector3f> spaceshipPos = new AtomicReference<>(new Vector3f());
        capability.ifPresent(ship -> spaceshipPos.set(ship.getSolarPosition()));
        return Vector3f.distance(spaceshipPos.get().x, spaceshipPos.get().y, spaceshipPos.get().z, object.getSolarPos().x, object.getSolarPos().y, object.getSolarPos().z);
    }

    public static List<ClientSpaceObject> getRenderObjects(@NotNull LazyOptional<SpaceshipCapability> capability)
    {
        Optional<List<ClientSpaceObject>> renderObjects = capability.map(cap -> cap.getRenderObjects());

        if(renderObjects.isPresent()){
            return renderObjects.get();
        }

        return Lists.newArrayList();
    }

    public static Vector3f getGalacticPosition(@NotNull LazyOptional<SpaceshipCapability> capability)
    {
    	Optional<Vector3f> galacticPosition = capability.map(cap -> cap.getGalacticPosition());
    	
    	if(galacticPosition.isPresent())
    		return galacticPosition.get();
    	
    	return new Vector3f(0, 0, 0);
    }

    public static Vector3f getOldGalacticPosition(@NotNull LazyOptional<SpaceshipCapability> capability)
    {
    	Optional<Vector3f> oldGalacticPosition = capability.map(cap -> cap.getOldGalacticPosition());

    	if(oldGalacticPosition.isPresent())
    		return oldGalacticPosition.get();

    	return new Vector3f(0, 0, 0);
    }
    
    public static Vector3f getRotation(@NotNull LazyOptional<SpaceshipCapability> capability)
    {
    	Optional<Vector3f> rotation = capability.map(cap -> cap.getRotation());
    	
    	if(rotation.isPresent())
    		return rotation.get();
    	
    	return new Vector3f(0, 0, 0);
    }

    public static Vector3f getOldRotation(@NotNull LazyOptional<SpaceshipCapability> capability)
    {
    	Optional<Vector3f> oldRotation = capability.map(cap -> cap.getOldRotation());

    	if(oldRotation.isPresent())
    		return oldRotation.get();

    	return new Vector3f(0, 0, 0);
    }
}
