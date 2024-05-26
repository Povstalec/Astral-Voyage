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
		float objectRenderSize = Math.min(Math.max((size/distance)*SIZE*6, 0.1F), 360F);
		
		sphericalPos.x = DISTANCE;
		Vector3f corner00 = placeOnSphere(-objectRenderSize, -objectRenderSize, sphericalPos, rotation);
		Vector3f corner10 = placeOnSphere(objectRenderSize, -objectRenderSize, sphericalPos, rotation);
		Vector3f corner11 = placeOnSphere(objectRenderSize, objectRenderSize,  sphericalPos, rotation);
		Vector3f corner01 = placeOnSphere(-objectRenderSize, objectRenderSize, sphericalPos, rotation);


		if(rgba.length < 4)
			rgba = new int[] {255, 255, 255, 255};

        RenderSystem.setShaderColor((float) rgba[0] / 255F, (float) rgba[1] / 255F, (float) rgba[2] / 255F, (float) rgba[3] / 255F);
		
		if(!blend)
			RenderSystem.disableBlend();
		
		RenderSystem.setShaderTexture(0, texture);
		
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(lastMatrix, corner00.x, corner00.y, corner00.z).uv(0, 0).endVertex();
        bufferbuilder.vertex(lastMatrix, corner10.x, corner10.y, corner10.z).uv(1, 0).endVertex();
        bufferbuilder.vertex(lastMatrix, corner11.x, corner11.y, corner11.z).uv(1, 1).endVertex();
        bufferbuilder.vertex(lastMatrix, corner01.x, corner01.y, corner01.z).uv(0, 1).endVertex();
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

	public static Vector3f placeOnSphere(float offsetX, float offsetY, Vector3f sphericalPos, double rotation) {
		Vector3f cartesianCoords = sphericalToCartesian(sphericalPos);

		double polarR = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
		double polarPhi = Math.atan2(offsetY, offsetX);
		polarPhi += rotation;

		double polarX = polarR * Math.cos(polarPhi);
		double polarY = polarR * Math.sin(polarPhi);

		cartesianCoords.x += - polarY * Math.cos(sphericalPos.z) * Math.sin(sphericalPos.y) - polarX * Math.cos(sphericalPos.y);
		cartesianCoords.y += polarY * Math.sin(sphericalPos.z);
		cartesianCoords.z += - polarY * Math.cos(sphericalPos.z) * Math.cos(sphericalPos.y) + polarX * Math.sin(sphericalPos.y);

		return cartesianCoords;
	}
	
	
	
	public static double cartesianX(Vector3f sphericalCoords)
	{
		return sphericalCoords.x * Math.sin(sphericalCoords.z) * Math.sin(sphericalCoords.y);
	}
	
	public static double cartesianY(Vector3f sphericalCoords)
	{
		return sphericalCoords.x * Math.cos(sphericalCoords.z);
	}
	
	public static double cartesianZ(Vector3f sphericalCoords)
	{
		return sphericalCoords.x * Math.sin(sphericalCoords.z) * Math.cos(sphericalCoords.y);
	}
	
	public static Vector3f sphericalToCartesian(Vector3f sphericalCoords)
	{
		return new Vector3f((float) cartesianX(sphericalCoords), (float) cartesianY(sphericalCoords), (float) cartesianZ(sphericalCoords));
	}

	public static Vector3f vectorBodyToBody(Vector3f bodyA, Vector3f bodyB)
	{
		return new Vector3f(bodyA.x-bodyB.x, bodyA.y-bodyB.y, bodyA.z-bodyB.z);
	}
}
