package net.povstalec.astralvoyage.client.render.level;

import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraft.util.RandomSource;
import net.povstalec.stellarview.api.celestials.Star;

/**
 * Compatibility for Stellar View rendering
 */
public class StellarViewRendering
{
	public static void createStar(BufferBuilder builder, RandomSource randomsource, 
			double x, double y, double z, double distance, double heightDeformation, double widthDeformation, long seed)
	{
		Star.createStar(builder, randomsource, x, y, z, distance, seed);
	}
}
