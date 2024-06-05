package net.povstalec.astralvoyage.common.util;

import org.joml.Vector3f;

public class SphericalCoords
{
	public float r;
	public float theta;
	public float phi;
	
	public SphericalCoords(float r, float theta, float phi)
	{
		this.r = r;
		this.theta = theta;
		this.phi = phi;
	}
	
	public SphericalCoords(Vector3f cartesianCoords)
	{
		this.r = (float) sphericalR(cartesianCoords);
		this.theta = (float) sphericalTheta(cartesianCoords);
		this.phi = (float) sphericalPhi(cartesianCoords);
	}
	
	public Vector3f toCartesian()
	{
		return new Vector3f((float) cartesianX(this), (float) cartesianY(this), (float) cartesianZ(this));
	}
	
	public static SphericalCoords cartesianToSpherical(Vector3f cartesianCoordinates)
	{
		return new SphericalCoords(cartesianCoordinates);
	}
	
	
	
	public static double sphericalR(Vector3f cartesianCoords)
	{
		return Math.sqrt(cartesianCoords.x * cartesianCoords.x + cartesianCoords.y * cartesianCoords.y + cartesianCoords.z * cartesianCoords.z);
	}
	
	public static double sphericalTheta(Vector3f cartesianCoords)
	{
		return Math.atan2(cartesianCoords.x, cartesianCoords.z);
	}
	
	public static double sphericalPhi(Vector3f cartesianCoords)
	{
		double xzLength = Math.sqrt(cartesianCoords.x * cartesianCoords.x + cartesianCoords.z * cartesianCoords.z);
		return Math.atan2(xzLength, cartesianCoords.y);
	}
	
	
	
	public static double cartesianX(SphericalCoords sphericalCoords)
	{
		return sphericalCoords.r * Math.sin(sphericalCoords.phi) * Math.sin(sphericalCoords.theta);
	}
	
	public static double cartesianY(SphericalCoords sphericalCoords)
	{
		return sphericalCoords.r * Math.cos(sphericalCoords.phi);
	}
	
	public static double cartesianZ(SphericalCoords sphericalCoords)
	{
		return sphericalCoords.r * Math.sin(sphericalCoords.phi) * Math.cos(sphericalCoords.theta);
	}
	
	
	
	public static Vector3f sphericalToCartesian(SphericalCoords sphericalCoords)
	{
		return new Vector3f((float) cartesianX(sphericalCoords), (float) cartesianY(sphericalCoords), (float) cartesianZ(sphericalCoords));
	}
}
