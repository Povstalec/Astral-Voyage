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
import net.povstalec.astralvoyage.common.network.packets.TextureLayerData;
import net.povstalec.astralvoyage.common.util.SphericalCoords;

public final class SpaceObjectRenderer
{
	private static final float DISTANCE = 100F;
	private static final float SIZE = 100F;
	
	private static void renderSurfaceLayer(BufferBuilder bufferbuilder, Matrix4f lastMatrix, float size, float distance, Pair<ResourceLocation, Pair<List<Integer>, Boolean>> layer, Vector3f shipToObject, Vector3f galShipToObject, double offset, float rotation)
	{
		ResourceLocation texture = layer.getFirst();
		int[] rgba = layer.getSecond().getFirst().stream().mapToInt((integer) -> integer).toArray();
		boolean blend = layer.getSecond().getSecond();

		SphericalCoords sphericalCoords = new SphericalCoords(shipToObject);
		float objectRenderSize = Math.min(Math.max((size/distance) * SIZE * 6, galShipToObject.length() > 0.1 ? 0.1F : 0.5F), 360F);
		if(galShipToObject.length() > 0.1)
		{
			objectRenderSize = 0.5F;
			sphericalCoords = new SphericalCoords(galShipToObject);
		}

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
		//float postSize = Float.compare(galShipToObject.length(), 0f) == 0 ? spaceObject.size : 1;

		textureLayers.forEach(layer -> renderSurfaceLayer(bufferbuilder, lastMatrix, spaceObject.size, distance, layer, shipToObject, galShipToObject, spaceObject.getOrbitOffset().orElse(0D), rotation));
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

	//TODO A size calculation function which accounts for planets and stars
	//Accounting for it being in galactic coordinate space(stars) and solar(planets/moons) as those need different minimal sizes
	public float fakeSize(float realSize, float solarDistance, float galacticDistance)
	{
		return 1F;
	}
}
