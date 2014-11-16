package com.jacobschneider.engine.math.boundingvolumes;

import com.jacobschneider.engine.math.Quaternion;
import com.jacobschneider.engine.math.Vector3;


public class BoundCircle extends BoundVolume {
	private final double radius;
	private Vector3 normal;
	private Vector3 center;
	
	
	public BoundCircle(Vector3 normalDir, Vector3 centerPos, double planeRadius) {
		this.radius = planeRadius;
		this.normal = normalDir;
		this.center = centerPos;
	}	


	@Override
	public void updateState(Vector3 x, Quaternion q) {
		this.center = x;			
	}
	
	public void updateDir(Vector3 normal) {
		this.normal = normal;
	}

	public Vector3 getCenter() {
		return center;
	}

	public Vector3 getNormal() {
		return normal;
	}
	
	public double getRadius() {
		return radius;
	}
	
}
