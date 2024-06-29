package net.povstalec.astralvoyage.client.render.level;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.astralvoyage.common.datapack.ClientSpaceObject;
import net.povstalec.astralvoyage.common.util.TextureLayerData;
import net.povstalec.astralvoyage.common.util.SphericalCoords;

public final class SpaceObjectRenderer
{
	private static final float DISTANCE = 100F;
	private static final float MIN_SIZE = 0.5F;
	private static final float MAX_SIZE = 360F;
	private static final float INTERSTELLAR_MIN_SIZE = 0.1F;
	private static final float INTERSTELLAR_MAX_SIZE = 0.7F;

	
	private static void renderSurfaceLayer(BufferBuilder bufferbuilder, Matrix4f lastMatrix, float size, float distance, Pair<ResourceLocation, Pair<List<Integer>, Boolean>> layer, Vector3f shipToObject, Vector3f galShipToObject, double offset, float rotation)
	{
		ResourceLocation texture = layer.getFirst();
		int[] rgba = layer.getSecond().getFirst().stream().mapToInt((integer) -> integer).toArray();
		boolean blend = layer.getSecond().getSecond();
		galShipToObject = new Vector3f(-galShipToObject.x, -galShipToObject.y, -galShipToObject.z);

		SphericalCoords sphericalCoords = new SphericalCoords(shipToObject);
		float objectRenderSize = fakeSize(size, distance, galShipToObject.length());

		if(galShipToObject.length() > 0.1)
			sphericalCoords = new SphericalCoords(galShipToObject);


		sphericalCoords.r = DISTANCE;
		Vector3f corner00 = placeOnSphere(-objectRenderSize, -objectRenderSize, sphericalCoords, offset, rotation);
		Vector3f corner10 = placeOnSphere(objectRenderSize, -objectRenderSize, sphericalCoords, offset, rotation);
		Vector3f corner11 = placeOnSphere(objectRenderSize, objectRenderSize,  sphericalCoords, offset, rotation);
		Vector3f corner01 = placeOnSphere(-objectRenderSize, objectRenderSize, sphericalCoords, offset, rotation);


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
	
	public static void renderSurface(BufferBuilder bufferbuilder, Matrix4f lastMatrix, ClientSpaceObject spaceObject, float distance, Vector3f galShipToObject, Vector3f shipToObject, float rotation)
	{
		List<Pair<ResourceLocation, Pair<List<Integer>, Boolean>>> textureLayers = TextureLayerData.toPairList(spaceObject.getTextureLayers());

		textureLayers.forEach(layer -> renderSurfaceLayer(bufferbuilder, lastMatrix, spaceObject.getSize(), distance, layer, shipToObject, galShipToObject, spaceObject.getOrbitOffset().orElse(0D), rotation));
	}

	public static Vector3f placeOnSphere(float offsetX, float offsetY, SphericalCoords sphericalCoords, double orbitOffset, double rotation) {
		Vector3f cartesianCoords = sphericalCoords.toCartesianF();

		double polarR = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
		double polarPhi = Math.atan2(offsetY, offsetX);
		polarPhi += rotation;

		double polarX = polarR * Math.cos(polarPhi);
		double polarY = polarR * Math.sin(polarPhi);

		cartesianCoords.x += - polarY * Math.cos(sphericalCoords.phi) * Math.sin(sphericalCoords.theta) - polarX * Math.cos(sphericalCoords.theta);
		cartesianCoords.y += polarY * Math.sin(sphericalCoords.phi);
		cartesianCoords.z += - polarY * Math.cos(sphericalCoords.phi) * Math.cos(sphericalCoords.theta) + polarX * Math.sin(sphericalCoords.theta) + Math.toRadians(orbitOffset);

		return cartesianCoords;
	}

	public static Vector3f vectorBodyToBody(Vector3f bodyA, Vector3f bodyB)
	{
		return new Vector3f(bodyA.x-bodyB.x, bodyA.y-bodyB.y, bodyA.z-bodyB.z);
	}

	//TODO This is still kinda bad, I think
	public static float fakeSize(float realSize, float solarDistance, float galacticDistance)
	{
		return Math.min(Math.max((realSize/solarDistance)*600, galacticDistance > 0.1 ? INTERSTELLAR_MIN_SIZE : MIN_SIZE),
				galacticDistance > 0.1 ? INTERSTELLAR_MAX_SIZE : MAX_SIZE);
	}
}
