package com.jacobschneider.engine.math.geometry;

import com.jacobschneider.engine.math.Vector3;
import com.jacobschneider.engine.math.vectorcalc.AbstractManifold;
import com.jacobschneider.engine.math.vectorcalc.ScalerField;

public class ScalerFieldManifold extends AbstractManifold {	
	private final ScalerField s;
	private final double potential;
	
	/**
	 * Creates a {@link Manifold} from a {@link ScalerField} and a potential value
	 * of the {@link ScalerField}
	 * @param s the scaler field
	 * @param potential the potential value to create the manifold at
	 */
	public ScalerFieldManifold(ScalerField s, double potential) {
		this.s = s;
		this.potential = potential;
	}

	@Override
	public Vector3 mapToManifold(Vector3 point) {
		return s.gradientTraversal(point, potential);
	}

	@Override
	public Vector3 projectToManifold(Vector3 surfacePoint, Vector3 vect) {
		return vect.projectToPlane(perpVect(surfacePoint));
	}

	@Override
	public Vector3 perpVect(Vector3 pointOnSurface) {
		return s.gradient(pointOnSurface);
	}
	
	@Override
	public boolean isOnManifold(Vector3 point) {
		return (s.getValue(point) == potential);
	}


}
