package net.povstalec.astralvoyage.client.render.level;

import java.util.List;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.astralvoyage.common.datapack.SpaceObject;

public final class SpaceObjectRenderer
{
	private static final float DISTANCE = 100F;
	private static final float SIZE = 100F;
	
	private static void renderSurfaceLayer(BufferBuilder bufferbuilder, Matrix4f lastMatrix, Pair<ResourceLocation, Pair<List<Integer>, Boolean>> layer)
	{
		ResourceLocation texture = layer.getFirst();
		int[] rgba = layer.getSecond().getFirst().stream().mapToInt((integer) -> integer).toArray();
		boolean blend = layer.getSecond().getSecond();
		
		if(rgba.length < 4)
			rgba = new int[] {255, 255, 255, 255};

        RenderSystem.setShaderColor((float) rgba[0] / 255F, (float) rgba[1] / 255F, (float) rgba[2] / 255F, (float) rgba[3] / 255F);
		
		if(!blend)
			RenderSystem.disableBlend();
		
		RenderSystem.setShaderTexture(0, texture);
		
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(lastMatrix, -SIZE, DISTANCE, -SIZE).uv(0, 0).endVertex();
        bufferbuilder.vertex(lastMatrix, SIZE, DISTANCE, -SIZE).uv(1, 0).endVertex();
        bufferbuilder.vertex(lastMatrix, SIZE, DISTANCE, SIZE).uv(1, 1).endVertex();
        bufferbuilder.vertex(lastMatrix, -SIZE, DISTANCE, SIZE).uv(0, 1).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        
        if(!blend)
        	RenderSystem.enableBlend();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	public static void renderSurface(BufferBuilder bufferbuilder, Matrix4f lastMatrix, SpaceObject spaceObject)
	{
		List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers = spaceObject.getTextureLayers();
		
		textureLayers.forEach(layer -> renderSurfaceLayer(bufferbuilder, lastMatrix, layer));
	}
}
