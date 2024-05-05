package net.povstalec.astralvoyage.client.render.level;

import java.util.Random;

import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.povstalec.astralvoyage.AstralVoyage;

public abstract class SpaceRenderer
{
	private static final float DEFAULT_DISTANCE = 100.0F;
	
	private static final float MIN_STAR_SIZE = 0.15F;
	private static final float MAX_STAR_SIZE = 0.25F;
	
	private static final int MIN_STAR_BRIGHTNESS = 170; // 0xAA
	private static final int MAX_STAR_BRIGHTNESS = 255; // 0xFF
	
	public static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
	public static final ResourceLocation MOON_PHASES_LOCATION = new ResourceLocation("textures/environment/moon_phases.png");
	public static final ResourceLocation EARTH_LOCATION = new ResourceLocation(AstralVoyage.MODID, "textures/environment/planets/earth.png");
	
	private static float randomSize(long seed)
	{
		Random random = new Random(seed);
		
		return random.nextFloat(MIN_STAR_SIZE, MAX_STAR_SIZE);
	}
	
	private static int randomBrightness(long seed)
	{
		Random random = new Random(seed);
		
		return random.nextInt(MIN_STAR_BRIGHTNESS, MAX_STAR_BRIGHTNESS);
	}
	
	public static void createStar(BufferBuilder builder, RandomSource randomsource, 
			double x, double y, double z, double distance, double heightDeformation, double widthDeformation, long seed)
	{
		int[] starColor = new int[] {255, 255, 255};
		
		int alpha = randomBrightness(seed); // 0xAA is the default
		int minAlpha = (alpha - 0xAA) * 2 / 3;

		double starSize = (double) randomSize(seed); // This randomizes the Star size
		double maxStarSize = 0.2 + starSize * 1 / 5;
		double minStarSize = starSize * 3 / 5;
		
		if(distance > 40)
			alpha -= 2 * (int) Math.round(distance);
		
		if(alpha < minAlpha)
			alpha = minAlpha;
		
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
			double aLocation = (double) ((j & 2) - 1) * Mth.clamp(starSize * 20 * distance, minStarSize, maxStarSize); //starSize;
			double bLocation = (double) ((j + 1 & 2) - 1) * Mth.clamp(starSize * 20 * distance, minStarSize, maxStarSize); //starSize;
			
			/* These are the values for cos(random) = sin(random)
			 * (random is simply there to randomize the star rotation)
			 * j:	0	1	2	3
			 * -------------------
			 * A:	0	-2	0	2
			 * B:	-2	0	2	0
			 * 
			 * A and B are there to create a diamond effect on the Y-axis and X-axis respectively
			 * (Pretend it's not as stretched as the slashes make it look)
			 * Where a coordinate is written as (B,A)
			 * 
			 *           (0,2)
			 *          /\
			 *   (-2,0)/  \(2,0)
			 *         \  /
			 *          \/
			 *           (0,-2)
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
			
			builder.vertex(starX + projectedX, starY + heightProjectionY, starZ + projectedZ).color(starColor[0], starColor[1], starColor[2], alpha).endVertex();
		}
	}
}
