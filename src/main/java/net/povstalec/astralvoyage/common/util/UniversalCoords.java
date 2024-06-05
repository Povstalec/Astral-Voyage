package net.povstalec.astralvoyage.common.util;

import org.joml.Vector3d;
import org.joml.Vector3f;

public class UniversalCoords
{
	public static final double LY_TO_KM = 9_460_730_472_581.2;
	public static final double MAX_KM_VALUE = LY_TO_KM / 2;
	
	public static final double LIGHT_SPEED = 299_792.458;
	
	// Coordinates in light years
	public long majorX;
	public long majorY;
	public long majorZ;
	
	// Coordinates in kilometers
	public double minorX;
	public double minorY;
	public double minorZ;
	
	public UniversalCoords(long majorX, long majorY, long majorZ, double minorX, double minorY, double minorZ)
	{
		this.majorX = majorX;
		this.majorY = majorY;
		this.majorZ = majorZ;
		
		if(minorX > MAX_KM_VALUE || minorY > MAX_KM_VALUE || minorZ > MAX_KM_VALUE)
			throw(new IllegalArgumentException("No Minor Coordinate value may be more than 4 730 365 236 290.6"));
		
		this.minorX = minorX;
		this.minorY = minorY;
		this.minorZ = minorZ;
	}
	
	public UniversalCoords(long majorX, long majorY, long majorZ)
	{
		this(majorX, majorY, majorZ, 0, 0, 0);
	}
	
	//============================================================================================
	//************************************Relative coordinates************************************
	//============================================================================================
	
	/**
	 * @param other The other coordinates that are compared to these coordinates
	 * @return Returns true if the other coordinates are within a reasonable distance (max 1 coordinate step away), otherwise false
	 */
	public boolean isClose(UniversalCoords other)
	{
		return Math.abs(majorDistanceSquared(other)) <= 1;
	}
	
	/**
	 * @param other The other coordinates that are compared to these coordinates
	 * @return Returns squared distance between two major coordinate values (mainly for use in checks, since square root operation is costly)
	 */
	public long majorDistanceSquared(UniversalCoords other)
	{
		return (this.majorX - other.majorX) * (this.majorY - other.majorY) * (this.majorZ - other.majorZ);
	}
	
	/**
	 * @param other The other coordinates that are compared to these coordinates
	 * @return Returns distance between two major coordinate values
	 */
	public double majorDistance(UniversalCoords other)
	{
		return Math.sqrt(majorDistanceSquared(other));
	}
	
	/**
	 * @param other The other coordinates that are compared to these coordinates
	 * @return  squared distance between two minor coordinate values (mainly for use in checks, since square root operation is costly)
	 */
	public double minorDistanceSquared(UniversalCoords other)
	{
		if(this.majorX == other.majorX && this.majorY == other.majorY && this.majorZ == other.majorZ)
			return (this.minorX - other.minorX) * (this.minorY - other.minorY) * (this.minorZ - other.minorZ);
		else if(majorDistanceSquared(other) > 1)
			throw new IllegalArgumentException("Other coordinates are too far away to calculate minor distance");

		double relativeOtherMinorX = (this.majorX - other.majorX) * LY_TO_KM + other.minorX;
		double relativeOtherMinorY = (this.majorY - other.majorY) * LY_TO_KM + other.minorY;
		double relativeOtherMinorZ = (this.majorZ - other.majorZ) * LY_TO_KM + other.minorZ;
		
		return new SphericalCoords(new Vector3d(this.minorX - relativeOtherMinorX, this.minorY - relativeOtherMinorY, this.minorZ - relativeOtherMinorZ)).r;
	}
	
	/**
	 * @param other The other coordinates that are compared to these coordinates
	 * @return Returns distance between two minor coordinate values
	 */
	public double minorDistance(UniversalCoords other)
	{
		return Math.sqrt(minorDistanceSquared(other));
	}
	
	/**
	 * @param other The other coordinates that are compared to these coordinates
	 * @param r The radius of the sphere onto which the sky position is projected
	 * @return Returns the sky position at which the other coordinates would appear on the sky when viewed from the coordinates of this
	 */
	public SphericalCoords skyPosition(UniversalCoords other, float radius)
	{
		SphericalCoords skyPosition;
		
		if(!isClose(other))
			skyPosition = new SphericalCoords(new Vector3f(this.majorX - other.majorX, this.majorY - other.majorY, this.majorZ - other.majorZ));
		else
		{
			double relativeOtherMinorX = (this.majorX - other.majorX) * LY_TO_KM + other.minorX;
			double relativeOtherMinorY = (this.majorY - other.majorY) * LY_TO_KM + other.minorY;
			double relativeOtherMinorZ = (this.majorZ - other.majorZ) * LY_TO_KM + other.minorZ;
			
			skyPosition = new SphericalCoords(new Vector3d(this.minorX - relativeOtherMinorX, this.minorY - relativeOtherMinorY, this.minorZ - relativeOtherMinorZ));
		}
		
		skyPosition.r = radius;
		
		return skyPosition;
	}
	
	//============================================================================================
	//*******************************************Motion*******************************************
	//============================================================================================
	
	public void moveMajor(long x, long y, long z)
	{
		this.majorX += x;
		this.majorY += y;
		this.majorZ += z;
	}
	
	public void moveMinor(double x, double y, double z)
	{
		if(x > LY_TO_KM || y > LY_TO_KM || z > LY_TO_KM)
			throw(new IllegalArgumentException("No Minor Motion Coordinate value may be more than 9 460 730 472 581.2"));
		
		this.minorX += x;
		this.minorY += y;
		this.minorZ += z;
		
		if(this.minorX > MAX_KM_VALUE)
		{
			this.minorX -= MAX_KM_VALUE;
			this.majorX += 1;
		}
		else if(this.minorX < -MAX_KM_VALUE)
		{
			this.minorX += MAX_KM_VALUE;
			this.majorX -= 1;
		}
		
		if(this.minorY > MAX_KM_VALUE)
		{
			this.minorY -= MAX_KM_VALUE;
			this.majorY += 1;
		}
		else if(this.minorY < -MAX_KM_VALUE)
		{
			this.minorY += MAX_KM_VALUE;
			this.majorY -= 1;
		}
		
		if(this.minorZ > MAX_KM_VALUE)
		{
			this.minorZ -= MAX_KM_VALUE;
			this.majorZ += 1;
		}
		else if(this.minorZ < -MAX_KM_VALUE)
		{
			this.minorZ += MAX_KM_VALUE;
			this.majorZ -= 1;
		}
	}
	
	public void setMajor(long x, long y, long z)
	{
		this.majorX = x;
		this.majorY = y;
		this.majorZ = z;
	}
	
	public void setMinor(long x, long y, long z)
	{
		if(y > MAX_KM_VALUE || y > MAX_KM_VALUE || z > MAX_KM_VALUE)
			throw(new IllegalArgumentException("No Minor Coordinate value may be more than 4 730 365 236 290.6"));
		
		this.minorX = x;
		this.minorY = y;
		this.minorZ = z;
	}
}
