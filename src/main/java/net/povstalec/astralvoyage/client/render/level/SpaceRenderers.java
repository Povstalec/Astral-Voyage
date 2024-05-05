package net.povstalec.astralvoyage.client.render.level;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.multiplayer.ClientLevel;

public class SpaceRenderers
{
	public static class EarthOrbitRenderer extends SpaceRenderer
	{
		public EarthOrbitRenderer()
		{
			super(10842L, 1500);
		}
		
		@Override
		protected void renderSurface(BufferBuilder bufferbuilder, Matrix4f lastMatrix)
		{
			createCelestialObject(bufferbuilder, lastMatrix, SpaceRenderer.EARTH_LOCATION, 
					1000, 100.0F, new float[] {0.0F, 0.0F, 1.0F, 1.0F});

			RenderSystem.enableBlend();
		}
		
		@Override
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			this.renderSun(bufferbuilder, lastMatrix, 50.0F);
	        
	        this.renderMoon(bufferbuilder, lastMatrix, 30.0F, level.getMoonPhase(), 0, (float) Math.toRadians(180));
	        
		}
	}
	
	public static class SunOrbitRenderer extends SpaceRenderer
	{
		public SunOrbitRenderer()
		{
			super(10842L, 1500);
		}
		
		@Override
		protected void renderSurface(BufferBuilder bufferbuilder, Matrix4f lastMatrix)
		{
			createCelestialObject(bufferbuilder, lastMatrix, SpaceRenderer.SUN_LOCATION, 
					100, 100.0F, new float[] {0.0F, 0.0F, 1.0F, 1.0F});

			RenderSystem.enableBlend();
		}

		@Override
		protected void renderCelestials(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f lastMatrix, Runnable setupFog, BufferBuilder bufferbuilder, float rain)
		{
			
		}
	}
}
