package com.jacobschneider.engine.math.boundingvolumes;

import com.jacobschneider.engine.framework.BoundVolume;
import com.jacobschneider.engine.math.Quaternion;
import com.jacobschneider.engine.math.Vector3;

public class BoundSphere extends BoundVolume {
	private Vector3 c; // center
	private double r; // radius
	
	public BoundSphere(Vector3 c, double r) {
		this.c = c;
		this.r = r;
	}
	
	Vector3 getCenter() {
		return c;
	}
	
	double getRadius() {
		return r;
	}

	@Override
	public void updateState(Vector3 x, Quaternion q) {
		this.c = x;
		
	}
}
