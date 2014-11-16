package com.jacobschneider.engine.math.geometry;

import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.math.vectorcalc.AbstractManifold;

public class Line extends AbstractManifold  {
	private final Vector3 point;
	private final Vector3 dir;
	private final Vector3 perp;
	
	public Line(Vector3 point, Vector3 dir) {
		this.point = point;
		this.dir = dir;
		this.perp = dir.randomPerp();
	}

	@Override
	public Vector3 mapToManifold(Vector3 worldPoint) {
		Vector3 r = worldPoint.subtract(point);
		return point.add(r.projectToVector(dir));
	}

	@Override
	public Vector3 projectToManifold(Vector3 surfacePoint, Vector3 vect) {
		if (!isOnManifold(surfacePoint)) {
			throw new IllegalArgumentException("surfacePoint must be on the surface of the manifold");
		}
		return vect.projectToVector(dir);		
	}

	@Override
	public Vector3 perpVect(Vector3 pointOnSurface) {
		return perp;
	}



}
