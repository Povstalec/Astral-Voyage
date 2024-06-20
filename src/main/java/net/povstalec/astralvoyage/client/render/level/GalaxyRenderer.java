package net.povstalec.astralvoyage.client.render.level;

import javax.annotation.Nullable;

import net.povstalec.astralvoyage.common.util.SphericalCoords;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferBuilder.RenderedBuffer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.RandomSource;
import net.povstalec.astralvoyage.AstralVoyage;
import net.povstalec.stellarview.client.render.level.misc.StellarCoordinates;

public abstract class GalaxyRenderer
{
	@Nullable
	protected VertexBuffer starBuffer;
	
	protected long seed;
	protected short numberOfStars;
	
	protected Vector3f offset = new Vector3f(0, 0, 0);
	protected Vector3f rotation = new Vector3f(0, 0, 0);
	
	public GalaxyRenderer(long seed, short numberOfStars)
	{
		this.seed = seed;
		
		this.numberOfStars = numberOfStars;
	}
	
	protected abstract BufferBuilder.RenderedBuffer getStarBuffer(BufferBuilder bufferBuilder,
			float xOffset, float yOffset, float zOffset,
			float xAxisRotation, float yAxisRotation, float zAxisRotation);
	
	public GalaxyRenderer setStarBuffer(float xOffset, float yOffset, float zOffset,
			float xAxisRotation, float yAxisRotation, float zAxisRotation)
	{
		if(starBuffer != null)
			starBuffer.close();
		
		starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tesselator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		BufferBuilder.RenderedBuffer bufferbuilder$renderedbuffer;
		
		this.offset.x = xOffset;
		this.offset.y = yOffset;
		this.offset.z = zOffset;
		
		this.rotation.x = xAxisRotation;
		this.rotation.y = yAxisRotation;
		this.rotation.z = zAxisRotation;
		
		
		bufferbuilder$renderedbuffer = getStarBuffer(bufferBuilder, xOffset, yOffset, zOffset, xAxisRotation, yAxisRotation, zAxisRotation);
		
		starBuffer.bind();
		starBuffer.upload(bufferbuilder$renderedbuffer);
		VertexBuffer.unbind();
		
		return this;
	}
	
	protected void renderStars(ClientLevel level, Camera camera, float partialTicks, PoseStack stack, Matrix4f projectionMatrix, Runnable setupFog/*,
			Vector3f skyAxisRotation, Vector3f axisRotation*/)
	{
		stack.pushPose();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		FogRenderer.setupNoFog();
		
		/*stack.mulPose(Axis.YP.rotationDegrees(skyAxisRotation.y));
        stack.mulPose(Axis.ZP.rotationDegrees(skyAxisRotation.z));
        stack.mulPose(Axis.XP.rotationDegrees(skyAxisRotation.x));
        
        stack.mulPose(Axis.YP.rotationDegrees(axisRotation.y));
        stack.mulPose(Axis.ZP.rotationDegrees(axisRotation.z));
        stack.mulPose(Axis.XP.rotationDegrees(axisRotation.x));*/
		
		stack.mulPose(Axis.YP.rotationDegrees(rotation.y));
        stack.mulPose(Axis.ZP.rotationDegrees(rotation.z));
        stack.mulPose(Axis.XP.rotationDegrees(rotation.x));
        
		this.starBuffer.bind();
		this.starBuffer.drawWithShader(stack.last().pose(), projectionMatrix, GameRenderer.getPositionColorShader());
		VertexBuffer.unbind();
		
		setupFog.run();
		stack.popPose();
	}
	
	
	
	public static class SpiralGalaxy extends GalaxyRenderer
	{
		private byte numberOfArms;
		
		public SpiralGalaxy(long seed, short starsPerArm, byte numberOfArms)
		{
			super(seed, starsPerArm);
			
			this.numberOfArms = numberOfArms;
		}
		
		@Override
		protected RenderedBuffer getStarBuffer(BufferBuilder bufferBuilder,
				float xOffset, float yOffset, float zOffset,
				float xAxisRotation, float yAxisRotation, float zAxisRotation)
		{
			RandomSource randomsource = RandomSource.create(seed);
			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

			for(int j = 0; j < numberOfArms; j++)
			{
				double rotation = Math.PI * j / ((double) numberOfArms / 2);
				double length = randomsource.nextDouble() + 1.5;
				for(int i = 0; i < numberOfStars; i++)
				{
					double progress = (double) i / numberOfStars;
					
					double phi = length * Math.PI * progress - rotation;
					double r = SphericalCoords.spiralR(5, phi, rotation);
					
					double x =  r * Math.cos(phi) + (randomsource.nextFloat() * 4.0F - 2.0F) * 1 / (progress * 1.5);
					double z =  r * Math.sin(phi) + (randomsource.nextFloat() * 4.0F - 2.0F) * 1 / (progress * 1.5);
					double y =  (randomsource.nextFloat() * 4.0F - 2.0F) * 1 / (progress * 1.5);
					
					//Rotates around X
					double alphaX = x;
					double alphaY = z * Math.sin(xAxisRotation) + y * Math.cos(xAxisRotation);
					double alphaZ = z * Math.cos(xAxisRotation) - y * Math.sin(xAxisRotation);
					
					//Rotates around Z
					double betaX = alphaX * Math.cos(zAxisRotation) - alphaY * Math.sin(zAxisRotation);
					double betaY = - alphaX * Math.sin(zAxisRotation) - alphaY * Math.cos(zAxisRotation);
					double betaZ = alphaZ;
					
					//Rotates around Y
					double gammaX = - betaX * Math.sin(yAxisRotation) - betaZ * Math.cos(yAxisRotation);
					double gammaY = betaY;
					double gammaZ = betaX * Math.cos(yAxisRotation) - betaZ * Math.sin(yAxisRotation);
					
					x = gammaX + xOffset;
					y = gammaY + yOffset;
					z = gammaZ + zOffset;
					
					double distance = x * x + y * y + z * z;
					
					if(AstralVoyage.isStellarViewLoaded())
						StellarViewRendering.createStar(bufferBuilder, randomsource, x, y, z, distance, 1, 1, randomsource.nextLong());
					else
						SpaceRenderer.createStar(bufferBuilder, randomsource, x, y, z, distance, 1, 1, randomsource.nextLong());
				}
			}
			return bufferBuilder.end();
		}
	}
}
