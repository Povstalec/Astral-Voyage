package net.povstalec.astralvoyage.client.render.level;

import java.util.List;

import net.povstalec.astralvoyage.common.datapack.ClientSpaceObject;
import net.povstalec.astralvoyage.common.network.packets.TextureLayerData;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public final class SpaceObjectRenderer
{
	private static final float DISTANCE = 100F;
	private static final float SIZE = 100F;
	
	private static void renderSurfaceLayer(BufferBuilder bufferbuilder, Matrix4f lastMatrix, float size, float distance, Pair<ResourceLocation, Pair<List<Integer>, Boolean>> layer, Vector3f shipToObject, float rotation)
	{
		ResourceLocation texture = layer.getFirst();
		int[] rgba = layer.getSecond().getFirst().stream().mapToInt((integer) -> integer).toArray();
		boolean blend = layer.getSecond().getSecond();

		Vector3f sphericalPos = new Vector3f((float) Math.sqrt(shipToObject.x*shipToObject.x + shipToObject.y*shipToObject.y + shipToObject.z*shipToObject.z), (float) Math.atan2(shipToObject.x, shipToObject.z), (float) Math.atan2(Math.sqrt(shipToObject.x*shipToObject.x + shipToObject.z*shipToObject.z), shipToObject.y));
		float objectRenderSize = Math.max((size/distance)*SIZE, 0.1F);

		float[] corner00 = placeOnSphere(-objectRenderSize, -objectRenderSize, sphericalPos, rotation);
		float[] corner10 = placeOnSphere(objectRenderSize, -objectRenderSize, sphericalPos, rotation);
		float[] corner11 = placeOnSphere(objectRenderSize, objectRenderSize,  sphericalPos, rotation);
		float[] corner01 = placeOnSphere(-objectRenderSize, objectRenderSize, sphericalPos, rotation);


		if(rgba.length < 4)
			rgba = new int[] {255, 255, 255, 255};

        RenderSystem.setShaderColor((float) rgba[0] / 255F, (float) rgba[1] / 255F, (float) rgba[2] / 255F, (float) rgba[3] / 255F);
		
		if(!blend)
			RenderSystem.disableBlend();
		
		RenderSystem.setShaderTexture(0, texture);
		
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(lastMatrix, corner00[0], corner00[1], corner00[2]).uv(0, 0).endVertex();
        bufferbuilder.vertex(lastMatrix, corner10[0], corner10[1], corner10[2]).uv(1, 0).endVertex();
        bufferbuilder.vertex(lastMatrix, corner11[0], corner11[1], corner11[2]).uv(1, 1).endVertex();
        bufferbuilder.vertex(lastMatrix, corner01[0], corner01[1], corner01[2]).uv(0, 1).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        
        if(!blend)
        	RenderSystem.enableBlend();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	public static void renderSurface(BufferBuilder bufferbuilder, Matrix4f lastMatrix, ClientSpaceObject spaceObject, float distance, Vector3f shipToObject, float rotation)
	{
		List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers = TextureLayerData.toPairList(spaceObject.getTextureLayers());

		textureLayers.forEach(layer -> renderSurfaceLayer(bufferbuilder, lastMatrix, spaceObject.getSize(), distance, layer, shipToObject, rotation));
	}

	public static float[] placeOnSphere(float offsetX, float offsetY, Vector3f sphericalPos, double rotation) {
		double x = sphericalPos.x*Math.sin(sphericalPos.z)*Math.cos(sphericalPos.y);
		double y = sphericalPos.x*Math.cos(sphericalPos.z);
		double z = sphericalPos.x*Math.sin(sphericalPos.z)*Math.cos(sphericalPos.y);

		double polarR = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
		double polarPhi = Math.atan2(offsetY, offsetX);
		polarPhi += rotation;

		double polarX = polarR * Math.cos(polarPhi);
		double polarY = polarR * Math.sin(polarPhi);

		x += - polarY * Math.cos(sphericalPos.z) * Math.sin(sphericalPos.y) - polarX * Math.cos(sphericalPos.y);
		y += polarY * Math.sin(sphericalPos.z);
		z += - polarY * Math.cos(sphericalPos.z) * Math.cos(sphericalPos.y) + polarX * Math.sin(sphericalPos.y);

		return new float[]{(float) x, (float) y, (float) ((float) z)};
	}
}
