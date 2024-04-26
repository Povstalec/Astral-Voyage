package net.povstalec.astralvoyage.client.render.level;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.resources.ResourceLocation;

public class CelestialRenderer
{
    private static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
	
	public static float[] moveSpherical(float offsetX, float offsetY, float r, double theta, double phi)
	{
		double x = r * Math.sin(phi) * Math.sin(theta);
		double y = r * Math.cos(phi);
		double z = r * Math.sin(phi) * Math.cos(theta);
		
		x += - offsetY * Math.cos(phi) * Math.sin(theta) - offsetX * Math.cos(theta);
		y += offsetY * Math.sin(phi);
		z += - offsetY * Math.cos(phi) * Math.cos(theta) + offsetX * Math.sin(theta);
		
		return new float[] {(float) x, (float) y, (float) z};
	}
	
    public static void createCelestialObject(BufferBuilder bufferbuilder, Matrix4f lastMatrix, ResourceLocation location,
			float size, float distance, float[] uv)
	{
		createCelestialObject(bufferbuilder, lastMatrix, location, size, distance, 0.0F, 0.0F, uv);
	}
	
	public static void createCelestialObject(BufferBuilder bufferbuilder, Matrix4f lastMatrix, ResourceLocation location,
			float size, float distance, float theta, float phi, float[] uv)
	{
		float[] u0v0 = moveSpherical(-size, -size, distance, theta, phi);
		float[] u1v0 = moveSpherical(size, -size, distance, theta, phi);
		float[] u1v1 = moveSpherical(size, size, distance, theta, phi);
		float[] u0v1 = moveSpherical(-size, size, distance, theta, phi);
		
		RenderSystem.setShaderTexture(0, location);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(lastMatrix, u0v0[0], u0v0[1], u0v0[2]).uv(uv[0], uv[1]).endVertex();
        bufferbuilder.vertex(lastMatrix, u1v0[0], u1v0[1], u1v0[2]).uv(uv[2], uv[1]).endVertex();
        bufferbuilder.vertex(lastMatrix, u1v1[0], u1v1[1], u1v1[2]).uv(uv[2], uv[3]).endVertex();
        bufferbuilder.vertex(lastMatrix, u0v1[0], u0v1[1], u0v1[2]).uv(uv[0], uv[3]).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
	}
    
	protected static void renderSun(BufferBuilder bufferbuilder, Matrix4f lastMatrix, float size)
	{
		createCelestialObject(bufferbuilder, lastMatrix, SUN_LOCATION, 
				size, 100.0F, new float[] {0.0F, 0.0F, 1.0F, 1.0F});

		RenderSystem.enableBlend();
	}
}
