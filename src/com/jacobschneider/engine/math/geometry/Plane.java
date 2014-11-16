package com.jacobschneider.engine.math.geometry;

import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.math.vectorcalc.AbstractManifold;

public class Plane extends AbstractManifold {
	private final Vector3 normal;
	private final Vector3 point;
	
	public Plane(Vector3 point, Vector3 normal) {
		this.point = point;
		this.normal = normal;
	}

	@Override
	public Vector3 mapToManifold(Vector3 worldPoint) {
		Vector3 r = worldPoint.subtract(point);
		return point.add(r.projectToPlane(normal));
	}

	@Override
	public Vector3 projectToManifold(Vector3 surfacePoint, Vector3 vect) {
		return vect.projectToPlane(normal);
	}

	@Override
	public Vector3 perpVect(Vector3 pointOnSurface) {
		return normal;
	}


}
