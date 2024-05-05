package net.povstalec.astralvoyage.client.render.level;

import javax.annotation.Nullable;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferBuilder.RenderedBuffer;
import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.povstalec.stellarview.client.render.level.misc.StellarCoordinates;

public abstract class GalaxyRenderer
{
	private static final float DEFAULT_DISTANCE = 100.0F;
	
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
	
	public static void createStar(BufferBuilder builder, RandomSource randomsource, 
			double x, double y, double z, double starSize, double distance, int[] starColor, double heightDeformation, double widthDeformation)
	{
		distance = 1.0D / Math.sqrt(distance);
		x *= distance;
		y *= distance;
		z *= distance;
		
		// This effectively pushes the Star away from the camera
		// It's better to have them very far away, otherwise they will appear as though they're shaking when the Player is walking
		double starX = x * DEFAULT_DISTANCE;
		double starY = y * DEFAULT_DISTANCE;
		double starZ = z * DEFAULT_DISTANCE;
		
		/* These very obviously represent Spherical Coordinates (r, theta, phi)
		 * 
		 * Spherical equations (adjusted for Minecraft, since usually +Z is up, while in Minecraft +Y is up):
		 * 
		 * r = sqrt(x * x + y * y + z * z)
		 * tetha = arctg(x / z)
		 * phi = arccos(y / r)
		 * 
		 * x = r * sin(phi) * sin(theta)
		 * y = r * cos(phi)
		 * z = r * sin(phi) * cos(theta)
		 * 
		 * Polar equations
		 * z = r * cos(theta)
		 * x = r * sin(theta)
		 */
		
		double sphericalTheta = Math.atan2(x, z);
		double sinTheta = Math.sin(sphericalTheta);
		double cosTheta = Math.cos(sphericalTheta);
		
		double xzLength = Math.sqrt(x * x + z * z);
		double sphericalPhi = Math.atan2(xzLength, y);
		double sinPhi = Math.sin(sphericalPhi);
		double cosPhi = Math.cos(sphericalPhi);
		
		// sin and cos are used to effectively clamp the random number between two values without actually clamping it,
		// wwhich would result in some awkward lines as Stars would be brought to the clamped values
		// Both affect Star size and rotation
		double random = randomsource.nextDouble() * Math.PI * 2.0D;
		double sinRandom = Math.sin(random);
		double cosRandom = Math.cos(random);
		
		if(starColor.length < 3)
			starColor = new int[] {255, 255, 255};
		
		// This loop creates the 4 corners of a Star
		for(int j = 0; j < 4; ++j)
		{
			/* Bitwise AND is there to multiply the size by either 1 or -1 to reach this effect:
			 * Where a coordinate is written as (A,B)
			 * 		(-1,1)		(1,1)
			 * 		x-----------x
			 * 		|			|
			 * 		|			|
			 * 		|			|
			 * 		|			|
			 * 		x-----------x
			 * 		(-1,-1)		(1,-1)
			 * 								|	A	B
			 * 0 & 2 = 000 & 010 = 000 = 0	|	x
			 * 1 & 2 = 001 & 010 = 000 = 0	|	x	x
			 * 2 & 2 = 010 & 010 = 010 = 2	|	x	x
			 * 3 & 2 = 011 & 010 = 010 = 2	|	x	x
			 * 4 & 2 = 100 & 000 = 000 = 0	|		x
			 * 
			 * After you subtract 1 one from each of them, you get this:
			 * j:	0	1	2	3
			 * --------------------
			 * A:	-1	-1	1	1
			 * B:	-1	1	1	-1
			 * Which corresponds to:
			 * UV:	00	01	11	10
			 */
			double aLocation = (double) ((j & 2) - 1) * Mth.clamp(starSize * 20 * distance, 0.1, 0.25); //starSize;
			double bLocation = (double) ((j + 1 & 2) - 1) * Mth.clamp(starSize * 20 * distance, 0.1, 0.25); //starSize;
			
			/* These are the values for cos(random) = sin(random)
			 * (random is simply there to randomize the star rotation)
			 * j:	0	1	2	3
			 * -------------------
			 * A:	0	-2	0	2
			 * B:	-2	0	2	0
			 * 
			 * A and B are there to create a diamond effect on the Y-axis and X-axis respectively
			 * (Pretend it's not as stretched as the slashes make it looked)
			 * Where a coordinate is written as (B,A)
			 * 
			 * 			(0,2)
			 * 			/\
			 * 	 (-2,0)/  \(2,0)
			 * 		   \  /
			 * 			\/
			 * 			(0,-2)
			 * 
			 */
			double height = heightDeformation * (aLocation * cosRandom - bLocation * sinRandom);
			double width = widthDeformation * (bLocation * cosRandom + aLocation * sinRandom);
			
			double heightProjectionY = height * sinPhi; // Y projection of the Star's height
			
			double heightProjectionXZ = - height * cosPhi; // If the Star is angled, the XZ projected height needs to be subtracted from both X and Z 
			
			/* 
			 * projectedX:
			 * Projected height is projected onto the X-axis using sin(theta) and then gets subtracted (added because it's already negative)
			 * Width is projected onto the X-axis using cos(theta) and then gets subtracted
			 * 
			 * projectedZ:
			 * Width is projected onto the Z-axis using sin(theta)
			 * Projected height is projected onto the Z-axis using cos(theta) and then gets subtracted (added because it's already negative)
			 * 
			 */
			double projectedX = heightProjectionXZ * sinTheta - width * cosTheta;
			double projectedZ = width * sinTheta + heightProjectionXZ * cosTheta;
			
			builder.vertex(starX + projectedX, starY + heightProjectionY, starZ + projectedZ).color(starColor[0], starColor[1], starColor[2], 0xAA).endVertex();
		}
	}
	
	protected void renderStars(ClientLevel level, Camera camera, float partialTicks, float rain, PoseStack stack, Matrix4f projectionMatrix, Runnable setupFog/*,
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
					double r = StellarCoordinates.spiralR(5, phi, rotation);
					
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
					
					double starSize = (double) (0.15F + randomsource.nextFloat() * 0.1F); // This randomizes the Star size
					double distance = x * x + y * y + z * z;
					
					int[] starColor = new int[] {255, 255, 255};
					
					createStar(bufferBuilder, randomsource, x, y, z, starSize, distance, starColor, 1, 1);
				}
			}
			return bufferBuilder.end();
		}
	}
}
